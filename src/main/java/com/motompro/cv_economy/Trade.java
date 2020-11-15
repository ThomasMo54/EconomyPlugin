package com.motompro.cv_economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trade {

    private Inventory tradeInventory;

    private CV_Economy plugin;
    private Player sender, target;

    private List<Integer> senderSlots = Arrays.asList(9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    private List<Integer> targetSlots = Arrays.asList(14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53);

    private boolean accepted = false;
    private boolean senderReady = false, targetReady = false;

    public Trade(CV_Economy plugin, Player sender, Player target) {
        this.plugin = plugin;
        this.sender = sender;
        this.target = target;

        tradeInventory = Bukkit.createInventory(null, 54, "Trade " + sender.getName());

        for(int i = 0; i < plugin.tradeInventory.getSize(); i++) {
            ItemStack item = plugin.tradeInventory.getItem(i);
            if(item == null || item.getType().equals(Material.AIR)) continue;
            tradeInventory.setItem(i, item.clone());
        }

        ItemStack senderHead = tradeInventory.getItem(0);
        SkullMeta senderSkull = (SkullMeta) senderHead.getItemMeta();
        senderSkull.setDisplayName(ChatColor.GOLD + sender.getName());
        senderSkull.setOwningPlayer(sender);
        senderHead.setItemMeta(senderSkull);
        tradeInventory.setItem(0, senderHead);

        ItemStack targetHead = tradeInventory.getItem(8);
        SkullMeta targetSkull = (SkullMeta) targetHead.getItemMeta();
        targetSkull.setDisplayName(ChatColor.GOLD + target.getName());
        targetSkull.setOwningPlayer(target);
        targetHead.setItemMeta(targetSkull);
        tradeInventory.setItem(8, targetHead);
    }

    public Inventory getInventory() {
        return tradeInventory;
    }

    public Player getSender() {
        return sender;
    }

    public Player getTarget() {
        return target;
    }

    public List<Integer> getSenderSlots() {
        return senderSlots;
    }

    public List<Integer> getTargetSlots() {
        return targetSlots;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isSenderReady() { return senderReady; }

    public boolean isTargetReady() { return targetReady; }

    public void setSenderReady(boolean ready) {
        this.senderReady = ready;
    }

    public void setTargetReady(boolean ready) {
        this.targetReady = ready;
    }

    public ItemStack[] getSenderItems() {
        ArrayList<ItemStack> senderItems = new ArrayList<>();
        for(int slot : senderSlots) {
            ItemStack item = tradeInventory.getItem(slot);
            if(item == null || item.getType().equals(Material.AIR)) continue;
            senderItems.add(item);
        }
        ItemStack[] senderItemsList = new ItemStack[senderItems.size()];
        return senderItems.toArray(senderItemsList);
    }

    public ItemStack[] getTargetItems() {
        ArrayList<ItemStack> targetItems = new ArrayList<>();
        for(int slot : targetSlots) {
            ItemStack item = tradeInventory.getItem(slot);
            if(item == null || item.getType().equals(Material.AIR)) continue;
            targetItems.add(item);
        }
        ItemStack[] targetItemsList = new ItemStack[targetItems.size()];
        return targetItems.toArray(targetItemsList);
    }

    public void start() {
        this.accepted = true;

        sender.openInventory(tradeInventory);
        target.openInventory(tradeInventory);
    }

    public void doTrade() {
        ItemStack[] senderItems = getSenderItems();
        ItemStack[] targetItems = getTargetItems();

        sender.getInventory().addItem(targetItems);
        target.getInventory().addItem(senderItems);
        sender.closeInventory();
        target.closeInventory();
        sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Échange terminé.");
        target.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Échange terminé.");
        for(int i = 0; i < plugin.getTrades().size(); i++) {
            Trade trade = plugin.getTrades().get(i);
            if((trade.getSender().getName().equals(sender.getName()) || trade.getTarget().getName().equals(target.getName()))) {
                plugin.getTrades().remove(i);
                break;
            }
        }
    }
}
