package fr.opsycraft.OpsyPoints;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	
	Config config = new Config(new File("plugins" + File.separator + "OpsyPoints" + File.separator + "config.yml"));
	String h = this.config.getString("host");
	String n = this.config.getString("name");
	String p = this.config.getString("pass");
	String db = this.config.getString("dbName");
	int po = this.config.getInt("port");
	public DataBase bdd = new DataBase(this.h, this.db, this.n, this.p);
	private File chestf;
	
	PointsExchanger ptsex = new PointsExchanger(this);
	double bCoins = this.ptsex.bCoins;

  	@Override
	public void onEnable() {
  		saveDefaultConfig();
  		this.chestf = new File(getDataFolder(), "chest.yml");
  		if (!this.chestf.exists()) {
  			this.chestf.getParentFile().mkdirs();
  			saveResource("chest.yml", false);
  		}
  		//this.menu = new Menu(this);
  	    getCommand("points").setExecutor(new PointsHandler(this));
  	    getCommand("chestbuy").setExecutor(new ChestBuyHandler(this));
  	    getCommand("exchange").setExecutor(new PointsExchanger(this));
    
  		this.bdd.connection();
  		getLogger().info("Le plugin vient de s'allumer");
  		getLogger().info("Bourse actuelle: " + Double.toString(bCoins));
  	}


	@Override
	public void onDisable() {
		config.save();
  		getLogger().info("Le plugin vient de s'�teindre");
  		this.bdd.disconnection();
  	}
}
