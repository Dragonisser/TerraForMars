package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyConsumer;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyProducerBlockEntity extends EnergyBlockEntity implements IEnergyProducer {

	public EnergyProducerBlockEntity(BlockPos pos, BlockState state) {
		super(TFMBlockEntities.PRODUCER_BLOCK_ENTITY_TYPE, pos, state);
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
		}
	}

	public void removeEnergy(int energy) {
		if (this.getEnergyStored() > 0 && (this.getEnergyStored() - energy) >= 0) {
			this.setEnergyStored(this.getEnergyStored() - energy);
		}
	}

	public int transferEnergy() {
		if (this.getEnergyStored() >= this.getEnergyOutput()) {
			this.removeEnergy(this.getEnergyOutput());
			return this.getEnergyOutput();
		} else if (this.getEnergyStored() < this.getEnergyOutput() && this.getEnergyStored() - this.getEnergyOutput() >= 0) {
			this.removeEnergy(this.getEnergyOutput() / 2);
			return this.getEnergyOutput() / 2;

		} else {
			return 0;
		}
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, EnergyBlockEntity entity) {
	}
}
