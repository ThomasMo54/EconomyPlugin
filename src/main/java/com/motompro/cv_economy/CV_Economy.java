package com.motompro.cv_economy;

import com.motompro.cv_economy.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class CV_Economy extends JavaPlugin {

    private File accountsConfigFile;
    private FileConfiguration accountsConfig;

    public static String MESSAGE_PREFIX = ChatColor.AQUA + "CV" + ChatColor.GOLD + "Economy " + ChatColor.GRAY + "» ";

    @Override
    public void onEnable() {
        this.createAccountsConfig();

        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("take").setExecutor(new TakeCommand(this));
        getCommand("baltop").setExecutor(new BaltopCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
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
}
