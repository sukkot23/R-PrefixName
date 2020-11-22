package com.flora.prefix.command;

import com.flora.prefix.Reference;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TabCompleteName implements TabCompleter
{
    List<String> empty = new ArrayList<String>() {{ add(""); }};
    String[] en_commands = { "create", "remove", "view" };
    String[] ko_commands = { "생성", "제거", "보기" };


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length > 0) {
            switch (args[0]) {
                case "생성":
                case "create":
                    return empty;

                case "제거":
                case "remove":
                    return tabCommandRemove(sender, alias, args);

                case "보기":
                case "view":
                    return tabCommandViewMod(sender, alias, args);

                default:
                    return tabCommandMain(sender, alias, args[0]);
            }
        } else
            return null;
    }

    private List<String> tabCommandMain(CommandSender sender, String alias, String args)
    {
        if (alias.equals("칭호")) {
            List<String> list_ko = new ArrayList<>(Arrays.asList(ko_commands));

            if (sender.isOp()) {
                if (sender instanceof Player)
                    return tabCompleteSort(list_ko, args);
                else
                    return new ArrayList<String>() {{ add("remove"); }};
            }
            else {
                return null;
            }
        }
        else {
            List<String> list_en = new ArrayList<>(Arrays.asList(en_commands));

            if (sender.isOp()) {
                if (sender instanceof Player)
                    return tabCompleteSort(list_en, args);
                else
                    return new ArrayList<String>() {{ add("remove"); }};
            }
            else {
                return null;
            }
        }
    }

    private List<String> tabCompleteSort(List<String> list, String args)
    {
        List<String> sortList = new ArrayList<>();
        for (String s : list)
        {
            if (args.isEmpty()) return list;

            if (s.toLowerCase().startsWith(args.toLowerCase()))
                sortList.add(s);
        }
        return sortList;
    }

    private List<String> tabCommandRemove(CommandSender sender, String alias, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabPlayerList(), args[1]);
            else if (args.length == 3) {
                if (args[1].isEmpty())
                    return empty;
                else
                    return tabCompleteSort(tabTitleList(args[1]), args[2]);
            } else
                return empty;
        } else {
            return null;
        }
    }

    private List<String> tabCommandViewMod(CommandSender sender, String alias, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabPlayerList(), args[1]);
            else if (args.length == 3) {
                if (args[1].isEmpty())
                    return empty;
                else
                    return tabCompleteSort( new ArrayList<String>() {{ add("true"); add("false"); }}, args[2]);
            } else
                return empty;
        } else {
            return null;
        }
    }



    private List<String> tabPlayerList()
    {
        List<String> list = new ArrayList<>();

        for (File file : Reference.getDataFiles()) {
            FileConfiguration config = Reference.getDataConfig(file.getName().substring(0, file.getName().length() - 4));
            list.add(config.getString("name"));
        }

        return list;
    }

    private List<String> tabTitleList(String name)
    {
        List<String> list = new ArrayList<>();

        if (Reference.getDataFileToName(name) != null)
        {
            FileConfiguration config = Reference.getDataConfigToName(name);

            List<Map<?, ?>> titleList = config.getMapList("titleList");

            if (!(titleList.isEmpty())) {
                Map<?, ?> mapList = titleList.get(0);

                for (Object key : mapList.keySet())
                    list.add((String) key);
            }
        } else {
            return empty;
        }

        return list;
    }
}
