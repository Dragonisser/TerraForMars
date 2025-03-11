package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.block.TFMBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TFMBlockEntities {

    public static BlockEntityType<EnergyBlockEntity> ENERGY_BLOCK_ENTITY_TYPE;

    public static BlockEntityType<EnergyConsumerBlockEntity> CONSUMER_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<EnergyProducerBlockEntity> PRODUCER_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<EnergyStorageBlockEntity> STORAGE_BLOCK_ENTITY_TYPE;

    private TFMBlockEntities() {}

    public static void init() {
        PRODUCER_BLOCK_ENTITY_TYPE = register("producer_block_entity", EnergyProducerBlockEntity::new, TFMBlocks.SOLAR_GENERATOR);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.of(TerraForMars.MOD_ID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }
}
