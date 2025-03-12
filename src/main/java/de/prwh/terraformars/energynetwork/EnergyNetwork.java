package de.prwh.terraformars.energynetwork;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyConsumer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import de.prwh.terraformars.entity.blockentity.EnergyBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of a connected network of energy entities. <br>
 * <p>
 * DO NOT SAVE/CACHE THESE NETWORKS ELSEWHERE FOR MORE THAN THE CURRENT TICK.
 * Always get your network from the {@link #getNetwork} method, as the network
 * connections may change between two calls. <br>
 * <p>
 * Note that EnergyNetwork objects are like a snapshot, if you add or remove a
 * {@link EnergyBlockEntity} from them, the network object won't get updated and
 * is unsynchronized to further instances of the network. Also, noteworthy is the
 * fact that it's not guaranteed that the same EnergyNetwork object will be
 * returned at calls of different entities of said network. They are equal in
 * terms of content, but could be different objects. This is not the case right
 * now, but could change in the future.
 */
/*
 * TODO: The performance could be improved by implementing a rebuild algorithm
 * for add and remove and/or asynchronous rebuilding to reduce the performance
 * impact on big networks
 */
public class EnergyNetwork {
	/**
	 * Wrapper around EnergyBlockEntity to allow for persistence and more stable
	 * object handling.
	 */
	private static class EnergyBlockEntityWrapper implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		private final String dimensionKeyString; // Could be better to use some kind of uid
		private final int x, y, z;

		public EnergyBlockEntityWrapper(EnergyBlockEntity te) {
			this.dimensionKeyString = Objects.requireNonNull(te.getWorld()).getRegistryKey().getValue().toString();
			this.x = te.getPos().getX();
			this.y = te.getPos().getY();
			this.z = te.getPos().getZ();
		}

		public EnergyBlockEntity getTileEntity(MinecraftServer server) {
			RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimensionKeyString));
			return (EnergyBlockEntity) Objects.requireNonNull(server.getWorld(dimensionKey)).getBlockEntity(new BlockPos(x, y, z));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dimensionKeyString.hashCode();
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EnergyBlockEntityWrapper other = (EnergyBlockEntityWrapper) obj;
			if (!dimensionKeyString.equals(other.dimensionKeyString))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return z == other.z;
		}
	}

	private static Graph<EnergyBlockEntityWrapper, DefaultEdge> networkGraph = new SimpleGraph<>(DefaultEdge.class);
	private static final Map<EnergyBlockEntity, EnergyNetwork> cache = new HashMap<>();
	private static File saveFile;

	private static final int[][] searchOffsets = new int[][] {
			new int[] { 1, 0, 0 },
			new int[] { 0, 1, 0 },
			new int[] { 0, 0, 1 },
			new int[] { -1, 0, 0 },
			new int[] { 0, -1, 0 },
			new int[] { 0, 0, -1 }
	};

	private final List<EnergyBlockEntity> entities = new ArrayList<>();
	private final List<IEnergyConsumer> consumer = new ArrayList<>();
	private final List<IEnergyProducer> producer = new ArrayList<>();

	private EnergyNetwork() {
	}

	private void addEntity(EnergyBlockEntity entity) {
		entities.add(entity);
		if (entity instanceof IEnergyConsumer) {
			consumer.add((IEnergyConsumer) entity);
		}
		if (entity instanceof IEnergyProducer) {
			producer.add((IEnergyProducer) entity);
		}
	}

	public List<EnergyBlockEntity> getEntities() {
		return entities;
	}

	public List<IEnergyConsumer> getConsumer() {
		return consumer;
	}

	public List<IEnergyProducer> getProducer() {
		return producer;
	}


	public static void onEntityAdd(EnergyBlockEntity te) {
		EnergyBlockEntityWrapper entityWrapper = new EnergyBlockEntityWrapper(te);
		networkGraph.addVertex(entityWrapper);

		Set<EnergyNetwork> adjacentNetworks = new HashSet<>();

		for (int[] offset : searchOffsets) {
			BlockEntity other = Objects.requireNonNull(te.getWorld()).getBlockEntity(new BlockPos(te.getPos().getX() + offset[0], te.getPos().getY() + offset[1], te.getPos().getZ() + offset[2]));
			if (!(other instanceof EnergyBlockEntity))
				continue;

			EnergyBlockEntityWrapper entityWrapperOther = new EnergyBlockEntityWrapper((EnergyBlockEntity) other);
			networkGraph.addEdge(entityWrapper, entityWrapperOther);

			// Collect the network of the adjacent block
			EnergyNetwork neighborNetwork = getNetwork((EnergyBlockEntity) other);
			if (neighborNetwork != null) {
				adjacentNetworks.add(neighborNetwork);
			}
		}

		// Merge all found networks into a single network
		EnergyNetwork mergedNetwork;
		if (adjacentNetworks.isEmpty()) {
			mergedNetwork = new EnergyNetwork(); // Create new network if no neighbors
		} else {
			mergedNetwork = adjacentNetworks.iterator().next(); // Use one existing network
			for (EnergyNetwork network : adjacentNetworks) {
				if (network != mergedNetwork) {
					mergedNetwork.merge(network); // Merge other networks into the first one
				}
			}
		}

		// Add the new entity to the merged network
		mergedNetwork.addEntity(te);
		cache.put(te, mergedNetwork);
		//cache.clear();

		// Notify all entities in the network
		for (EnergyBlockEntity energyBlockEntity : mergedNetwork.getEntities()) {
			energyBlockEntity.onNetworkEntityApplied(mergedNetwork, te);
		}
	}

	public void merge(EnergyNetwork other) {
		if (other == this) return; // Prevent self-merge

		// Move all entities from 'other' to 'this'
		for (EnergyBlockEntity entity : other.getEntities()) {
			this.addEntity(entity);
			entity.onNetworkEntityApplied(this, entity); // Notify block of new network
		}

		// Remove all block entities of 'other' from cache before merging
		other.getEntities().forEach(EnergyNetwork.cache::remove);

		// Ensure 'other' is completely removed
		EnergyNetwork.cache.values().removeIf(network -> network == other);
	}

