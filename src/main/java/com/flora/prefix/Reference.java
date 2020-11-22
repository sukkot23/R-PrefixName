package com.flora.prefix;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Reference
{
    public static final Plugin PLUGIN = JavaPlugin.getPlugin(Main.class);

    public static final String SUCCESS = "§7[§a ! §7]";
    public static final String WARING = "§7[§e ! §7]";
    public static final String FAIL = "§7[§c ! §7]";

    /* Player, TitleName */
    public static Map<Player, String> playerList = new HashMap<>();
    /* Player, ViewMod */
    public static Map<Player, Boolean> playerTitleChannel = new HashMap<>();
    /* NameTag Scoreboard */
    public static Scoreboard nameTagScoreBoard;


    /* Set ScoreBoard */
    public static void setPlayerScoreBoard(Player player)
    {
        if (Reference.playerTitleChannel.get(player)) {
            player.setScoreboard(Reference.nameTagScoreBoard);
            player.setPlayerListName(player.getDisplayName() + " ");
        } else {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
        }
    }

    /* NameTag ScoreBoard */
    private static void onCreateNewTeam(Player player)
    {
        String uuid = player.getUniqueId().toString();
        FileConfiguration config = Reference.getDataConfig(uuid);

        Team team = Reference.nameTagScoreBoard.registerNewTeam(player.getName());

        if (playerList.get(player).isEmpty())
            team.setPrefix("");
        else
            team.setPrefix(playerList.get(player) + "§r ");

        team.addEntry(player.getName());
    }

    public static void onReloadNameTeam(Player player)
    {
        String uuid = player.getUniqueId().toString();
        FileConfiguration config = Reference.getDataConfig(uuid);

        if (Reference.nameTagScoreBoard.getTeam(player.getName()) == null) {
            onCreateNewTeam(player);
        } else {
            Team team = Reference.nameTagScoreBoard.getTeam(player.getName());

            if (playerList.get(player).isEmpty()) {
                assert team != null;
                team.setPrefix("");
            }
            else {
                assert team != null;
                team.setPrefix(playerList.get(player) + "§r ");
            }
        }
    }



    /* Player Data File */
    public static File getDataFile(String uuid)
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata", uuid + ".dat");
    }

    public static File[] getDataFiles()
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata").listFiles();
    }

    public static FileConfiguration getDataConfig(String uuid)
    {
        return YamlConfiguration.loadConfiguration(getDataFile(uuid));
    }



    /* Save Data File */
    public static void saveDataFile(FileConfiguration config, File file)
    {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("§cFile I/O Error!!");
        }
    }



    /* Get Player Data File to PlayerName */
    public static File getDataFileToName(String playerName) throws NullPointerException
    {
        for (File file : getDataFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (Objects.requireNonNull(config.getString("name")).equalsIgnoreCase(playerName))
                return file;
        }
        return null;
    }



    /* Get Player Data File to PlayerName */
    public static FileConfiguration getDataConfigToName(String playerName) throws NullPointerException
    {
        return YamlConfiguration.loadConfiguration(Objects.requireNonNull(getDataFileToName(playerName)));
    }
}
