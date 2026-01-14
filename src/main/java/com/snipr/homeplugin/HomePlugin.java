package com.snipr.homeplugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.snipr.homeplugin.commands.HomeCommand;
import com.snipr.homeplugin.commands.SetHomeCommand;
import com.snipr.homeplugin.data.HomeDataManager;

import javax.annotation.Nonnull;

/**
 * HomePlugin - Allows players to set and teleport to their home location.
 * 
 * Supported commands:
 * - /home - Teleport to your home
 * - /sethome [x y z] - Set your home location
 * 
 * @author Tracks
 * @version 1.0.0
 */
public class HomePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static HomePlugin instance;
    
    private final HomeDataManager homeDataManager;

    public HomePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        
        // Initialize home data manager
        this.homeDataManager = new HomeDataManager(this);
        
        LOGGER.atInfo().log("HomePlugin v" + this.getManifest().getVersion().toString() + " initialized!");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up HomePlugin...");
        
        // Register slash commands
        this.getCommandRegistry().registerCommand(new HomeCommand(this));
        this.getCommandRegistry().registerCommand(new SetHomeCommand(this));
        
        LOGGER.atInfo().log("HomePlugin setup complete!");
        LOGGER.atInfo().log("Commands: /home, /sethome <x> <y> <z>");
    }

    /**
     * Get the plugin instance.
     */
    public static HomePlugin getInstance() {
        return instance;
    }
    
    /**
     * Get the home data manager.
     */
    public HomeDataManager getHomeDataManager() {
        return homeDataManager;
    }
}
