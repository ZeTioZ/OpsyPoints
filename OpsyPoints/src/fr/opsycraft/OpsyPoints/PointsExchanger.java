package fr.opsycraft.OpsyPoints;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import fr.opsycraft.OpsyPoints.DataBase;
import fr.opsycraft.OpsyPoints.Config;
import net.milkbowl.vault.economy.Economy;

public class PointsExchanger implements CommandExecutor {

	double bCoins;
	Config config = new Config(new File("plugins" + File.separator + "OpsyPoints" + File.separator + "config.yml"));
	private String h = config.getString("host");
	private String n = config.getString("name");
	private String p = config.getString("pass");
	private String db = config.getString("dbName");
	int po = config.getInt("port");
	public DataBase bdd = new DataBase(this.h, this.db, this.n, this.p);
	
	public PointsExchanger(main main) {
		this.bCoins = config.getInt("boughtCoins");
	}
	
	String prefix = "§c[§eEchangeur§c] ";
	
	public double moneyCost() {
		int hours = 1;
		int hourstoseconds = hours * 3600;
		int basePrice = 100;
		double finalPrice = (Math.round((basePrice * (1.0 + (bCoins / (double) hourstoseconds)))*100.0))/100.0;
		return finalPrice;
	}
	
	private void boughtCalculator(Integer f) {
		int bCoinsFinal = (int) bCoins + f;
		bCoins = bCoinsFinal;
		config.set("boughtCoins", bCoins);
		return;
	}
	
	private void MoneyFlowIndex(String p) {
		Player sender = Bukkit.getServer().getPlayer(p);
		double actualMoneyCost = moneyCost();
		sender.sendMessage(prefix + "§2Cours de la monnaie actuel: " + actualMoneyCost + " H => 1 OC");
		sender.sendMessage(prefix + "§2Pour échanger vos hearts en points, utlisez §d/echange <hearts>");
		sender.sendMessage(prefix + "§2Montant d'échange minimal: " + actualMoneyCost + " Heart(s)");
		//Index Calculation in BETA !
	}
	
	@SuppressWarnings("deprecation") //Suppressed for all the economy method
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("exchange")) {
				if(args.length == 0) {
					MoneyFlowIndex(sender.getName());
				}
				else if(args.length == 1) {
					try 
					{
						RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
						Economy econ = rsp.getProvider();
						String gameSenderString = sender.getName(); //Retrieve the command sender in a string value
						String playerSenderString = this.bdd.getString("SELECT pseudo FROM users WHERE pseudo = '" + gameSenderString + "';", 1); //Retrieve the command sender in a string value from the DB
						int playerSenderMoneyInt = this.bdd.getInt("SELECT money FROM users WHERE pseudo = '" + gameSenderString + "';", 1); //Retrieve the command sender money in a string value from the DB 
						double playerMoney = econ.getBalance(sender.getName());
						double doubleArgument = Double.parseDouble(args[0]);
						int intArgument = Integer.parseInt(args[0]);
						int actualMoneyIndex = (int) Math.round(moneyCost());
						double actualMoneyIndexCheck = moneyCost();
						if(doubleArgument >= actualMoneyIndexCheck) {
							if(playerMoney > doubleArgument) {
								if(playerSenderString.equalsIgnoreCase(gameSenderString) && playerSenderString != null) {
									int pointsAdd = Math.round(intArgument / actualMoneyIndex);
									econ.withdrawPlayer(sender.getName(), doubleArgument);
									int playerFinal = playerSenderMoneyInt + pointsAdd;
									boughtCalculator(pointsAdd);
									this.bdd.sendRequest("UPDATE users SET money = " + playerFinal + " WHERE pseudo = '" + sender.getName() + "';");
									sender.sendMessage(prefix + "Tu viens de recevoir " + pointsAdd + " OpsyCoins !");
									sender.sendMessage(prefix + "Tu as maintenant " + playerFinal + " OpsyCoins !");
								}
								else {
									sender.sendMessage(prefix + "§cVous n'êtes pas inscrit au site web ! http://opsycraft.fr/");
								}
							}
							else {
								sender.sendMessage(prefix + "§cVous n'avez pas le montant de hearts entrés !");
							}
						}
						else {
							sender.sendMessage(prefix + "§cVous devez entrer une somme au dessus de " + (int) Math.ceil(actualMoneyIndexCheck) + " Hearts à échanger !");
						}
					}
					catch(NumberFormatException e) {
						sender.sendMessage(prefix + "Merci de rentrer un chiffre !");
					}
				}
				else {
					sender.sendMessage(prefix + "§cErreur de syntaxe !");
				}
			}
		}
		else if(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
			sender.sendMessage(prefix + "§cCette commande ne peut être utilisée qu'en tant que joueur !");
		}
		return false;
	}
}
