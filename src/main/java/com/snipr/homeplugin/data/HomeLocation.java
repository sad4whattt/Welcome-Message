package com.snipr.homeplugin.data;

/**
 * Represents a home location with coordinates and world name.
 */
public class HomeLocation {
    
    private final double x;
    private final double y;
    private final double z;
    private final String worldName;
    
    public HomeLocation(double x, double y, double z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    @Override
    public String toString() {
        return String.format("HomeLocation{x=%.2f, y=%.2f, z=%.2f, world=%s}", x, y, z, worldName);
    }
}
