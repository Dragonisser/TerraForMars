package de.prwh.terraformars.energynetwork.interfaces;

public interface IEnergyProducer {

	public static final String ENERGY_STORED_KEY = "energyStored";
	public static final String ENERGY_STORED_MAX_KEY = "energyStoredMax";
	public static final String ENERGY_PRODUCE_KEY = "energyProduce";
	public static final String ENERGY_OUTPUT_KEY = "energyOutput";
	
	public int getEnergyStored();
	public int getEnergyStoredMax();
	public int getEnergyProduce();
	public int getEnergyOutput();
	
	public void setEnergyStored(int energy);
	public void setEnergyStoredMax(int energy);
	public void setEnergyProduce(int energy);
	public void setEnergyOutput(int energy);
	
	public int transferEnergy();

}
