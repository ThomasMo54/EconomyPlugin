package com.motompro.cv_economy;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class TradeEvents implements Listener {

    private CV_Economy plugin;

    public TradeEvents(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        InventoryView view = e.getView();
        ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null) return;

        if(view.getTitle().startsWith("Trade")) {
            String sender = view.getTitle().split(" ")[1];
            Trade trade = null;
            for(Trade t : plugin.getTrades()) {
                if(t.getSender().getName().equals(sender)) {
                    trade = t;
                    break;
                }
            }

            if(trade == null) return;

            if(trade.getSenderSlots().contains(e.getSlot())) {
                if(player.getName().equals(trade.getSender().getName())) {
                    if(trade.isSenderReady()) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if(trade.getTargetSlots().contains(e.getSlot())) {
                if(player.getName().equals(trade.getTarget().getName())) {
                    if(trade.isTargetReady()) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if(clickedItem.getType().equals(Material.AIR)) return;

            NBTItem itemNBT = new NBTItem(clickedItem);

            if(itemNBT.getBoolean("notClickable")) {
                e.setCancelled(true);
                return;
            }

            if(itemNBT.getBoolean("acceptButton")) {
                ItemMeta meta = clickedItem.getItemMeta();
                if(e.getSlot() == 1 && player.getName().equals(trade.getSender().getName())) {
                    if(clickedItem.getDurability() == 14) {
                        meta.setDisplayName(ChatColor.GREEN + "Prêt");
                        clickedItem.setDurability((short) 5);
                        trade.setSenderReady(true);
                    } else {
                        meta.setDisplayName(ChatColor.RED + "Pas prêt");
                        clickedItem.setDurability((short) 14);
                        trade.setSenderReady(false);
                    }
                    clickedItem.setItemMeta(meta);
                    trade.getInventory().setItem(e.getSlot(), clickedItem);
                }
                if(e.getSlot() == 7 && player.getName().equals(trade.getTarget().getName())) {
                    if(clickedItem.getDurability() == 14) {
                        meta.setDisplayName(ChatColor.GREEN + "Prêt");
                        clickedItem.setDurability((short) 5);
                        trade.setTargetReady(true);
                    } else {
                        meta.setDisplayName(ChatColor.RED + "Pas prêt");
                        clickedItem.setDurability((short) 14);
                        trade.setTargetReady(false);
                    }
                    clickedItem.setItemMeta(meta);
                    trade.getInventory().setItem(e.getSlot(), clickedItem);
                }
                e.setCancelled(true);
                if(trade.isSenderReady() && trade.isTargetReady()) {
                    trade.doTrade();
                }
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        InventoryView view = e.getView();

        if(view.getTitle().startsWith("Trade")) {
            for(int i = 0; i < plugin.getTrades().size(); i++) {
                Trade trade = plugin.getTrades().get(i);
                if((trade.getSender().getName().equals(player.getName()) || trade.getTarget().getName().equals(player.getName()) && trade.isAccepted())) {
                    trade.getSender().sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Le trade a été annulé !");
                    trade.getTarget().sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "Le trade a été annulé !");
                    trade.getSender().closeInventory();
                    trade.getTarget().closeInventory();
                    trade.getSender().getInventory().addItem(trade.getSenderItems());
                    trade.getTarget().getInventory().addItem(trade.getTargetItems());
                    plugin.getTrades().remove(i);
                    break;
                }
            }
        }
    }
}
