package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.energynetwork.EnergyNetwork;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyConsumer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class EnergyStorageBlockEntity extends EnergyBlockEntity implements IEnergyStorage {

	public EnergyStorageBlockEntity(BlockPos pos, BlockState state) {
		super(TFMBlockEntities.STORAGE_BLOCK_ENTITY_TYPE, pos, state);
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
	public int getEnergyProduce() {
		return values.get(IEnergyProducer.ENERGY_PRODUCE_KEY);
	}

	@Override
	public int getEnergyOutput() {
		return values.get(IEnergyProducer.ENERGY_OUTPUT_KEY);
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

	@Override
	public void setEnergyProduce(int energy) {
		values.put(IEnergyProducer.ENERGY_PRODUCE_KEY, energy);
	}

	@Override
	public void setEnergyOutput(int energy) {
		values.put(IEnergyProducer.ENERGY_OUTPUT_KEY, energy);
	}

	public void addEnergy(int energy) {
		if (this.getEnergyStored() < this.getEnergyStoredMax() && (this.getEnergyStored() + energy) <= this.getEnergyStoredMax()) {
			this.setEnergyStored(this.getEnergyStored() + energy);
			markDirty();
		}
	}

	public void removeEnergy(int energy) {
		if (this.getEnergyStored() > 0 && (this.getEnergyStored() - energy) >= 0) {
			this.setEnergyStored(this.getEnergyStored() - energy);
			markDirty();
		}
	}

	public void ifConnected() {
		if (!(this.world != null && this.world.isClient)) {
			if (EnergyNetwork.getNetwork(this) != null) {
				List<IEnergyProducer> producers = EnergyNetwork.getNetwork(this).getProducer();

				if (!producers.isEmpty()) {
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
	}

	public int transferEnergy() {
		if (this.getEnergyStored() >= this.getEnergyOutput()) {
			this.removeEnergy(this.getEnergyOutput());
			markDirty();
			return this.getEnergyOutput();
		} else if (this.getEnergyStored() > 0) { // Simplified condition
			int transferred = Math.min(this.getEnergyStored(), this.getEnergyOutput() / 2);
			this.removeEnergy(transferred);
			markDirty();
			return transferred;
		} else {
			return 0;
		}
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, EnergyStorageBlockEntity entity) {
		entity.ifConnected();
	}
}
