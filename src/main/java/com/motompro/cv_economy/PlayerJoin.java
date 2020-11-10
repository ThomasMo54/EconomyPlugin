package com.motompro.cv_economy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class PlayerJoin implements Listener {

    private CV_Economy plugin;

    public PlayerJoin(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        String playerUUID = player.getUniqueId().toString();
        if(!plugin.getAccountsConfig().contains(playerUUID)) {
            HashMap<String, Object> playerAccount = new HashMap<>();
            playerAccount.put("name", player.getName());
            playerAccount.put("balance", 0.0d);
            plugin.getAccountsConfig().createSection(playerUUID, playerAccount);
        } else {
            plugin.getAccountsConfig().set("name", player.getName());
        }

        plugin.saveAccountsConfig();
    }
}
