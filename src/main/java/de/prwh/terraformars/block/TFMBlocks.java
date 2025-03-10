package de.prwh.terraformars.block;

import de.prwh.terraformars.TerraForMars;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class TFMBlocks {

    public static Block SOLAR_GENERATOR;
    public static Block TERRAFORMER;

    private TFMBlocks() {}

    public static void init() {
        SOLAR_GENERATOR = register("solar_generator", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL));
        TERRAFORMER = register("terraformer", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL));
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, true, true);
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean addToItemGroup, boolean registerBlockItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);

        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (addToItemGroup) {
            ItemGroupEvents.modifyEntriesEvent(TerraForMars.BLOCK_GROUP_KEY).register(itemgroup -> itemgroup.add(block));
        }
        if (registerBlockItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(TerraForMars.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TerraForMars.MOD_ID, name));
    }
}
