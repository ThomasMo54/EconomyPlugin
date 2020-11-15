package com.motompro.cv_economy;

import com.motompro.cv_economy.commands.*;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class CV_Economy extends JavaPlugin {

    private FileConfiguration defaultConfig = this.getConfig();

    private File accountsConfigFile;
    private FileConfiguration accountsConfig;

    private ArrayList<Trade> trades = new ArrayList<>();
    public Inventory tradeInventory;

    public static String MESSAGE_PREFIX = ChatColor.AQUA + "CV" + ChatColor.GOLD + "Economy " + ChatColor.GRAY + "» ";

    @Override
    public void onEnable() {
        this.createAccountsConfig();

        defaultConfig.addDefault("startMoney", 1000);
        defaultConfig.options().copyDefaults(true);
        this.saveConfig();

        initTradeInventory();

        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("take").setExecutor(new TakeCommand(this));
        getCommand("givemoney").setExecutor(new GiveMoneyCommand(this));
        getCommand("baltop").setExecutor(new BaltopCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new TradeEvents(this), this);
    }

    @Override
    public void onDisable() {}

    private void createAccountsConfig() {
        accountsConfigFile = new File(getDataFolder(), "accounts.yml");
        if (!accountsConfigFile.exists()) {
            accountsConfigFile.getParentFile().mkdirs();
            saveResource("accounts.yml", false);
        }

        accountsConfig = new YamlConfiguration();
        try {
            accountsConfig.load(accountsConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getAccountsConfig() {
        return this.accountsConfig;
    }

    public void saveAccountsConfig() {
        try {
            accountsConfig.save(accountsConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTradeInventory() {
        ItemStack separator = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta separatorMeta = separator.getItemMeta();
        separatorMeta.setDisplayName(" ");
        separator.setItemMeta(separatorMeta);

        NBTItem separatorNBT = new NBTItem(separator);
        separatorNBT.setBoolean("notClickable", true);

        tradeInventory = Bukkit.createInventory(null, 54);
        for(int i = 4; i < 50; i += 9) {
            tradeInventory.setItem(i, separatorNBT.getItem());
        }
        for(int i = 2; i < 7; i++) {
            tradeInventory.setItem(i, separatorNBT.getItem());
        }

        ItemStack senderHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        NBTItem senderNBT = new NBTItem(senderHead);
        senderNBT.setBoolean("notClickable", true);
        tradeInventory.setItem(0, senderNBT.getItem());

        ItemStack targetHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        NBTItem targetNBT = new NBTItem(targetHead);
        targetNBT.setBoolean("notClickable", true);
        tradeInventory.setItem(8, targetNBT.getItem());

        ItemStack acceptButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
        ItemMeta acceptButtonMeta = acceptButton.getItemMeta();
        acceptButtonMeta.setDisplayName(ChatColor.RED + "Pas prêt");
        acceptButton.setItemMeta(acceptButtonMeta);
        NBTItem acceptButtonNBT = new NBTItem(acceptButton);
        acceptButtonNBT.setBoolean("acceptButton", true);
        tradeInventory.setItem(1, acceptButtonNBT.getItem());
        tradeInventory.setItem(7, acceptButtonNBT.getItem());
    }

    public double getPlayerMoney(UUID playerUUID) {
        ConfigurationSection accountSection = accountsConfig.getConfigurationSection(playerUUID.toString());
        if(accountSection != null) {
            return accountSection.getDouble("balance");
        } else {
            return -1;
        }
    }

    public double getPlayerMoney(String playerName) {
        for(String uuid : accountsConfig.getKeys(false)) {
            if(accountsConfig.getConfigurationSection(uuid).getString("name").equals(playerName)) {
                return accountsConfig.getConfigurationSection(uuid).getDouble("balance");
            }
        }
        return -1;
    }

    public boolean addPlayerMoney(UUID playerUUID, double amount) {
        ConfigurationSection accountSection = accountsConfig.getConfigurationSection(playerUUID.toString());
        if(accountSection != null) {
            double balance = accountSection.getDouble("balance");
            balance += amount;
            accountsConfig.getConfigurationSection(playerUUID.toString()).set("balance", balance);
            saveAccountsConfig();
            return true;
        } else {
            return false;
        }
    }

    public boolean addPlayerMoney(String playerName, double amount) {
        for(String uuid : accountsConfig.getKeys(false)) {
            if(accountsConfig.getConfigurationSection(uuid).getString("name").equals(playerName)) {
                double balance = accountsConfig.getConfigurationSection(uuid).getDouble("balance");
                balance += amount;
                accountsConfig.getConfigurationSection(uuid).set("balance", balance);
                saveAccountsConfig();
                return true;
            }
        }
        return false;
    }

    public Map<String, Double> getBaltop(int n) {
        List<String> names = new ArrayList<>(Collections.nCopies(n, ""));
        List<Double> balances = new ArrayList<>(Collections.nCopies(n, 0d));

        for(String uuid : accountsConfig.getKeys(false)) {
            ConfigurationSection account = accountsConfig.getConfigurationSection(uuid);
            String name = account.getString("name");
            double balance = account.getDouble("balance");
            for(int i = 0; i < n; i++) {
                if(balances.get(i) <= balance) {
                    names.add(i, name);
                    balances.add(i, balance);
                    break;
                }
            }
        }

        HashMap<String, Double> baltop = new HashMap<>();
        for(int i = 0; i < n; i++) {
            if(!names.get(i).equals("")) {
                baltop.put(names.get(i), balances.get(i));
            }
        }

        return baltop;
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
        new BukkitRunnable() {
            String sender = trade.getSender().getName();
            @Override
            public void run() {
                for(int i = 0; i < trades.size(); i++) {
                    Trade t = trades.get(i);
                    if(t.getSender().getName().equals(sender)) {
                        if(!t.isAccepted()) {
                            t.getSender().sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "La demande de trade a expiré !");
                            t.getTarget().sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.RED + "La demande de trade a expiré !");
                            trades.remove(i);
                            break;
                        }
                    }
                }
            }
        }.runTaskLater(this, 600);
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }
}
