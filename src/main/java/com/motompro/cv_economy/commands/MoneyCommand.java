package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private CV_Economy plugin;

    public MoneyCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if(sender instanceof Player) {
            player = (Player) sender;
        }

        if(args.length == 1) {
            double balance = plugin.getPlayerMoney(args[0]);
            if(balance != -1) {
                sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.AQUA + args[0] + ChatColor.GREEN + " a " + ChatColor.AQUA + balance + ChatColor.GREEN + " crédits.");
            } else {
                sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + args[0] + " n'existe pas !");
            }
        } else {
            if(player != null) {
                double balance = plugin.getPlayerMoney(player.getUniqueId());
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez " + ChatColor.AQUA + balance + ChatColor.GREEN + " crédits.");
            }
        }

        return false;
    }
}
