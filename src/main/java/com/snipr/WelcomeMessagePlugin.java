package com.snipr.welcomemessage;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.snipr.welcomemessage.config.WelcomeConfig;
import com.snipr.welcomemessage.listeners.PlayerJoinListener;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        
        createDefaultConfig();
        
        this.config = this.withConfig("WelcomeMessage", WelcomeConfig.CODEC);
        
        LOGGER.atInfo().log("WelcomeMessagePlugin v" + this.getManifest().getVersion().toString() + " initialized!");
    }
    
    private void createDefaultConfig() {
        File folder = new File("mods/Snipr_WelcomeMsg");
        File file = new File(folder, "WelcomeMessage.json");
        
        if (!file.exists()) {
             folder.mkdirs();
             try (FileWriter writer = new FileWriter(file)) {
                 writer.write("{\n" +
                         "  \"WelcomeMessage\": \"Welcome to the server, {player}! Enjoy your stay!\",\n" +
                         "  \"BroadcastJoin\": true,\n" +
                         "  \"DelaySeconds\": 0\n" +
                         "}");
                 LOGGER.atInfo().log("Generated default configuration at " + file.getPath());
             } catch (IOException e) {
                 LOGGER.atInfo().log("Failed to create default config! " + e.getMessage());
                 e.printStackTrace();
             }
        }
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
