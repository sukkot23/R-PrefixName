package com.flora.prefix;

import com.flora.prefix.command.CommandName;
import com.flora.prefix.command.TabCompleteName;
import com.flora.prefix.event.EventDataManager;
import com.flora.prefix.event.EventEditBook;
import com.flora.prefix.event.EventInteractBook;
import com.flora.prefix.event.EventInventoryClick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Reference.nameTagScoreBoard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

        Bukkit.getPluginManager().registerEvents(new EventDataManager(), this);
        Bukkit.getPluginManager().registerEvents(new EventEditBook(), this);
        Bukkit.getPluginManager().registerEvents(new EventInteractBook(), this);
        Bukkit.getPluginManager().registerEvents(new EventInventoryClick(), this);

        Objects.requireNonNull(Bukkit.getPluginCommand("prefixName")).setExecutor(new CommandName());
        Objects.requireNonNull(Bukkit.getPluginCommand("prefixName")).setTabCompleter(new TabCompleteName());
    }
}
