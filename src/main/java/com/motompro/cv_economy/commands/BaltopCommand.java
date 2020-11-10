package com.motompro.cv_economy.commands;

import com.motompro.cv_economy.CV_Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BaltopCommand implements CommandExecutor {

    private CV_Economy plugin;

    public BaltopCommand(CV_Economy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<String, Double> baltop = plugin.getBaltop(5);

        sender.sendMessage(CV_Economy.MESSAGE_PREFIX + ChatColor.GREEN + "Top 5 du serveur :");
        AtomicInteger i = new AtomicInteger(1);
        baltop.forEach((name, balance) -> {
            sender.sendMessage(ChatColor.GREEN + String.valueOf(i.get()) + ". " + name + " : " + ChatColor.AQUA + balance);
            i.getAndIncrement();
        });

        return false;
    }
}
