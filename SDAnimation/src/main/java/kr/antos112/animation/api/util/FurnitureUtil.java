package kr.antos112.animation.api.util;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class FurnitureUtil {
    public static CustomFurniture findFurnitureAtLocation(Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        List<Entity> nearbyArmorStands = new ArrayList<>();
        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                nearbyArmorStands.add(entity);
            }
        }

        for (Entity entity : nearbyArmorStands) {
            CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
            if (furniture != null) {
                return furniture;
            }
        }
        return null;
    }
}
