package com.flora.prefix.event;

import com.flora.prefix.Reference;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventDataManager implements Listener
{
    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!(Reference.getDataFile(uuid).canRead()))
            onCreateNewData(player, uuid);

        Reference.playerList.put(player, Objects.requireNonNull(Reference.getDataConfig(uuid).getString("title")).replaceAll("&", "§"));
        Reference.playerTitleChannel.put(player, Reference.getDataConfig(uuid).getBoolean("viewMod"));


        new BukkitRunnable() {
            @Override
            public void run() {
                Reference.onReloadNameTeam(player);
                Reference.setPlayerScoreBoard(player);
            }
        }.runTaskLaterAsynchronously(Reference.PLUGIN, 20L);
    }

    @EventHandler
    private void onPlayerExitEvent(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        Reference.playerList.remove(player);
        Reference.playerTitleChannel.remove(player);
    }

    @EventHandler
    private void onServerReloadEvent(ServerLoadEvent event)
    {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String uuid = p.getUniqueId().toString();

            Reference.playerList.put(p, Objects.requireNonNull(Reference.getDataConfig(uuid).getString("title")).replaceAll("&", "§"));
            Reference.playerTitleChannel.put(p, Reference.getDataConfig(uuid).getBoolean("viewMod"));

            Reference.onReloadNameTeam(p);
            Reference.setPlayerScoreBoard(p);
        }
    }


    private void onCreateNewData(Player player, String uuid)
    {
        FileConfiguration config = Reference.getDataConfig(uuid);
        List<Map<String, Object[]>> titleList = new ArrayList<>();

        config.set("name", player.getName());
        config.set("uuid", uuid);

        config.set("title", "");
        config.set("titleList", titleList);
        config.set("viewMod", true);

        config.set("date", LocalDate.now().toString());

        Reference.saveDataFile(config, Reference.getDataFile(uuid));
    }
}
