package de.prwh.terraformars.datagen;

import de.prwh.terraformars.TerraForMars;
import de.prwh.terraformars.block.TFMBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TerraForMarsGermanLangProvider extends FabricLanguageProvider {

    protected TerraForMarsGermanLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(TFMBlocks.SOLAR_GENERATOR, "Solar Generator");
        translationBuilder.add(TFMBlocks.TERRAFORMER, "Terraformer");
        translationBuilder.add(TerraForMars.BLOCK_GROUP_KEY, "TerraForMars Blocks");
        translationBuilder.add(TerraForMars.ITEM_GROUP_KEY, "TerraForMars Items");
    }
}
