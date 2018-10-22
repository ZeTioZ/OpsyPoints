package fr.opsycraft.OpsyPoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChestBuyHandler
  implements CommandExecutor
{
  Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));
  String h = this.config.getString("host");
  String n = this.config.getString("name");
  String p = this.config.getString("pass");
  String db = this.config.getString("dbName");
  int po = this.config.getInt("port");
  public DataBase bdd = new DataBase(this.h, this.db, this.n, this.p);
  String prefix = "§c[§eOpsyChest§c] ";
  
  public void addChestPlayer(String s, String p)
  {
    Player sender = Bukkit.getServer().getPlayer(s);
    Player target = Bukkit.getServer().getPlayer(p);
    ConsoleCommandSender c = Bukkit.getConsoleSender();
    for (int i = 1; i < 10; i++) {
      if (target.hasPermission("endervault.vault." + i))
      {
        if (i == 9) {
          sender.sendMessage(this.prefix + ChatColor.AQUA + p + " &cavait déjà tout les coffres ! Il est donc impossible de lui en ajouter !".replaceAll("&", "§"));
        }
      }
      else
      {
        Bukkit.dispatchCommand(c, "pex user " + p + " add endervault.vault." + i);
        target.sendMessage(this.prefix + "&2Le coffre numéro ".replaceAll("&", "§") + i + " vous a été ajouté !");
        sender.sendMessage(this.prefix + "&2Le coffre numéro ".replaceAll("&", "§") + i + " a été ajouté au joueur " + ChatColor.AQUA + p + " &2avec succès !".replaceAll("&", "§"));
        return;
      }
    }
  }
  
  public void addChestConsole(String s, String p)
  {
    Player target = Bukkit.getServer().getPlayer(p);
    ConsoleCommandSender c = Bukkit.getConsoleSender();
    int chestPrice = 1500;
    String playerTargetMoneyString = this.bdd.getString("SELECT money FROM users WHERE pseudo = '" + p + "';", 1);
    int playerTargetMoneyInt = Integer.parseInt(playerTargetMoneyString);
    for (int i = 1; i < 10; i++) {
      if (target.hasPermission("endervault.vault." + i))
      {
        if (i == 9)
        {
          target.sendMessage(this.prefix + "&cVous avez essayé de vous ajouter un coffre, mais vous les aviez déjà tous !".replaceAll("&", "§"));
          target.sendMessage(this.prefix + "&cVos OpsyCoins vous ont été remboursé !".replaceAll("&", "§"));
          int playerFinal = playerTargetMoneyInt + chestPrice;
          this.bdd.sendRequest("UPDATE users SET money = " + playerFinal + " WHERE pseudo = '" + p + "';");
          c.sendMessage(this.prefix + ChatColor.AQUA + p + " &cavait déjà tout les coffres ! Il est donc impossible de lui en ajouter !".replaceAll("&", "§"));
        }
      }
      else
      {
        Bukkit.dispatchCommand(c, "pex user " + p + " add endervault.vault." + i);
        target.sendMessage(this.prefix + "&2Le coffre numéro ".replaceAll("&", "§") + i + " vous a été ajouté !");
        c.sendMessage(this.prefix + "&2Le coffre numéro ".replaceAll("&", "§") + i + " a été ajouté au joueur " + ChatColor.AQUA + p + " &2avec succès !".replaceAll("&", "§"));
        return;
      }
    }
  }
  
  public void removeChestPlayer(String s, String p)
  {
    Player sender = Bukkit.getServer().getPlayer(s);
    Player target = Bukkit.getServer().getPlayer(p);
    ConsoleCommandSender c = Bukkit.getConsoleSender();
    for (int i = 9; i > 0; i--) {
      if (target.hasPermission("endervault.vault." + i))
      {
        if (i == 1)
        {
          sender.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          return;
        }
        if (i == 2)
        {
          List<String> twoChestRanks = new ArrayList<String>();
          twoChestRanks.add("soldat");
          twoChestRanks.add("guerrier");
          twoChestRanks.add("chevalier");
          twoChestRanks.add("donateur");
          for (String twoChestRanksCheck : twoChestRanks) {
            if (target.hasPermission("opsytitles." + twoChestRanksCheck))
            {
              sender.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
              return;
            }
          }
        }
        else if (i == 3)
        {
          if (target.hasPermission("opsytitles.vip")) {
            sender.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          }
        }
        else if ((i == 4) && 
          (target.hasPermission("opsytitles.legende")))
        {
          sender.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          return;
        }
        Bukkit.dispatchCommand(c, "pex user " + p + " remove endervault.vault." + i);
        target.sendMessage(this.prefix + "&cLe coffre numéro ".replaceAll("&", "§") + i + " vous a été retiré !");
        sender.sendMessage(this.prefix + "&cLe coffre numéro ".replaceAll("&", "§") + i + " a été retiré au joueur " + ChatColor.AQUA + p + " &cavec succès !".replaceAll("&", "§"));
        return;
      }
    }
  }
  
  public void removeChestConsole(String s, String p)
  {
    Player target = Bukkit.getServer().getPlayer(p);
    ConsoleCommandSender c = Bukkit.getConsoleSender();
    for (int i = 9; i > 0; i--) {
      if (target.hasPermission("endervault.vault." + i))
      {
        if (i == 1)
        {
          c.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          return;
        }
        if (i == 2)
        {
          List<String> twoChestRanks = new ArrayList<String>();
          twoChestRanks.add("soldat");
          twoChestRanks.add("guerrier");
          twoChestRanks.add("chevalier");
          twoChestRanks.add("donateur");
          for (String twoChestRanksCheck : twoChestRanks) {
            if (target.hasPermission("opsytitles." + twoChestRanksCheck))
            {
              c.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
              return;
            }
          }
        }
        else if (i == 3)
        {
          if (target.hasPermission("opsytitles.vip")) {
            c.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          }
        }
        else if ((i == 4) && 
          (target.hasPermission("opsytitles.legende")))
        {
          c.sendMessage(this.prefix + ChatColor.AQUA + p + " &cn'a plus de coffre suppémentaires !".replaceAll("&", "§"));
          return;
        }
        Bukkit.dispatchCommand(c, "pex user " + p + " remove endervault.vault." + i);
        target.sendMessage(this.prefix + "&cLe coffre numéro ".replaceAll("&", "§") + i + " vous a été retiré !");
        c.sendMessage(this.prefix + "&cLe coffre numéro ".replaceAll("&", "§") + i + " a été retiré au joueur " + ChatColor.AQUA + p + " &cavec succès !".replaceAll("&", "§"));
        return;
      }
    }
  }
  
  @Override
