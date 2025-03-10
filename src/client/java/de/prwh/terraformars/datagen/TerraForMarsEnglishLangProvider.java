package de.prwh.terraformars.datagen;

import de.prwh.terraformars.block.TFMBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TerraForMarsEnglishLangProvider extends FabricLanguageProvider {

    protected TerraForMarsEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(TFMBlocks.SOLAR_GENERATOR, "Solar Generator");
        translationBuilder.add(TFMBlocks.TERRAFORMER, "Terraformer");
    }
}
