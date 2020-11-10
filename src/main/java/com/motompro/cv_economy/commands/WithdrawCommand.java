package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WithdrawCommand implements CommandExecutor {

    private CV_Economy plugin;

    public WithdrawCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        int amount;
        if(args[0].equals("all")) {
            amount = (int) Math.floor(plugin.getPlayerMoney(player.getUniqueId()));
        } else {
            amount = Integer.parseInt(args[0]);
            if(amount <= 0) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous ne pouvez retirer qu'un montant superieur à 0 !");
                return false;
            }
        }

        List<Integer> itemsToAdd = new ArrayList<>(Collections.nCopies(5, 0));
        int amountLeft = amount;
        while(amountLeft > 0) {
            if(amountLeft >= 500) {
                amountLeft -= 500;
                itemsToAdd.set(4, itemsToAdd.get(4) + 1);
                continue;
            }
            if(amountLeft >= 100) {
                amountLeft -= 100;
                itemsToAdd.set(3, itemsToAdd.get(3) + 1);
                continue;
            }
            if(amountLeft >= 50) {
                amountLeft -= 50;
                itemsToAdd.set(2, itemsToAdd.get(2) + 1);
                continue;
            }
            if(amountLeft >= 10) {
                amountLeft -= 10;
                itemsToAdd.set(1, itemsToAdd.get(1) + 1);
                continue;
            }
            amountLeft -= 1;
            itemsToAdd.set(0, itemsToAdd.get(0) + 1);
        }

        if(itemsToAdd.get(0) > 0) {
            player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, itemsToAdd.get(0)));
        }
        if(itemsToAdd.get(1) > 0) {
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, itemsToAdd.get(1)));
        }
        if(itemsToAdd.get(2) > 0) {
            player.getInventory().addItem(new ItemStack(Material.INK_SACK, itemsToAdd.get(2), (short) 4));
        }
        if(itemsToAdd.get(3) > 0) {
            player.getInventory().addItem(new ItemStack(Material.EMERALD, itemsToAdd.get(3)));
        }
        if(itemsToAdd.get(4) > 0) {
            while(itemsToAdd.get(4) > 64) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
                itemsToAdd.set(4, itemsToAdd.get(4) - 64);
            }
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, itemsToAdd.get(4)));
        }

        plugin.addPlayerMoney(player.getUniqueId(), -amount);
        player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez retiré " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits.");

        return false;
    }
}
