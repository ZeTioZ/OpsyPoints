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

import net.milkbowl.vault.economy.Economy;

public class PointsExchanger implements CommandExecutor {
	
	Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));
	String h = this.config.getString("host");
	String n = this.config.getString("name");
	String p = this.config.getString("pass");
	String db = this.config.getString("dbName");
	int po = this.config.getInt("port");
	public DataBase bdd = new DataBase(this.h, this.db, this.n, this.p);
	String prefix = "§c[§eEchangeur§c] ";
	
	public void MoneyFlowIndex(String p) {
		Player sender = Bukkit.getServer().getPlayer(p);
		sender.sendMessage(prefix + "§2Cours de la monnaie actuel: 100 000 H => 1000 OC");
		sender.sendMessage(prefix + "§2Pour échanger vos hearts en points, utlisez §d/echange <hearts>");
		sender.sendMessage(prefix + "§2Montant d'échange minimal: 100 Hearts");
		//Must be improved with some index calculation...
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
					try {
						RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
						Economy econ = rsp.getProvider();
						String gameSenderString = sender.getName(); //Retrieve the command sender in a string value
						String playerSenderString = this.bdd.getString("SELECT pseudo FROM users WHERE pseudo = '" + gameSenderString + "';", 1); //Retrieve the command sender in a string value from the DB
						String playerSenderMoneyString = this.bdd.getString("SELECT money FROM users WHERE pseudo = '" + gameSenderString + "';", 1); //Retrieve the command sender money in a string value from the DB 
						int playerSenderMoneyInt = Integer.parseInt(playerSenderMoneyString); //Retrieve the command sender money in an int value
						double playerMoney = econ.getBalance(sender.getName());
						int intArgument = Integer.parseInt(args[0]);
						if(intArgument >= 100) {
							if(playerMoney > intArgument) {
								if(playerSenderString.equalsIgnoreCase(gameSenderString) && playerSenderString != null) {
									int pointsAdd = intArgument/100;
									econ.withdrawPlayer(sender.getName(), intArgument);
						               int playerFinal = playerSenderMoneyInt + pointsAdd;
						               this.bdd.sendRequest("UPDATE users SET money = " + playerFinal + " WHERE pseudo = '" + sender.getName() + "';");
						               sender.sendMessage("§c[Points]§3 Tu viens de recevoir " + pointsAdd + " OpsyCoins !");
						               sender.sendMessage("§c[Points]§3 Tu as maintenant " + playerFinal + " OpsyCoins !");
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
							sender.sendMessage(prefix + "§cVous devez entrer une somme au dessus de 100 Hearts à échanger !");
						}
					}
					catch(NumberFormatException e) {
						sender.sendMessage("[Echangeur] Veuillez entrer le montant sous forme de chiffres !");
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
