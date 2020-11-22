package com.flora.prefix.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryNameBox
{
    Inventory inventory;

    public InventoryNameBox(FileConfiguration config, int page)
    {
        this.inventory = inventoryTitle(config, page);
    }

    public Inventory getInventory()
    {
        return this.inventory;
    }

    public Inventory inventoryTitle(FileConfiguration config, int page)
    {
        Inventory inv = Bukkit.createInventory(null, 54, "§6ㆍ §c" + config.getString("name") + "§8의 칭호 창고 ");

        List<Map<?, ?>> titleList = config.getMapList("titleList");

        if (!(titleList.isEmpty())) {
            List<Object[]> newDataList = new ArrayList<>();
            Map mapList = titleList.get(0);

            for (Object key : mapList.keySet())
            {
                ArrayList value = (ArrayList) mapList.get(key);
                Object[] data = { key, null, null, null };

                for (int i = 0; i < value.size(); i++) { data[i + 1] = value.get(i); }
                newDataList.add(data);
            }

            int max = 36;
            int listSize = mapList.size();
            int maxPage = listSize / max;

            if (listSize % max == 0)
                maxPage--;

            for (int i = 0; i < max; i++)
            {
                int number = i + (page * max);

                if (listSize > number)
                    inv.setItem(i, InventoryIcon.iconTitle(newDataList.get(number), config));
                else
                    inv.setItem(i, new ItemStack(Material.AIR));
            }

            if (page > 0)
                inv.setItem(48, InventoryIcon.iconArrow(true));

            if (maxPage > page)
                inv.setItem(50, InventoryIcon.iconArrow(false));
        }

        if (config.getBoolean("viewMod"))
            inv.setItem(45, InventoryIcon.iconViewMod(true));
        else
            inv.setItem(45, InventoryIcon.iconViewMod(false));


        inv.setItem(49, InventoryIcon.iconCenter(page));

        for (int j = 36; j < 45; j++)
            inv.setItem(j, InventoryIcon.iconBlock());

        return inv;
    }
}
