package de.prwh.terraformars;

import de.prwh.terraformars.block.TFMBlocks;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraForMars implements ModInitializer {
	public static final String MOD_ID = "terraformars";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//ItemGroup
	public static final RegistryKey<ItemGroup> BLOCK_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "item_group_blocks"));
	public static final ItemGroup BLOCK_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(TFMBlocks.SOLAR_GENERATOR))
			.displayName(Text.translatable("itemgroup" + MOD_ID + ".blocks"))
			.build();

	public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "item_group_items"));
	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(TFMBlocks.SOLAR_GENERATOR))
			.displayName(Text.translatable("itemgroup" + MOD_ID + ".items"))
			.build();


	@Override
	public void onInitialize() {

		Registry.register(Registries.ITEM_GROUP, BLOCK_GROUP_KEY, BLOCK_GROUP);
		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP);

		TFMBlocks.init();
	}
}