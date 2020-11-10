package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand implements CommandExecutor {

    private CV_Economy plugin;

    public EconomyCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if(args[0].equals("help")) {
            helpMessage(player);
        }

        return false;
    }

    private void helpMessage(Player player) {
        player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Liste des commandes :");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/money <joueur> : affiche les crédits sur votre compte ou sur celui d'un autre joueur.");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/pay <joueur> <montant> : donne des crédits à un joueur.");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/baltop : affiche le top 5 des joueurs les plus riches.");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/take <joueur> <montant/all> : supprime des crédits à un joueur.");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/deposit <montant/all> : dépose des crédits dans votre inventaire sur votre compte.");
        player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + "/withdraw <montant/all> : retire des crédits dans votre inventaire depuis votre compte.");
    }
}
