package com.snipr.welcomemessage;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.snipr.welcomemessage.config.WelcomeConfig;
import com.snipr.welcomemessage.listeners.PlayerJoinListener;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

/**
 * Welcome Messages - Sends custom welcome messages to players when they join the server.
 * 
 * This plugin demonstrates:
 * - Event handling (PlayerReadyEvent)
 * - Configuration management
 * - Chat messaging
 * 
 * @author Tracks
 * @version 1.0.0
 */
public class WelcomeMessagePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static WelcomeMessagePlugin instance;
    
    // Configuration
    private final Config<WelcomeConfig> config;

    public WelcomeMessagePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        
        this.config = this.withConfig("WelcomeMessage", WelcomeConfig.CODEC);
        
        LOGGER.atInfo().log("WelcomeMessagePlugin v" + this.getManifest().getVersion().toString() + " initialized!");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up WelcomeMessagePlugin...");
        
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoinListener::onPlayerJoin);
        
        LOGGER.atInfo().log("WelcomeMessagePlugin setup complete!");
        LOGGER.atInfo().log("Welcome message: \"" + getWelcomeConfig().getWelcomeMessage() + "\"");
    }

    public static WelcomeMessagePlugin getInstance() {
        return instance;
    }
    
    public WelcomeConfig getWelcomeConfig() {
        return this.config.get();
    }
}
