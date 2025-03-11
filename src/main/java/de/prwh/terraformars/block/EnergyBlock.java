package de.prwh.terraformars.block;

import com.mojang.serialization.MapCodec;
import de.prwh.terraformars.energynetwork.EnergyNetwork;
import de.prwh.terraformars.entity.blockentity.EnergyBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyBlock extends BlockWithEntity {

    public EnergyBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        EnergyBlockEntity energyBlockEntity = (EnergyBlockEntity) world.getBlockEntity(pos);
        EnergyNetwork.onEntityAdd(energyBlockEntity);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        EnergyBlockEntity energyBlockEntity = (EnergyBlockEntity) world.getBlockEntity(pos);
        EnergyNetwork.onEntityRemove(energyBlockEntity);
        return state;
    }
}