//	public static void onEntityAdd(EnergyBlockEntity te) {
//		EnergyBlockEntityWrapper entityWrapper = new EnergyBlockEntityWrapper(te);
//		networkGraph.addVertex(entityWrapper);
//		for (int[] offset : searchOffsets) {
//			BlockEntity other = te.getWorld().getBlockEntity(new BlockPos(te.getPos().getX() + offset[0], te.getPos().getY() + offset[1], te.getPos().getZ() + offset[2]));
//			if (!(other instanceof EnergyBlockEntity))
//				continue;
//
//			EnergyBlockEntityWrapper entityWrapperOther = new EnergyBlockEntityWrapper((EnergyBlockEntity) other);
//			networkGraph.addEdge(entityWrapper, entityWrapperOther);
//		}
//		cache.clear();
//
//		EnergyNetwork network = getNetwork(te);
//		if (network == null) {
//			// Handle the case where no network is found (e.g., log an error or return early)
//			return;
//		}
//        if (!network.entities.isEmpty()) {
//			for (EnergyBlockEntity energyBlockEntity : network.getEntities()) {
//				energyBlockEntity.onNetworkEntityApplied(network, te);
//			}
//		}
//	}



	public static void onEntityRemove(EnergyBlockEntity te) {
		networkGraph.removeVertex(new EnergyBlockEntityWrapper(te));
		cache.clear();
	}

	public static EnergyNetwork getNetwork(EnergyBlockEntity energyBlockEntity) {

		MinecraftServer server = Objects.requireNonNull(energyBlockEntity.getWorld()).getServer();
		if (server == null) {
			return null; // Server is unavailable
		}

		// This is definitely faster than a graph traversal
		EnergyNetwork cen = cache.get(energyBlockEntity);
		if (cen != null) {
			return cen;
		}
		// This should still be faster than a graph traversal
		for (EnergyNetwork network : EnergyNetwork.cache.values()) {
			if (network.entities.contains(energyBlockEntity)) {
				cache.put(energyBlockEntity, network);
				return network;
			}
		}

		EnergyBlockEntityWrapper entityWrapper = new EnergyBlockEntityWrapper(energyBlockEntity);
		if (!networkGraph.containsVertex(entityWrapper)) {
			TerraForMars.LOGGER.error("EnergyBlockEntityWrapper not found in networkGraph for entity: {}", energyBlockEntity);
			return new EnergyNetwork();
		}

		TerraForMars.LOGGER.info("Starting to search for network in networkGraph.");
		ClosestFirstIterator<EnergyBlockEntityWrapper, DefaultEdge> iterator = new ClosestFirstIterator<>(networkGraph, entityWrapper);
		EnergyNetwork network = new EnergyNetwork();

		while (iterator.hasNext()) {
 			EnergyBlockEntityWrapper member = iterator.next();
			EnergyBlockEntity memberEntity = member.getTileEntity(server);
			network.addEntity(memberEntity);
			if (!memberEntity.equals(energyBlockEntity)) {
				cache.put(memberEntity, network);
				TerraForMars.LOGGER.info("Reconstructing network cache from networkgraph: {}", memberEntity);
			}
		}
		cache.put(energyBlockEntity, network);
		return network;
	}

	public static void setSaveFile(File file) {
		saveFile = file;
	}

	@SuppressWarnings("unchecked")
	public static void readFromFile() {
		// Reset graph in case of loading another "world set" without reloading
		// this class
		networkGraph = new SimpleGraph<>(DefaultEdge.class);
		cache.clear();

		if (saveFile == null || !saveFile.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
			TerraForMars.LOGGER.info("Trying to parse data");
			networkGraph = (Graph<EnergyBlockEntityWrapper, DefaultEdge>) ois.readObject();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			throw new RuntimeException("Could not load EnergyNetwork graph from file", e);
		}
	}

	public static void saveToFile() {
		TerraForMars.LOGGER.info("Saving all EnergyNetworks in Dimensions");
		if (saveFile == null)
			throw new IllegalStateException("File must be specified before saving");

		try {
			if (!saveFile.exists()) {
				boolean result = saveFile.createNewFile();
				TerraForMars.LOGGER.debug("Save file success: {}",result);
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
				oos.writeObject(networkGraph);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not save EnergyNetwork graph to file", e);
		}
	}

	@Override
	public String toString() {
		return "EnergyNetwork@" + this.hashCode() + "{\n" +
				"  Entities (" + entities.size() + "):\n" + formatList(entities) + "\n" +
				"  Consumer (" + consumer.size() + "):\n" + formatList(consumer) + "\n" +
				"  Producer (" + producer.size() + "):\n" + formatList(producer) + "\n" +
				"  Cache (\n" + formatCache() + "  )\n}";
	}

	// Helper method to format lists with new lines and indentation
	private String formatList(List<?> list) {
		return list.stream()
				.map(obj -> "    { " + obj.getClass().getSimpleName() + "@" +
						Integer.toHexString(System.identityHashCode(obj)) + " }")
				.reduce((a, b) -> a + "\n" + b)
				.orElse("    Empty");
	}

	// Helper method to format cache with new lines and indentation
	private String formatCache() {
		// Group the entries by NetworkHash
		Map<Integer, List<String>> groupedCache = cache.entrySet().stream()
				.sorted((entry1, entry2) -> Integer.compare(entry1.getValue().hashCode(), entry2.getValue().hashCode())) // Sort by NetworkHash
				.collect(Collectors.groupingBy(
						entry -> entry.getValue().hashCode(),
						LinkedHashMap::new, // Maintain insertion order
						Collectors.mapping(entry -> "        { BlockEntity: " + entry.getKey().getClass().getSimpleName() + "@" +
								Integer.toHexString(System.identityHashCode(entry.getKey())) +
								", NetworkHash: " + entry.getValue().hashCode() + " }", Collectors.toList())
				));

		// Format grouped cache with [] around BlockEntities with the same NetworkHash
		StringBuilder sb = new StringBuilder();
		groupedCache.forEach((networkHash, entries) -> {
			sb.append("    [\n");
			entries.forEach(entry -> sb.append(entry).append("\n"));
			sb.append("    ]\n");
		});

		return sb.toString().isEmpty() ? "    Empty" : sb.toString();
	}
}
