package com.flora.prefix.command;

import com.flora.prefix.Reference;
import com.flora.prefix.gui.InventoryNameBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class CommandName implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length > 0) {
            switch (args[0]) {
                case "생성":
                case "create":
                    onCommandCreate(sender, label, args);
                    break;

                case "제거":
                case "remove":
                    onCommandRemove(sender, label, args);
                    break;

                case "보기":
                case "view":
                    onCommandViewMod(sender, label, args);
                    break;

                default:
                    onCommandNameBox(sender);
            }
        } else
            onCommandNameBox(sender);

        return false;
    }

    private void onCommandCreate(CommandSender sender, String label, String[] args)
    {
        if (!(sender.isOp())) return;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (onCheckInventoryFull(player)) { player.sendMessage(Reference.FAIL + "§c 인벤토리를 한 칸 이상 비워주세요"); return; }

            player.getInventory().addItem(writable_titleBook());
            player.sendMessage(Reference.WARING + " '§b칭호 북§7'이 지급되었습니다");
        } else {
            sender.sendMessage(Reference.FAIL + "§c 콘솔에서 사용할 수 없습니다");
        }
    }

    private void onCommandRemove(CommandSender sender, String label, String[] args)
    {
        if (!(sender.isOp())) return;
        if (args.length < 2) { sender.sendMessage(Reference.FAIL + "§c 플레이어를 입력해주세요"); return; }
        if (args.length < 3) { sender.sendMessage(Reference.FAIL + "§c 제거할 칭호를 입력해주세요"); return; }
        if (Reference.getDataFileToName(args[1]) == null) { sender.sendMessage(Reference.FAIL + "§c 플레이어를 찾을 수 없습니다"); return; }

        FileConfiguration config = Reference.getDataConfigToName(args[1]);
        List<Map<?, ?>> titleList = config.getMapList("titleList");

        if (titleList.isEmpty()) { sender.sendMessage(Reference.FAIL + "§c 제거할 수 있는 칭호가 없습니다"); return; }
        Map<?, ?> mapList = titleList.get(0);

        boolean isFindKey = false;
        for (Object key : mapList.keySet()) { if (key.equals(args[2])) { isFindKey = true; break; } }

        if (isFindKey)
        {
            if (Objects.equals(config.getString("title"), args[2]))
                config.set("title", "");

            mapList.remove(args[2]);
            titleList.set(0, mapList);

            config.set("titleList", titleList);
            Reference.saveDataFile(config, Reference.getDataFileToName(args[1]));

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(config.getString("uuid"))));

            if (player.isOnline())
            {
                Reference.playerList.put(player.getPlayer(), "");
                Reference.onReloadNameTeam(Objects.requireNonNull(player.getPlayer()));
                player.getPlayer().sendMessage(Reference.WARING + " 관리자에 의해 " + args[2].replaceAll("&", "§") + "§7 칭호가 삭제되었습니다");
            }

            sender.sendMessage(Reference.SUCCESS + " " + args[1] + "의 " + args[2].replaceAll("&", "§") + "§7 칭호를 삭제하였습니다");

        } else {
            sender.sendMessage(Reference.FAIL + "§c 칭호를 찾을 수 없습니다");
        }

    }

    private void onCommandViewMod(CommandSender sender, String label, String[] args)
    {
        if (!(sender.isOp())) return;
        if (args.length < 2) { sender.sendMessage(Reference.FAIL + "§c 플레이어를 입력해주세요"); return; }
        if (args.length < 3) { sender.sendMessage(Reference.FAIL + "§c 값을 입력해주세요"); return; }
        if (Reference.getDataFileToName(args[1]) == null) { sender.sendMessage(Reference.FAIL + "§c 플레이어를 찾을 수 없습니다"); return; }
        if (!(args[2].equals("true") || args[2].equals("false"))) { sender.sendMessage(Reference.FAIL + "§c 값을 입력해주세요"); return; }

        boolean value;
        FileConfiguration config = Reference.getDataConfigToName(args[1]);

        value = args[2].equals("true");

        config.set("viewMod", value);
        Reference.saveDataFile(config, Reference.getDataFileToName(args[1]));

        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(config.getString("uuid"))));

        if (player.isOnline()) {
            Reference.playerTitleChannel.put(player.getPlayer(), value);
            Reference.setPlayerScoreBoard(player.getPlayer());
        }

        sender.sendMessage(Reference.SUCCESS + " " + args[1] + "의 뷰 모드를 변경하였습니다");
    }

    private void onCommandNameBox(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

            player.openInventory(new InventoryNameBox(config, 0).getInventory());
        } else {
            sender.sendMessage(Reference.FAIL + "§c 콘솔에서 사용할 수 없습니다");
        }
    }

    private boolean onCheckInventoryFull(Player player)
    {
        int stack = 0;

        for (ItemStack item : player.getInventory().getStorageContents()) if (item != null) stack++;

        return stack == player.getInventory().getStorageContents().length;
    }

    private ItemStack writable_titleBook()
    {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta)item.getItemMeta();
        List<String> lore = new ArrayList<>();

        assert meta != null;
        meta.setDisplayName("§b 칭호 북 ");
        lore.add("§d 제작할 칭호를 작성해주세요");

        meta.addPage("명칭: []\n등급: []\n\n §8대괄호 사이에\n정보을(를) 입력해주세요\n\n §8부가설명을 추가하고 싶다면\n다음 페이지를 사용해주세요");
        meta.addPage("");
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}
