package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyConsumer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EnergyProducerBlockEntity extends EnergyBlockEntity implements IEnergyProducer {

	public EnergyProducerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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

	public void tickEntity(World world, BlockPos blockPos, BlockState blockState, EnergyProducerBlockEntity entity) {
		if (world.getTime() % world.getTickManager().getTickRate() == 0) {
			entity.addEnergy(entity.getEnergyProduce());
			//TerraForMars.LOGGER.info("Producer {}", world.getDimensionEntry().toString());
		}
	}
}
