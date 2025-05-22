package ga.guimx.gbunkers.utils;

import org.bukkit.Location;

public class LocationCheck {
    public static boolean isInside2D(Location loc, Location corner1, Location corner2) {
        if (!loc.getWorld().equals(corner1.getWorld()) || !loc.getWorld().equals(corner2.getWorld())) {
            return false; //different worlds
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());

        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double x = loc.getX();
        double z = loc.getZ();

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
    public static boolean isInside3D(Location loc, Location corner1, Location corner2) {
        if (!loc.getWorld().equals(corner1.getWorld()) || !loc.getWorld().equals(corner2.getWorld())) {
            return false; // different worlds
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());

        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());

        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }
}
