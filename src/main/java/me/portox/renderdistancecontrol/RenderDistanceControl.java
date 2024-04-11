package me.portox.renderdistancecontrol;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class RenderDistanceControl extends JavaPlugin {

    private int premiumPlayerRenderDistance;
    private int elytraRenderDistance;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();

        config.addDefault("PremiumPlayerRenderDistance", 16);
        config.addDefault("ElytraRenderDistance", 4);

        config.options().copyDefaults(true);
        saveConfig();

        premiumPlayerRenderDistance = config.getInt("PremiumPlayerRenderDistance");
        elytraRenderDistance = config.getInt("ElytraRenderDistance");

        Logger logger = this.getLogger();

        logger.info("enabled!");

        for (Player player : getServer().getOnlinePlayers()) {
            setViewDistance(player);
        }


        RenderDistanceControl plugin = this;
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                plugin.setViewDistance(event.getPlayer());
            }

            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                if (player.hasMetadata("isAfk")) {
                    player.removeMetadata("isAfk", RenderDistanceControl.this);
                }
                if (player.isGliding() && !player.hasMetadata("isGliding")) {
                    plugin.setElytraViewDistance(player);
                    player.setMetadata("isGliding", new FixedMetadataValue(plugin, true));
                } else if (!player.isGliding() && player.hasMetadata("isGliding")) {
                    plugin.setViewDistance(player);
                    player.removeMetadata("isGliding", plugin);
                }
            }
        }, this);
    }

    private void setViewDistance(Player player) {
        if (player.hasPermission("renderdistancecontrol.premium.premiumrenderdistance")) {
            player.setViewDistance(premiumPlayerRenderDistance);
            player.setSimulationDistance(premiumPlayerRenderDistance);
            getLogger().info("set " + player.getName() + "'s render|simulation distance to " + premiumPlayerRenderDistance);
        } else {
            player.setViewDistance(getServer().getViewDistance());
            getLogger().info("set " + player.getName() + "'s render distance to default server rendering|simulation distance");
        }
    }

    private void setElytraViewDistance(Player player) {
        player.setViewDistance(elytraRenderDistance);
        player.setSimulationDistance(elytraRenderDistance);
        getLogger().info(player.getName() + " is flying, set render|simulation distance to " + elytraRenderDistance);
    }

    @Override
    public void onDisable() {
        Logger logger = this.getLogger();
        logger.info("disabled");
    }
}