public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
  {
    if ((sender instanceof Player))
    {
      if (cmd.getName().equalsIgnoreCase("chestbuy")) {
        if (sender.hasPermission("opsychest.buy.player"))
        {
          if (args.length == 0)
          {
            sender.sendMessage("§7§m-]----------|-----|----------[-§r".replaceAll("&", "§"));
            sender.sendMessage("§d /chestbuy §6» §bAffiche la page d'aide.".replaceAll("&", "§"));
            sender.sendMessage("§d /chestbuy <player> add §6» §bAjoute un coffre au joueur.".replaceAll("&", "§"));
            sender.sendMessage("§d /chestbuy <player> remove §6» §bRetire un coffre au joueur.".replaceAll("&", "§"));
            sender.sendMessage("§7§m-]----------|-----|----------[-§r".replaceAll("&", "§"));
          }
          else
          {
            if (args.length == 1)
            {
              sender.sendMessage(this.prefix + "&cVous n'avez pas entré d'option, utilisez &d\"add\" &cpour ajouter un coffre et &d\"remove\" &cpour en retirer.".replaceAll("&", "§"));
              return false;
            }
            if (args.length == 2)
            {
              Player target = Bukkit.getServer().getPlayer(args[0]);
              if ((target != null) && (target.isOnline()) && (target != sender))
              {
                if (args[1].equalsIgnoreCase("add"))
                {
                  addChestPlayer(sender.getName(), args[0]);
                  return true;
                }
                if (args[1].equalsIgnoreCase("remove"))
                {
                  removeChestPlayer(sender.getName(), args[0]);
                  return true;
                }
              }
              else
              {
                if (target == sender)
                {
                  sender.sendMessage(this.prefix + "&cVous ne pouvez pas vous ajouter/retirer des coffres vous même !".replaceAll("&", "§"));
                  return false;
                }
                sender.sendMessage(this.prefix + "&cLe joueur que vous avez entré est invalide ou hors ligne !".replaceAll("&", "§"));
                return false;
              }
            }
            else
            {
              List<String> help = this.config.getStringList("Chestbuy.help");
              for (String helps : help) {
            	  sender.sendMessage(helps.replaceAll("&", "§"));
              }
              return false;
            }
          }
        }
        else
        {
          sender.sendMessage(this.prefix + "&cVous n'avez pas la permission d'utiliser cette commande !".replaceAll("&", "§"));
          return false;
        }
      }
    }
    else if ((sender instanceof ConsoleCommandSender) || (sender instanceof RemoteConsoleCommandSender)) {
      if (cmd.getName().equalsIgnoreCase("chestbuy")) {
    	  if (args.length == 0)
    	  {
    		  sender.sendMessage("§7§m-]----------|-----|----------[-§r".replaceAll("&", "§"));
    		  sender.sendMessage("§d /chestbuy §6» §bAffiche la page d'aide.".replaceAll("&", "§"));
    		  sender.sendMessage("§d /chestbuy <player> add §6» §bAjoute un coffre au joueur.".replaceAll("&", "§"));
    		  sender.sendMessage("§d /chestbuy <player> remove §6» §bRetire un coffre au joueur.".replaceAll("&", "§"));
    		  sender.sendMessage("§7§m-]----------|-----|----------[-§r".replaceAll("&", "§"));
    	  }
    	  else
    	  {
    		  if (args.length == 1)
    		  {
    			  sender.sendMessage(this.prefix + "&cVous n'avez pas entré d'option, utilisez &d\"add\" &cpour ajouter un coffre et &d\"remove\" &cpour en retirer.".replaceAll("&", "§"));
    			  return false;
    		  }
    		  if (args.length == 2)
    		  {
    			  Player target = Bukkit.getServer().getPlayer(args[0]);
    			  if ((target != null) && (target.isOnline()) && (target != sender))
    			  {
    				  if (args[1].equalsIgnoreCase("add"))
    				  {
    					  addChestConsole(null, args[0]);
    					  return true;
    				  }
    				  if (args[1].equalsIgnoreCase("remove"))
    				  {
    					  removeChestConsole(null, args[0]);
    					  return true;
    				  }
    			  }
    			  else
    			  {
    				  if (target == sender)
    				  {
    					  sender.sendMessage(this.prefix + "&cVous ne pouvez pas vous ajouter/retirer des coffres vous même !".replaceAll("&", "§"));
    					  return false;
    				  }
    				  sender.sendMessage(this.prefix + "&cLe joueur que vous avez entré est invalide ou hors ligne !".replaceAll("&", "§"));
    				  return false;
    			  }
    		  }
    		  else
    		  {
    			  List<String> help = this.config.getStringList("Chestbuy.help");
    			  for (String helps : help) {
    				  sender.sendMessage(helps.replaceAll("&", "§"));
    			  }
    			  return false;
    		  }
    	  }
       }
    }
    return false;
  }
}
