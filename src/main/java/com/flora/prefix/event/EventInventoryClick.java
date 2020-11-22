package com.flora.prefix.event;

import com.flora.prefix.Reference;
import com.flora.prefix.gui.InventoryNameBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class EventInventoryClick implements Listener
{
    @EventHandler
    private void onInventoryEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        if (isNameBoxInventory(event))
        {
            event.setCancelled(true);
            onClickNameBoxGUI(event, player);
        }
    }

    private void onClickNameBoxGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case FLOWER_BANNER_PATTERN:
                onClickTitle(event, player);
                break;

            case ITEM_FRAME:
                onChangePage(event, player);
                break;

            case LANTERN:
                onChangeViewMod(event, player, true);
                break;

            case SOUL_LANTERN:
                onChangeViewMod(event, player, false);
                break;
        }
    }

    private void onClickTitle(InventoryClickEvent event, Player player)
    {
        int page = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2));
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (isEquipTitle(display)) {
            if (event.getClick().isRightClick())
            {
                Reference.playerList.put(player, "");
                config.set("title", "");
                Reference.saveDataFile(config, Reference.getDataFile(player.getUniqueId().toString()));

                player.openInventory(new InventoryNameBox(config, page - 1).getInventory());
                Reference.onReloadNameTeam(player);
            }
        } else {
            if (event.getClick().isLeftClick())
            {
                Reference.playerList.put(player, display);
                config.set("title", display.replaceAll("§", "&"));
                Reference.saveDataFile(config, Reference.getDataFile(player.getUniqueId().toString()));

                player.openInventory(new InventoryNameBox(config, page - 1).getInventory());
                Reference.onReloadNameTeam(player);
            }
        }
    }

    private void onChangePage(InventoryClickEvent event, Player player)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2)) - 1;
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (display.equals("§f◀"))
            player.openInventory(new InventoryNameBox(config, pageNumber - 1).getInventory());
        else if (display.equals("§f▶"))
            player.openInventory(new InventoryNameBox(config, pageNumber + 1).getInventory());
    }

    private void onChangeViewMod(InventoryClickEvent event, Player player, Boolean value)
    {
        int page = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2));
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (value) {
            Reference.playerTitleChannel.put(player, false);
            config.set("viewMod", false);
            player.sendMessage(Reference.SUCCESS + " 칭호 표시를§c 비활성화§7 하였습니다");

        } else {
            Reference.playerTitleChannel.put(player, true);
            config.set("viewMod", true);
            player.sendMessage(Reference.SUCCESS + " 칭호 표시를§a 활성화§7 하였습니다");

        }

        Reference.setPlayerScoreBoard(player);
        Reference.saveDataFile(config, Reference.getDataFile(player.getUniqueId().toString()));

        player.openInventory(new InventoryNameBox(config, page - 1).getInventory());
    }


    private boolean isNameBoxInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§6ㆍ ") && event.getView().getTitle().contains("§8의 칭호 창고 ");
    }

    private boolean isEquipTitle(String titleName) {
        return titleName.contains("§d [장착중]");
    }
}
