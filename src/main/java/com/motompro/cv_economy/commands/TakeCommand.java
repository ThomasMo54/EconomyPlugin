package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeCommand implements CommandExecutor {

    private CV_Economy plugin;

    public TakeCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if(sender instanceof Player) {
            player = (Player) sender;
            if(!player.isOp() && !player.hasPermission("admin.*")) {
                sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande !");
                return false;
            }
        }

        if(args.length != 2) {
            sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Commande incorrecte !");
            return false;
        }

        String playerName = args[0];
        double amount;
        if(args[1].equals("all")) {
            amount = plugin.getPlayerMoney(playerName);
        } else {
            amount = Double.parseDouble(args[1]);
        }

        double playerMoney = plugin.getPlayerMoney(playerName);
        if(playerMoney < amount) {
            plugin.addPlayerMoney(playerName, -playerMoney);
            sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez retiré " + ChatColor.AQUA + playerMoney + ChatColor.GREEN + " crédits à " + ChatColor.AQUA + playerName + ChatColor.GREEN + ".");
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.getName().equals(playerName)) {
                    p.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.AQUA + "Un admin" + ChatColor.GREEN + " vous a retiré " + ChatColor.AQUA + playerMoney + ChatColor.GREEN + " crédits.");
                    break;
                }
            }
        } else {
            plugin.addPlayerMoney(playerName, -amount);
            sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez retiré " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits à " + ChatColor.AQUA + playerName + ChatColor.GREEN + ".");
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.getName().equals(playerName)) {
                    p.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.AQUA + "Un admin" + ChatColor.GREEN + " vous a retiré " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits.");
                    break;
                }
            }
        }

        return false;
    }
}
