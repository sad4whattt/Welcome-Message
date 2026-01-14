package com.snipr.homeplugin.data;

import com.hypixel.hytale.logger.HytaleLogger;
import com.snipr.homeplugin.HomePlugin;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages storing and loading player home locations.
 * Stores homes in a simple text file format.
 */
public class HomeDataManager {
    
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final HomePlugin plugin;
    private final Map<String, HomeLocation> homes;
    private final File dataFile;
    
    public HomeDataManager(HomePlugin plugin) {
        this.plugin = plugin;
        this.homes = new HashMap<>();
        
        // Create data directory
        File dataFolder = new File("plugins/Snipr_HomePlugin");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        this.dataFile = new File(dataFolder, "homes.txt");
        loadHomes();
    }
    
    /**
     * Set a player's home location.
     */
    public void setHome(String playerName, HomeLocation location) {
        homes.put(playerName, location);
        saveHomes();
        LOGGER.atInfo().log("Set home for player " + playerName + " at " + location);
    }
    
    /**
     * Get a player's home location.
     */
    public HomeLocation getHome(String playerName) {
        return homes.get(playerName);
    }
    
    /**
     * Delete a player's home location.
     */
    public void deleteHome(String playerName) {
        homes.remove(playerName);
        saveHomes();
        LOGGER.atInfo().log("Deleted home for player " + playerName);
    }
    
    /**
     * Load homes from file.
     */
    private void loadHomes() {
        if (!dataFile.exists()) {
            LOGGER.atInfo().log("No homes data file found, starting fresh");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String playerName = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    String worldName = parts[4];
                    
                    homes.put(playerName, new HomeLocation(x, y, z, worldName));
                }
            }
            LOGGER.atInfo().log("Loaded " + homes.size() + " homes from file");
        } catch (IOException e) {
            LOGGER.atInfo().log("Error loading homes: " + e.getMessage());
        }
    }
    
    /**
     * Save homes to file.
     */
    private void saveHomes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Map.Entry<String, HomeLocation> entry : homes.entrySet()) {
                String playerName = entry.getKey();
                HomeLocation loc = entry.getValue();
                
                // Format: playerName,x,y,z,world
                writer.write(String.format("%s,%.2f,%.2f,%.2f,%s%n",
                    playerName,
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getWorldName()
                ));
            }
        } catch (IOException e) {
            LOGGER.atInfo().log("Error saving homes: " + e.getMessage());
        }
    }
}
