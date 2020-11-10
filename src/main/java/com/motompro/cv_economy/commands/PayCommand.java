package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private CV_Economy plugin;

    public PayCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if(args.length != 2) {
            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Commande incorrecte !");
            return false;
        }

        String playerName = args[0];
        double amount = Double.parseDouble(args[1]);

        if(plugin.getPlayerMoney(player.getUniqueId()) < amount) {
            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous n'avez pas assez de crédits !");
            return false;
        }

        boolean paySuccess = plugin.addPlayerMoney(playerName, amount);
        if(paySuccess) {
            plugin.addPlayerMoney(player.getUniqueId(), -amount);
            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez donné " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits à " + ChatColor.AQUA + playerName + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + playerName + " n'existe pas !");
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getName().equals(playerName)) {
                p.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " vous a donné " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits.");
                break;
            }
        }

        return false;
    }
}
