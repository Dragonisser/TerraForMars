package de.prwh.terraformars.entity.blockentity.producer;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import de.prwh.terraformars.entity.blockentity.EnergyProducerBlockEntity;
import de.prwh.terraformars.entity.blockentity.TFMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SolarGeneratorBlockEntity extends EnergyProducerBlockEntity {

    private boolean isDay = false;

    public SolarGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TFMBlockEntities.SOLAR_GENERATOR_TYPE, pos, state);
        this.setEnergyProduce(0);
        this.setEnergyStored(0);
        this.setEnergyOutput(5);
        this.setEnergyStoredMax(100);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, SolarGeneratorBlockEntity entity) {
        entity.tickEntity(world, blockPos, blockState, entity);

        if (!world.isClient) {

            boolean hasSkyAccess = world.isSkyVisible(blockPos.add(0, 1, 0));
            boolean isDaytime = world.getDimension().hasSkyLight() && world.isDay();

            if (isDaytime && hasSkyAccess) {
                entity.isDay = true;
                entity.setEnergyProduce(1);
            } else {
                entity.isDay = false;
                entity.setEnergyProduce(0); // Ensure energy stops at night or when obstructed
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("isDay", isDay);
        nbt.putInt(IEnergyProducer.ENERGY_STORED_KEY, this.getEnergyStored());

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.isDay = nbt.getBoolean("isDay");
        this.setEnergyStored(nbt.getInt(IEnergyProducer.ENERGY_STORED_KEY));
    }
}
