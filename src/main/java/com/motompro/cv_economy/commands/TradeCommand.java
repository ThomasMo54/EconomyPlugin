package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import com.motompro.cv_economy.Trade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {

    private CV_Economy plugin;

    public TradeCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if(args.length > 2 || args.length == 0) {
            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Commande incorrecte !");
            return false;
        }

        if(args[0].equals("accept")) {
            if(args.length != 2) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Commande incorrecte ! (/trade accept <player>)");
                return false;
            }
            String senderName = args[1];

            for(Trade trade : plugin.getTrades()) {
                if(trade.getSender().getName().equals(senderName) && trade.getTarget().getName().equals(player.getName()) && !trade.isAccepted()) {
                    trade.start();
                    break;
                }
            }

        } else {
            Player targetPlayer = null;

            String targetName = args[0];

            if(targetName.equals(player.getName())) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous ne pouvez pas lancer de trade à vous-même !");
                return false;
            }

            for(Trade trade : plugin.getTrades()) {
                if(trade.getSender().getName().equals(player.getName())) {
                    player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous avez déjà une demande d'échange en cours !");
                    return false;
                }
            }

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.getName().equals(targetName)) {
                    targetPlayer = p;
                    break;
                }
            }

            if(targetPlayer == null) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + targetName + " n'est pas en ligne !");
                return false;
            }

            plugin.addTrade(new Trade(plugin, player, targetPlayer));

            player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez envoyé une demande d'échange à " + ChatColor.AQUA + targetName + ChatColor.GREEN + ", elle expirera dans " + ChatColor.AQUA + "30" + ChatColor.GREEN + " secondes.");
            targetPlayer.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " vous a envoyé une demande d'échange, faites " + ChatColor.AQUA + "/trade accept " + player.getName() + ChatColor.GREEN + " pour l'accepter. La demande expirera dans " + ChatColor.AQUA + "30" + ChatColor.GREEN + " secondes.");
        }

        return false;
    }
}
