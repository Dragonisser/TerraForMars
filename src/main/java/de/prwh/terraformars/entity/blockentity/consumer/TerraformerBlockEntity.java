package de.prwh.terraformars.entity.blockentity.consumer;

import de.prwh.terraformars.energynetwork.interfaces.IEnergyProducer;
import de.prwh.terraformars.entity.blockentity.EnergyConsumerBlockEntity;
import de.prwh.terraformars.entity.blockentity.TFMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TerraformerBlockEntity extends EnergyConsumerBlockEntity {
    public TerraformerBlockEntity(BlockPos pos, BlockState state) {
        super(TFMBlockEntities.TERRAFORMER_TYPE,pos, state);
        this.setEnergyStored(0);
        this.setEnergyConsume(1);
        this.setEnergyStoredMax(100);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, TerraformerBlockEntity entity) {
        entity.tickEntity(world, blockPos, blockState, entity);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt(IEnergyProducer.ENERGY_STORED_KEY, this.getEnergyStored());

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.setEnergyStored(nbt.getInt(IEnergyProducer.ENERGY_STORED_KEY));
    }
}
