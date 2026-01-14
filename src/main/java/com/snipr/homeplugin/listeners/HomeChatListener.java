package com.snipr.homeplugin.listeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.snipr.homeplugin.HomePlugin;
import com.snipr.homeplugin.data.HomeLocation;

import javax.annotation.Nonnull;
import java.awt.Color;

/**
 * Chat listener that intercepts home commands via chat messages
 * to bypass the operator permission requirement.
 * 
 * Supports both ! and / prefix:
 * - !home or /home - Teleport to your home
 * - !sethome <x> <y> <z> or /sethome <x> <y> <z> - Set your home location
 */
public class HomeChatListener {
    
    private static HomePlugin plugin;
    private static final HomeFormatter FORMATTER = new HomeFormatter();
    
    public static void setPlugin(HomePlugin pluginInstance) {
        plugin = pluginInstance;
    }
    
    public static void onPlayerChat(PlayerChatEvent event) {
        event.setFormatter(FORMATTER);
    }
    
    /**
     * Custom formatter that intercepts commands and processes them
     */
    private static class HomeFormatter implements PlayerChatEvent.Formatter {
        
        @Override
        @Nonnull
        public Message format(@Nonnull PlayerRef playerRef, @Nonnull String message) {
            String trimmedMessage = message.trim();
            String lowerMessage = trimmedMessage.toLowerCase();
            
            // Check for /home or !home command
            if (lowerMessage.equals("/home") || lowerMessage.equals("!home")) {
                handleHomeCommand(playerRef);
                // Return empty message to suppress chat
                return Message.raw("");
            }
            
            // Check for /sethome or !sethome command (with or without arguments)
            if (lowerMessage.equals("/sethome") || lowerMessage.startsWith("/sethome ") ||
                lowerMessage.equals("!sethome") || lowerMessage.startsWith("!sethome ")) {
                handleSetHomeCommand(playerRef, trimmedMessage);
                // Return empty message to suppress chat
                return Message.raw("");
            }
            
            // Normal chat message - format it normally
            return Message.join(
                Message.raw(playerRef.getUsername() + ": ").color(Color.WHITE),
                Message.raw(message).color(Color.WHITE)
            );
        }
        
        private void handleHomeCommand(PlayerRef playerRef) {
            String playerName = playerRef.getUsername();
            HomeLocation home = plugin.getHomeDataManager().getHome(playerName);
            
            if (home == null) {
                playerRef.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(Color.RED),
                        Message.raw("You don't have a home set! Use /sethome <x> <y> <z> first.").color(Color.WHITE)
                    )
                );
                return;
            }
            
            // Teleport using the Teleport component
            World world = Universe.get().getDefaultWorld();
            world.execute(() -> {
                try {
                    // EntityStore doesn't extend Store, so we need to use reflection for everything
                    EntityStore entityStore = world.getEntityStore();
                    
                    Ref<EntityStore> playerEntityRef = null;
                    
                    // Search through all players in the world to find matching player
                    try {
                        // Get the Collection of players using reflection
                        java.lang.reflect.Method getPlayersMethod = world.getClass().getMethod("getPlayers");
                        Object playersObj = getPlayersMethod.invoke(world);
                        
                        if (playersObj instanceof Iterable) {
                            for (Object playerObj : (Iterable<?>) playersObj) {
                                // Get player's username
                                java.lang.reflect.Method getDisplayNameMethod = playerObj.getClass().getMethod("getDisplayName");
                                String displayName = (String) getDisplayNameMethod.invoke(playerObj);
                                
                                if (displayName.equals(playerName)) {
                                    // Found the player, get its reference
                                    java.lang.reflect.Method getRefMethod = playerObj.getClass().getMethod("getReference");
                                    playerEntityRef = (Ref<EntityStore>) getRefMethod.invoke(playerObj);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        playerRef.sendMessage(
                            Message.join(
                                Message.raw("[Home] ").color(Color.RED),
                                Message.raw("Could not locate player in world.").color(Color.WHITE)
                            )
                        );
                        return;
                    }
                    
                    if (playerEntityRef == null) {
                        playerRef.sendMessage(
                            Message.join(
                                Message.raw("[Home] ").color(Color.RED),
                                Message.raw("Player not found in world.").color(Color.WHITE)
                            )
                        );
                        return;
                    }
                    
                    // Get player's current rotation using reflection
                    TransformComponent transform = null;
                    try {
                        java.lang.reflect.Method getCompMethod = entityStore.getClass().getMethod("getComponent", Ref.class, Object.class);
                        transform = (TransformComponent) getCompMethod.invoke(entityStore, playerEntityRef, TransformComponent.getComponentType());
                    } catch (Exception e) {
                        // Ignore - will use default rotation
                    }
                    Vector3f rotation = (transform != null) ? transform.getRotation() : Vector3f.ZERO;
                    
                    // Create teleport to home location
                    Vector3d homePos = new Vector3d(home.getX(), home.getY(), home.getZ());
                    Teleport teleport = new Teleport(world, homePos, rotation);
                    
                    // Add teleport component using reflection
                    try {
                        java.lang.reflect.Method addCompMethod = entityStore.getClass().getMethod("addComponent", Ref.class, Object.class, Object.class);
                        addCompMethod.invoke(entityStore, playerEntityRef, Teleport.getComponentType(), teleport);
                    } catch (Exception e) {
                        playerRef.sendMessage(
                            Message.join(
                                Message.raw("[Home] ").color(Color.RED),
                                Message.raw("Failed to teleport: " + e.getMessage()).color(Color.WHITE)
                            )
                        );
                        e.printStackTrace();
                        return;
                    }
                    
                    playerRef.sendMessage(
                        Message.join(
                            Message.raw("[Home] ").color(Color.GREEN),
                            Message.raw("Teleporting to: ").color(Color.WHITE),
                            Message.raw(String.format("%.1f, %.1f, %.1f", 
                                home.getX(), home.getY(), home.getZ())).color(Color.YELLOW)
                        )
                    );
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    playerRef.sendMessage(
                        Message.join(
                            Message.raw("[Home] ").color(Color.RED),
                            Message.raw("Teleport error: " + e.getMessage()).color(Color.WHITE)
                        )
                    );
                }
            });
        }
        
        private void handleSetHomeCommand(PlayerRef playerRef, String message) {
            String playerName = playerRef.getUsername();
            
            // Parse arguments
            String[] parts = message.split("\\s+");
            
            if (parts.length != 4) {
                playerRef.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(Color.RED),
                        Message.raw("Usage: !sethome <x> <y> <z>").color(Color.WHITE)
                    )
                );
                return;
            }
            
            try {
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                
                HomeLocation location = new HomeLocation(x, y, z, "world");
                plugin.getHomeDataManager().setHome(playerName, location);
                
                playerRef.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(new Color(85, 255, 85)),
                        Message.raw(String.format("Home location set to %.1f, %.1f, %.1f!", x, y, z)).color(Color.WHITE)
                    )
                );
            } catch (NumberFormatException e) {
                playerRef.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(Color.RED),
                        Message.raw("Invalid coordinates! Use numbers only.").color(Color.WHITE)
                    )
                );
            }
        }
    }
}
