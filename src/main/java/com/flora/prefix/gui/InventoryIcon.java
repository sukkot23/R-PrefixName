package com.flora.prefix.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventoryIcon
{
    /* PrefixName */
    public static ItemStack iconTitle(Object[] data, FileConfiguration config)
    {
        ItemStack item = new ItemStack(Material.FLOWER_BANNER_PATTERN);
        ItemMeta meta= item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(((String) data[0]).replaceAll("&", "§"));

        if (isEquip(config, (String) data[0]))
        {
            meta.setDisplayName("§d" + ((String) data[0]).replaceAll("&", "§") + "§d [장착중]");
            meta.addEnchant(Enchantment.LUCK, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        List<String> lore = new ArrayList<>(getLoreFromObject((String) data[2]));
        lore.add("  §7획득일 : " + data[3]);
        lore.add("");
        lore.add("§e 좌클릭 : §a칭호 장착");
        lore.add("§e 우클릭 : §c칭호 해제");

        meta.setCustomModelData((int) data[1]);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack iconViewMod(boolean viewMod)
    {
        ItemStack item;

        if (viewMod) {
            item = new ItemStack(Material.LANTERN);
            ItemMeta meta = item.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§f칭호 보기모드 §aON");
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.SOUL_LANTERN);
            ItemMeta meta = item.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§f칭호 보기모드 §cOFF");
            item.setItemMeta(meta);
        }

        return item;
    }

    /* BLOCK */
    public static ItemStack iconBlock()
    {
        ItemStack item = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(" ");
        meta.setCustomModelData(1);

        item.setItemMeta(meta);

        return item;
    }

    /* Arrow */
    public static ItemStack iconArrow(boolean left)
    {
        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (left) {
            meta.setDisplayName("§f◀");
        } else
            meta.setDisplayName("§f▶");

        item.setItemMeta(meta);

        return item;
    }

    /* Page Number */
    public static ItemStack iconCenter(int page)
    {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§b" + (page + 1));

        item.setItemMeta(meta);

        return item;
    }

    public static List<String> getLoreFromObject(String stringLore)
    {
        return new ArrayList<>(Arrays.asList(stringLore.split(", ")));
    }

    public static boolean isEquip(FileConfiguration config, String checkTitle)
    {
        if (Objects.requireNonNull(config.getString("title")).isEmpty()) return false;

        return checkTitle.equals(Objects.requireNonNull(config.getString("title")));
    }
}
