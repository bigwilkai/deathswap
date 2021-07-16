package me.wilkai.deathswap.listener;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.DeathswapPlugin;
import me.wilkai.deathswap.util.Debug;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Random;

/**
 * Class which Listens for Events that are related to the Deathswap.
 */
public class ListenerAll implements Listener {

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
    public ListenerAll(DeathswapPlugin plugin) {
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
                Debug.log(entity.getCustomName() + " tried to Light a Portal.");
                entity.sendMessage(deathswap.getConfig().portalLightDenied);
            }

            event.setCancelled(true);
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

    @EventHandler // When any Projectile contacts a surface.
    private void onProjectileHit(ProjectileHitEvent event) {
        if(deathswap.hasStarted() && !deathswap.getConfig().allowPearlSkipping) { // If the Deathswap is ongoing and we don't allow Pearl Skipping.
            if(event.getEntityType() == EntityType.ENDER_PEARL) { // If the Projectile is an Ender Pearl.
                int secondsLived = event.getEntity().getTicksLived() / 20; // Find out when the Pearl was thrown.

                if(deathswap.getTimeRemaining() + secondsLived >= deathswap.getInitialSwapTime()) { // If the Pearl was thrown before the latest swap.
                    event.setCancelled(true); // Don't teleport the Player.

                    EnderPearl pearl = (EnderPearl) event.getEntity();
                    Player shooter = (Player)pearl.getShooter();

                    if(shooter == null) {
                        Debug.log("Unknown Entity tried to throw a Pearl too close to the Swap.");
                    }
                    else {
                        Debug.log(shooter.getDisplayName() + " tried to throw a Pearl too close to the Swap.");
                    }
                }
            }
        }
    }
}
