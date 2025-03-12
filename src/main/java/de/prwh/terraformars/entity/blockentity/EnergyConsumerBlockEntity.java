package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.EnergyNetwork;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyConsumer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public abstract class EnergyConsumerBlockEntity extends EnergyBlockEntity implements IEnergyConsumer {

	public EnergyConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
	}

	@Override
	public int getEnergyStored() {
		return values.get(IEnergyConsumer.ENERGY_STORED_KEY);
	}

	@Override
	public int getEnergyStoredMax() {
		return values.get(IEnergyConsumer.ENERGY_STORED_MAX_KEY);
	}

	@Override
	public int getEnergyConsume() {
		return values.get(IEnergyConsumer.ENERGY_CONSUME_KEY);
	}

	@Override
	public void setEnergyStored(int energy) {
		values.put(IEnergyConsumer.ENERGY_STORED_KEY, energy);
	}

	@Override
	public void setEnergyStoredMax(int energy) {
		values.put(IEnergyConsumer.ENERGY_STORED_MAX_KEY, energy);
	}

	@Override
	public void setEnergyConsume(int energy) {
		values.put(IEnergyConsumer.ENERGY_CONSUME_KEY, energy);
	}

	public void addEnergy(int energy) {
		if (this.getEnergyStored() < this.getEnergyStoredMax() && (this.getEnergyStored() + energy) <= this.getEnergyStoredMax()) {
			this.setEnergyStored(this.getEnergyStored() + energy);
		}
	}

	public void removeEnergy(int energy) {
		if (this.getEnergyStored() > 0 && (this.getEnergyStored() - energy) >= 0) {
			this.setEnergyStored(this.getEnergyStored() - energy);
			markDirty();
		}
	}

	public void onConnected() {
		if (EnergyNetwork.getNetwork(this) != null) {
			List<IEnergyProducer> producers = Objects.requireNonNull(EnergyNetwork.getNetwork(this)).getProducer();

			if (producers != null && !producers.isEmpty()) {
				for (IEnergyProducer pro : producers) {
					if (pro.getEnergyStored() > 0) {
						if (this.getEnergyStored() + pro.getEnergyOutput() <= this.getEnergyStoredMax()) {
							this.addEnergy(pro.transferEnergy());
							markDirty();
						}
					}
				}
			}
		}
	}

	public void tickEntity(World world, BlockPos blockPos, BlockState blockState, EnergyConsumerBlockEntity entity) {
		if (!world.isClient && world.getTime() % world.getTickManager().getTickRate() == 0) {
			entity.onConnected();
			entity.removeEnergy(entity.getEnergyConsume());
//			TerraForMars.LOGGER.info("EnergyStored {}", entity.getEnergyStored());
//			TerraForMars.LOGGER.info("EnergyConsume {}", entity.getEnergyConsume());
		}
	}
}
