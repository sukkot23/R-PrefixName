package com.flora.prefix.event;

import com.flora.prefix.Reference;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventInteractBook implements Listener
{
    @EventHandler
    private void onUseTitleBookEvent(PlayerInteractEvent event) {
        if (checkInteractItem(event)) return;
        Player player = event.getPlayer();
        ItemMeta meta = Objects.requireNonNull(event.getItem()).getItemMeta();
        String uuid = player.getUniqueId().toString();

        assert meta != null;
        String loreName = getTitleName(Objects.requireNonNull(meta.getLore()).get(meta.getLore().size() - 1));
        String titleName = loreName.substring(0, loreName.length() - 2).replaceAll("§", "&");

        FileConfiguration config = Reference.getDataConfig(uuid);
        List<Map<?, ?>> titleList = config.getMapList("titleList");
        /* TitleName, [CustomModelData, Lore, Date] */
        Object[] value = {meta.getCustomModelData(), getListToString(meta.getLore()), LocalDate.now().toString()};

        if (titleList.isEmpty()) {
            Map<String, Object[]> titleData = new HashMap<>();
            titleData.put(titleName, value);

            titleList.add(titleData);

            config.set("titleList", titleList);
            Reference.saveDataFile(config, Reference.getDataFile(uuid));
        } else {
            Map<String, Object[]> mapList = (Map<String, Object[]>) titleList.get(0);

            if (!(mapList.containsKey(titleName)))
            {
                mapList.put(titleName, value);

                titleList.set(0, mapList);

                config.set("titleList", titleList);
                Reference.saveDataFile(config, Reference.getDataFile(uuid));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Reference.WARING + "§c 이미 동일한 칭호를 소지하고 있습니다"));
                player.playSound(player.getLocation(), Sound.ENTITY_PIG_AMBIENT, 0.5F, 1.0F);
                return;
            }
        }

        event.getItem().setAmount(0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.0F);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Reference.WARING + " '" + titleName.replaceAll("&", "§") + "§7' 칭호를 획득하셨습니다"));
    }

    private boolean checkInteractItem(PlayerInteractEvent event)
    {
        if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) return true;

        if (event.getItem() == null) return true;
        if (event.getItem().getType() != Material.ENCHANTED_BOOK) return true;
        if (!Objects.requireNonNull(event.getItem().getItemMeta()).hasLore()) return true;

        return !event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b 칭호 북 ");
    }

    private String getTitleName(String lore)
    {
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(lore);

        if (!matcher.find()) return "";
        return matcher.group(1);
    }

    private String getListToString(List<String> lore)
    {
        lore.remove(lore.size() - 1);
        String b = lore.toString();

        return b.substring(1, b.length() - 1);
    }
}
