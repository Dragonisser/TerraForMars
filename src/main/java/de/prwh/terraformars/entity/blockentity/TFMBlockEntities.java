package de.prwh.terraformars.entity.blockentity;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.block.TFMBlocks;
import de.prwh.terraformars.entity.blockentity.consumer.TerraformerBlockEntity;
import de.prwh.terraformars.entity.blockentity.producer.SolarGeneratorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TFMBlockEntities {

    public static BlockEntityType<EnergyBlockEntity> ENERGY_BLOCK_ENTITY_TYPE;

    public static BlockEntityType<SolarGeneratorBlockEntity> SOLAR_GENERATOR_TYPE;
    public static BlockEntityType<TerraformerBlockEntity> TERRAFORMER_TYPE;
    
    public static BlockEntityType<EnergyStorageBlockEntity> STORAGE_BLOCK_ENTITY_TYPE;

    private TFMBlockEntities() {}

    public static void init() {
        SOLAR_GENERATOR_TYPE = register("solar_generator_block_entity", SolarGeneratorBlockEntity::new, TFMBlocks.SOLAR_GENERATOR);
        TERRAFORMER_TYPE = register("terraformer_block_entity", TerraformerBlockEntity::new, TFMBlocks.TERRAFORMER);
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
