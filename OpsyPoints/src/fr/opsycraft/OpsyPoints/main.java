package fr.opsycraft.OpsyPoints;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import fr.opsycraft.OpsyPoints.PointsExchanger;

public class main extends JavaPlugin {
	
	Config config = new Config(new File("plugins" + File.separator + "OpsyPoints" + File.separator + "config.yml"));
	String h = this.config.getString("host");
	String n = this.config.getString("name");
	String p = this.config.getString("pass");
	String db = this.config.getString("dbName");
	int po = this.config.getInt("port");
	public DataBase bdd = new DataBase(this.h, this.db, this.n, this.p);
	//private File chestf;
	
	PointsExchanger ptsex = new PointsExchanger(this);
	double bCoins = this.ptsex.bCoins;
	
	public void indexRecalc() {
		double nCoins = (bCoins * 0.75);
		if (nCoins <= 0)
		{
			nCoins = 0;
			getLogger().info("La bourse est à son point le plus bas !");
		}
		else {
			getLogger().info("La bourse a été recalculée !");
			getLogger().info("Anciens bCoins : " + Double.toString(bCoins));
			getLogger().info("Ancienne bourse : " + Double.toString(this.ptsex.moneyCost()));
		}
		bCoins = nCoins;
		this.ptsex.bCoins = nCoins;
		config.set("boughtCoins", (int) nCoins);
	}

  	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
  		saveDefaultConfig();
  		/*this.chestf = new File(getDataFolder(), "chest.yml");
  		if (!this.chestf.exists()) {
  			this.chestf.getParentFile().mkdirs();
  			saveResource("chest.yml", false);
  		}*/
  		//this.menu = new Menu(this);
  	    getCommand("points").setExecutor(new PointsHandler(this));
  	    getCommand("chestbuy").setExecutor(new ChestBuyHandler(this));
  	    getCommand("exchange").setExecutor(new PointsExchanger(this));
    
  		this.bdd.connection();
  		java.util.Date date = new java.util.Date();
  		if (date.getDay() == 1) 
  		{
  			indexRecalc();
  		}
  		getLogger().info("Le plugin vient de s'allumer");
  		getLogger().info("bCoins actuel: " + Double.toString(bCoins));
  		getLogger().info("Nouvelle bourse : " + Double.toString(this.ptsex.moneyCost()));
  		
  	}


	@Override
	public void onDisable() {
		config.save();
  		getLogger().info("Le plugin vient de s'éteindre");
  		this.bdd.disconnection();
  	}
	
}
