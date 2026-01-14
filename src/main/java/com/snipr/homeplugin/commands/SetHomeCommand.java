package com.snipr.homeplugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.snipr.homeplugin.HomePlugin;
import com.snipr.homeplugin.data.HomeLocation;

import javax.annotation.Nonnull;
import java.awt.Color;

/**
 * /sethome command - Sets player's home to their current location or specified coordinates
 * Syntax: /sethome [x] [y] [z]
 */
public class SetHomeCommand extends AbstractPlayerCommand {
    
    private final HomePlugin plugin;
    
    public SetHomeCommand(HomePlugin plugin) {
        super("sethome", "Set your home location. Usage: /sethome or /sethome <x> <y> <z>");
        this.plugin = plugin;
        setAllowsExtraArguments(true);
    }
    
    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {
        
        String playerName = playerData.getUsername();
        Vector3d homePos;
        
        // Parse arguments from input string
        String[] args = ctx.getInputString().trim().split("\\s+");
        
        if (args.length == 1) {
            // Use current position
            TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
            if (transform == null) {
                playerData.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(Color.RED),
                        Message.raw("Could not get your position!").color(Color.WHITE)
                    )
                );
                return;
            }
            homePos = transform.getPosition();
        } else if (args.length == 4) {
            // Parse provided coordinates
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                homePos = new Vector3d(x, y, z);
            } catch (NumberFormatException e) {
                playerData.sendMessage(
                    Message.join(
                        Message.raw("[Home] ").color(Color.RED),
                        Message.raw("Invalid coordinates! Use /sethome <x> <y> <z> or just /sethome for current position.").color(Color.WHITE)
                    )
                );
                return;
            }
        } else {
            playerData.sendMessage(
                Message.join(
                    Message.raw("[Home] ").color(Color.RED),
                    Message.raw("Usage: /sethome or /sethome <x> <y> <z>").color(Color.WHITE)
                )
            );
            return;
        }
        
        // Save home
        HomeLocation home = new HomeLocation(homePos.x, homePos.y, homePos.z, "world");
        plugin.getHomeDataManager().setHome(playerName, home);
        
        playerData.sendMessage(
            Message.join(
                Message.raw("[Home] ").color(Color.GREEN),
                Message.raw("Home set to: ").color(Color.WHITE),
                Message.raw(String.format("%.1f, %.1f, %.1f", 
                    homePos.x, homePos.y, homePos.z)).color(Color.YELLOW)
            )
        );
    }
}
