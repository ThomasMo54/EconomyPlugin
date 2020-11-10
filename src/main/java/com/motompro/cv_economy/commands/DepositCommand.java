package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepositCommand implements CommandExecutor {

    private CV_Economy plugin;

    public DepositCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Inventory playerInventory = player.getInventory();

        int amount;
        if(args[0].equals("all")) {
            amount = -1;
        } else {
            amount = Integer.parseInt(args[0]);
            if(amount <= 0) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous ne pouvez déposer qu'un montant superieur à 0 !");
                return false;
            }
        }

        List<Integer> creditItems = new ArrayList<>(Collections.nCopies(5, 0));
        for(ItemStack item : playerInventory.getContents()) {
            if(item == null) continue;

            if(item.getType().equals(Material.IRON_INGOT)) {
                creditItems.set(0, creditItems.get(0) + item.getAmount());
            } else if(item.getType().equals(Material.GOLD_INGOT)) {
                creditItems.set(1, creditItems.get(1) + item.getAmount());
            } else if(item.getType().equals(Material.INK_SACK) && item.getDurability() == 4) {
                creditItems.set(2, creditItems.get(2) + item.getAmount());
            } else if(item.getType().equals(Material.EMERALD)) {
                creditItems.set(3, creditItems.get(3) + item.getAmount());
            } else if(item.getType().equals(Material.DIAMOND)) {
                creditItems.set(4, creditItems.get(4) + item.getAmount());
            }
        }

        List<Integer> itemsToRemove = new ArrayList<>(Collections.nCopies(5, 0));
        if(amount == -1) {
            itemsToRemove = creditItems;
            amount += itemsToRemove.get(0) + itemsToRemove.get(1) * 10 + itemsToRemove.get(2) * 50 + itemsToRemove.get(3) * 100 + itemsToRemove.get(4) * 500;
        } else {
            int amountLeft = amount;
            while(amountLeft > 0) {
                if(amountLeft >= 500 && creditItems.get(4) >= 1) {
                    amountLeft -= 500;
                    itemsToRemove.set(4, itemsToRemove.get(4) + 1);
                    creditItems.set(4, creditItems.get(4) - 1);
                    continue;
                }
                if(amountLeft >= 100 && creditItems.get(3) >= 1) {
                    amountLeft -= 100;
                    itemsToRemove.set(3, itemsToRemove.get(3) + 1);
                    creditItems.set(3, creditItems.get(3) - 1);
                    continue;
                }
                if(amountLeft >= 50 && creditItems.get(2) >= 1) {
                    amountLeft -= 50;
                    itemsToRemove.set(2, itemsToRemove.get(2) + 1);
                    creditItems.set(2, creditItems.get(2) - 1);
                    continue;
                }
                if(amountLeft >= 10 && creditItems.get(1) >= 1) {
                    amountLeft -= 10;
                    itemsToRemove.set(1, itemsToRemove.get(1) + 1);
                    creditItems.set(1, creditItems.get(1) - 1);
                    continue;
                }
                if(amountLeft >= 1 && creditItems.get(0) >= 1) {
                    amountLeft -= 1;
                    itemsToRemove.set(0, itemsToRemove.get(0) + 1);
                    creditItems.set(0, creditItems.get(0) - 1);
                    continue;
                }
                break;
            }

            if(amountLeft > 0) {
                player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Vous n'avez pas assez de crédits sur vous !");
                return false;
            }
        }

        for(int slot = 0; slot < playerInventory.getSize(); slot++) {
            ItemStack item = playerInventory.getItem(slot);
            if(item == null) continue;

            int newAmount;
            if(item.getType().equals(Material.IRON_INGOT) && itemsToRemove.get(0) > 0) {
                newAmount = item.getAmount() - itemsToRemove.get(0);
                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    itemsToRemove.set(0, 0);
                } else {
                    playerInventory.clear(slot);
                    itemsToRemove.set(0, -newAmount);
                }
            }
            if(item.getType().equals(Material.GOLD_INGOT) && itemsToRemove.get(1) > 0) {
                newAmount = item.getAmount() - itemsToRemove.get(1);
                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    itemsToRemove.set(1, 0);
                } else {
                    playerInventory.clear(slot);
                    itemsToRemove.set(1, -newAmount);
                }
            }
            if(item.getType().equals(Material.INK_SACK) && item.getDurability() == 4 && itemsToRemove.get(2) > 0) {
                newAmount = item.getAmount() - itemsToRemove.get(2);
                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    itemsToRemove.set(2, 0);
                } else {
                    playerInventory.clear(slot);
                    itemsToRemove.set(2, -newAmount);
                }
            }
            if(item.getType().equals(Material.EMERALD) && itemsToRemove.get(3) > 0) {
                newAmount = item.getAmount() - itemsToRemove.get(3);
                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    itemsToRemove.set(3, 0);
                } else {
                    playerInventory.clear(slot);
                    itemsToRemove.set(3, -newAmount);
                }
            }
            if(item.getType().equals(Material.DIAMOND) && itemsToRemove.get(4) > 0) {
                newAmount = item.getAmount() - itemsToRemove.get(4);
                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    itemsToRemove.set(4, 0);
                } else {
                    playerInventory.clear(slot);
                    itemsToRemove.set(4, -newAmount);
                }
            }
        }

        plugin.addPlayerMoney(player.getUniqueId(), amount);
        player.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Vous avez déposé " + ChatColor.AQUA + amount + ChatColor.GREEN + " crédits.");

        return false;
    }
}
