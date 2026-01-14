package com.snipr.welcomemessage.listeners;

import com.snipr.welcomemessage.WelcomeMessagePlugin;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

import java.awt.Color;

public class PlayerJoinListener {
    
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    /**
     * Called when a player is ready (fully connected to the server).
     * 
     * @param event The PlayerReadyEvent containing player information
     */
    public static void onPlayerJoin(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getDisplayName();
        
        String welcomeMessage = WelcomeMessagePlugin.getInstance()
            .getWelcomeConfig()
            .getWelcomeMessage();
        
        String personalizedMessage = welcomeMessage.replace("{player}", playerName);
        
        player.sendMessage(
            Message.join(
                Message.raw("[Welcome] ").color(Color.YELLOW),
                Message.raw(personalizedMessage).color(Color.GREEN)
            )
        );
        
        LOGGER.atInfo().log("Sent welcome message to player: " + playerName);
        
        if (WelcomeMessagePlugin.getInstance().getWelcomeConfig().isBroadcastJoin()) {
            LOGGER.atInfo().log("Player joined: " + playerName);
        }
    }
}
