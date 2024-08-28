package org.example.code.rpg.Event;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MonsterDamageListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            // Increase monster speed
            if (monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Monster) {
            Monster monster = (Monster) damager;
            // Increase damage based on monster type
            if (monster instanceof Zombie) {
                event.setDamage(event.getDamage() * 1.5); // Increase damage by 1.5x
            } else if (monster instanceof Spider) {
                event.setDamage(event.getDamage() * 1.5); // Increase damage by 1.5x
            }
        }

        // If the entity was hit by an arrow
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            // If the shooter is a Skeleton and the target is a Player
            if (arrow.getShooter() instanceof Skeleton && event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                // Apply Wither effect to the player
                PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 200, 0); // 10 seconds duration
                player.addPotionEffect(wither);
            }
        }
    }
}
