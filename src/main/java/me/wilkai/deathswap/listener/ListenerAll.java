package me.wilkai.deathswap.listener;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.DeathswapPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Random;

public class ListenerAll implements Listener {

    private final Deathswap deathswap;
    private final DeathswapPlugin plugin;
    private final EntityType[] hostileMobs = new EntityType[] {
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CAVE_SPIDER,
            EntityType.SPIDER,
            EntityType.BLAZE,
            EntityType.CREEPER,
            EntityType.DROWNED,
            EntityType.GUARDIAN,
            EntityType.ENDERMITE,
            EntityType.HUSK,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOGLIN,
            EntityType.STRAY,
            EntityType.PHANTOM,
            EntityType.WITCH,
            EntityType.WITHER_SKELETON,
            EntityType.SILVERFISH,
            EntityType.PIGLIN_BRUTE
    };

    public ListenerAll(DeathswapPlugin plugin) {
        this.plugin = plugin;
        this.deathswap = plugin.getDeathswap();
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if(deathswap.hasStarted()) {
            deathswap.getPlayers().remove(event.getEntity());

            if(deathswap.getPlayers().size() < 2) {
                deathswap.stop();
            }
        }
    }

    @EventHandler // When an Entity attacks another Entity. (We are looking for Players specifically but there's no event for that.)
    private void onEntityHurtByEntity(EntityDamageByEntityEvent event) {
        if(deathswap.hasStarted() && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(!plugin.getDeathswap().getConfig().allowPvp) { // If Pvp is disabled.
                if(deathswap.getConfig().announcePvpDisabled) {
                    event.getDamager().sendMessage("§cCombat is Disabled!");
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler // When a Portal somewhere is created.
    private void onPortalCreate(PortalCreateEvent event) {
        // If the Portal is not the End Portal.
        if(deathswap.hasStarted() && !deathswap.getConfig().allowNether && event.getReason() != PortalCreateEvent.CreateReason.END_PLATFORM) {
            Entity entity = event.getEntity();
            if(entity != null && deathswap.getConfig().announcePortalsDisabled) {
                entity.sendMessage("§7§oGod whispers to you: Sorry buddy but we can't have any of that funny portal business around here.");
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        for(EntityType type : hostileMobs) {
            if(type == event.getEntityType()) {
                int i = new Random().nextInt(100) + 1;

                if(i > deathswap.getConfig().mobSpawningPercentage) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
