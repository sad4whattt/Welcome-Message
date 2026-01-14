package com.snipr.homeplugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.snipr.homeplugin.HomePlugin;
import com.snipr.homeplugin.data.HomeLocation;

import javax.annotation.Nonnull;
import java.awt.Color;

/**
 * /home command - Teleports player to their saved home location
 * Uses AbstractPlayerCommand to get proper ECS Store and Ref types for teleportation
 */
public class HomeCommand extends AbstractPlayerCommand {
    
    private final HomePlugin plugin;
    
    public HomeCommand(HomePlugin plugin) {
        super("home", "Teleport to your home location");
        this.plugin = plugin;
    }
    
    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {
        
        String playerName = playerData.getUsername();
        HomeLocation home = plugin.getHomeDataManager().getHome(playerName);
        
        if (home == null) {
            playerData.sendMessage(
                Message.join(
                    Message.raw("[Home] ").color(Color.RED),
                    Message.raw("You don't have a home set! Use /sethome <x> <y> <z> first.").color(Color.WHITE)
                )
            );
            return;
        }
        
        try {
            // Get player's current rotation
            TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
            Vector3f rotation = (transform != null) ? transform.getRotation() : Vector3f.ZERO;
            
            // Create and add teleport component
            Vector3d homePos = new Vector3d(home.getX(), home.getY(), home.getZ());
            Teleport teleport = new Teleport(world, homePos, rotation);
            store.addComponent(playerRef, Teleport.getComponentType(), teleport);
            
            playerData.sendMessage(
                Message.join(
                    Message.raw("[Home] ").color(Color.GREEN),
                    Message.raw("Teleporting to: ").color(Color.WHITE),
                    Message.raw(String.format("%.1f, %.1f, %.1f", 
                        home.getX(), home.getY(), home.getZ())).color(Color.YELLOW)
                )
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            playerData.sendMessage(
                Message.join(
                    Message.raw("[Home] ").color(Color.RED),
                    Message.raw("Teleport error: " + e.getMessage()).color(Color.WHITE)
                )
            );
        }
    }
}

