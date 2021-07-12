package me.wilkai.deathswap;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.DeathswapPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Random;

/**
 * Class which Listens for Events that are related to the Deathswap.
 */
public class EventListener implements Listener {

    /**
     * The Active Deathswap Instance.
     */
    private final Deathswap deathswap;

    /**
     * The Active Plugin Instance.
     */
    private final DeathswapPlugin plugin;

    /**
     * An array containing all Entity Types which will have their Spawn Rate affected by the Mob Spawn Rate in the Config.
     */
    private final EntityType[] hostileMobs = new EntityType[] {
            EntityType.HUSK,
            EntityType.BLAZE,
            EntityType.GHAST,
            EntityType.STRAY,
            EntityType.WITCH,
            EntityType.HOGLIN,
            EntityType.SPIDER,
            EntityType.ZOMBIE,
            EntityType.ZOGLIN,
            EntityType.CREEPER,
            EntityType.DROWNED,
            EntityType.PHANTOM, // Fuck these things.
            EntityType.SKELETON,
            EntityType.ENDERMAN,
            EntityType.MAGMA_CUBE,
            EntityType.CAVE_SPIDER,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.WITHER_SKELETON
    };

    /**
     * Creates a new Instance of the Event Listener Class with the Plugin provided,
     * @param plugin The instance of the Deathswap Plugin.
     */
    public EventListener(DeathswapPlugin plugin) {
        this.plugin = plugin;
        this.deathswap = plugin.getDeathswap();
    }

    @EventHandler // Listens for when a Player dies so we know if someone got knocked out.
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
                event.setCancelled(true);
            }
        }
    }

    @EventHandler // When a Portal somewhere is created.
    private void onPortalCreate(PortalCreateEvent event) {
        // If the Portal is not the End Portal.
        if(deathswap.hasStarted() && !deathswap.getConfig().allowNether && event.getReason() != PortalCreateEvent.CreateReason.END_PLATFORM) {
            Entity entity = event.getEntity();

            if(entity != null) { // Send the Entity who lit the portal (if known) a nice friendly message.
                entity.sendMessage(deathswap.getConfig().portalLightDenied);
            }
        }
    }

    @EventHandler // Called when an Entity Spawns, used by the Mob Spawn Rate modifier to control the amount of Hostile Mobs in the game.
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if(deathswap.hasStarted()) {
            for(EntityType type : hostileMobs) {
                if(type == event.getEntityType()) {
                    // Generate a random number between 1 & 100
                    int i = new Random().nextInt(100) + 1;

                    // If the number is larger than the Mob Spawn Rate, don't let the mob spawn.
                    // This means if the Mob Spawn Rate is 0 no mobs can spawn as "i" never drops below 1.
                    // And on the other hand, if it is set to 100 mobs will always spawn because "i" can never increase above 100.
                    // In other words, it's a percentage.
                    if(i > deathswap.getConfig().mobSpawnRate) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }

    }

    @EventHandler // Called when a Player leaves the Server
    private void onPlayerQuit(PlayerQuitEvent event) {
        if(deathswap.hasStarted()) {
            Player player = event.getPlayer();

            if(deathswap.getConfig().kickPlayersOnLeave && deathswap.getPlayers().contains(player)) {
                Bukkit.broadcastMessage(deathswap.getConfig().playerLeaveServer.replace("<player>", player.getDisplayName()));
                deathswap.getPlayers().remove(player);

                if(deathswap.getPlayers().size() < 2) {
                    deathswap.stop();
                }
            }
        }
    }
}
