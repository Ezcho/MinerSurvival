package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


public class UnableInstallBedListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // 플레이어가 설치한 블록이 침대인지 확인
        if (event.getBlock().getType() == Material.RED_BED || event.getBlock().getType() == Material.WHITE_BED ||
                event.getBlock().getType() == Material.BLACK_BED || event.getBlock().getType() == Material.BLUE_BED ||
                event.getBlock().getType() == Material.BROWN_BED || event.getBlock().getType() == Material.CYAN_BED ||
                event.getBlock().getType() == Material.GRAY_BED || event.getBlock().getType() == Material.GREEN_BED ||
                event.getBlock().getType() == Material.LIGHT_BLUE_BED || event.getBlock().getType() == Material.LIGHT_GRAY_BED ||
                event.getBlock().getType() == Material.LIME_BED || event.getBlock().getType() == Material.MAGENTA_BED ||
                event.getBlock().getType() == Material.ORANGE_BED || event.getBlock().getType() == Material.PINK_BED ||
                event.getBlock().getType() == Material.PURPLE_BED || event.getBlock().getType() == Material.YELLOW_BED) {

            // 설치한 블록의 위치가 y 60 이하라면?
            if (event.getBlock().getLocation().getY() <= 60) {
                // 침대 설치 못하게 이벤트 취소
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "The bed cannot be installed below Y coordinate 60!");
            }
        }
    }
}
