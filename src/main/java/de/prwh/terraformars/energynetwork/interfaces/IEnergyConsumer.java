package de.prwh.terraformars.energynetwork.interfaces;

public interface IEnergyConsumer {

	public static final String ENERGY_STORED_KEY = "energyStored";
	public static final String ENERGY_STORED_MAX_KEY = "energyStoredMax";
	public static final String ENERGY_CONSUME_KEY = "energyConsume";
	
	public int getEnergyStored();
	public int getEnergyStoredMax();
	public int getEnergyConsume();
	
	public void setEnergyStored(int energy);
	public void setEnergyStoredMax(int energy);
	public void setEnergyConsume(int energy);
}
