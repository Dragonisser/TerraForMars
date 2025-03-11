package de.prwh.terraformars.block;

import com.mojang.serialization.MapCodec;
import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.energynetwork.EnergyNetwork;
import de.prwh.terraformars.entity.blockentity.EnergyBlockEntity;
import de.prwh.terraformars.entity.blockentity.EnergyProducerBlockEntity;
import de.prwh.terraformars.entity.blockentity.TFMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SolarGeneratorBlock extends EnergyBlock {

    public SolarGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(SolarGeneratorBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyProducerBlockEntity(pos, state) {
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof EnergyBlockEntity energyBlockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        energyBlockEntity.incrementClicks();
        player.sendMessage(Text.literal("You've clicked the block for the " + energyBlockEntity.getClicks() + "th time."), true);

        if(!world.isClient) {
            TerraForMars.LOGGER.info(EnergyNetwork.getNetwork(energyBlockEntity).toString());
        }
        TerraForMars.LOGGER.info("dimension id {}", world.getDimensionEntry().getIdAsString());

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, TFMBlockEntities.PRODUCER_BLOCK_ENTITY_TYPE, EnergyProducerBlockEntity::tick);
    }
}
