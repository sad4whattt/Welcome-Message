package com.snipr.welcomemessage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class WelcomeConfig {
    
    public static final BuilderCodec<WelcomeConfig> CODEC = BuilderCodec
            .builder(WelcomeConfig.class, WelcomeConfig::new)
            .append(new KeyedCodec<String>("WelcomeMessage", Codec.STRING),
                    (config, value, info) -> config.welcomeMessage = value,
                    (config, info) -> config.welcomeMessage)
            .add()
            .append(new KeyedCodec<Boolean>("BroadcastJoin", Codec.BOOLEAN),
                    (config, value, info) -> config.broadcastJoin = value,
                    (config, info) -> config.broadcastJoin)
            .add()
            .append(new KeyedCodec<Integer>("DelaySeconds", Codec.INTEGER),
                    (config, value, info) -> config.delaySeconds = value,
                    (config, info) -> config.delaySeconds)
            .add()
            .build();

    private String welcomeMessage = "Welcome to the server, {player}! Enjoy your stay!";
    private boolean broadcastJoin = true;
    private int delaySeconds = 0;

    public WelcomeConfig() {
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public boolean isBroadcastJoin() {
        return broadcastJoin;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }
}
