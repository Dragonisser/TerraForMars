package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.energynetwork.EnergyNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public abstract class EnergyBlockEntity extends BlockEntity {

    protected Map<String, Integer> values = new HashMap<String, Integer>();

    public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
    }

    public void onNetworkEntityApplied(EnergyNetwork network, EnergyBlockEntity change) {
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, EnergyBlockEntity entity) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
    }
}
