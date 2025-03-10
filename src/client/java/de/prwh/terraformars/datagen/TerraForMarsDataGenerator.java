package de.prwh.terraformars.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TerraForMarsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(TerraForMarsEnglishLangProvider::new);
		pack.addProvider(TerraForMarsGermanLangProvider::new);
		pack.addProvider(TerraForMarsModelProvider::new);
	}
}
