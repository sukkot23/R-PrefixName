package com.flora.prefix.event;

import com.flora.prefix.Reference;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EventEditBook implements Listener
{
    @EventHandler
    private void onCreateTitleBookEvent(PlayerEditBookEvent event)
    {
        if (!(event.getPlayer().isOp())) return;
        Player player = event.getPlayer();
        BookMeta bookMeta = event.getNewBookMeta();

        if (bookMeta.getDisplayName().equals("§b 칭호 북 ") && event.isSigning())
        {
            if (getTitleName(bookMeta.getPage(1), 1).isEmpty()) { player.sendMessage(Reference.FAIL + "§c 칭호명은 필수 입력 사항입니다"); return; }

            new BukkitRunnable() {
                @Override
                public void run() { player.getInventory().setItemInMainHand(titleBook(bookMeta)); }
            }.runTaskLaterAsynchronously(Reference.PLUGIN, 5L);
        }
    }

    private ItemStack titleBook(BookMeta bookMeta)
    {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        String titleName = getTitleName(bookMeta.getPage(1).replaceAll("&", "§"), 1);
        int rank;

        try {
            rank = Integer.parseInt(getTitleName(bookMeta.getPage(1), 2));
        } catch (NumberFormatException exception) {
            rank = 0;
        }

        if (bookMeta.getPageCount() > 1)
        {
            String lorePage = bookMeta.getPage(2);
            String[] a = lorePage.split("\n");

            for (String l : a) lore.add(l.replaceAll("&", "§"));
        }
        lore.add(" §7우클릭 시 '" + titleName + "§7' 칭호를 획득할 수 있습니다");

        assert meta != null;
        meta.setDisplayName(bookMeta.getDisplayName());
        meta.setCustomModelData(rank);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private String getTitleName(String pageString, int mod)
    {
        Pattern pattern = Pattern.compile("\\[(.*)]");
        String[] a = pageString.split(":");

        switch (mod) {
            case 1:
                Matcher m1 = pattern.matcher(a[1]);
                if (!m1.find()) return "";
                return m1.group(1);
            case 2:
                Matcher m2 = pattern.matcher(a[2]);
                if (!m2.find()) return "";
                return m2.group(1);
        }
        return "";
    }
}
