package kr.antos112.animation.api;

import dev.lone.itemsadder.api.CustomFurniture;
import kr.antos112.animation.SDAnimation;
import kr.antos112.animation.api.type.AnimationType;
import kr.antos112.animation.api.type.Direction;
import kr.antos112.animation.api.util.DirectionUtil;
import kr.antos112.animation.api.util.FurnitureUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchoolDoor {
    private Location location;
    private final String name;
    private boolean isAnimation = false;
    private CustomFurniture furniture;
    private List<Location> locations = new ArrayList<>();
    private File file;
    private YamlConfiguration config;
    private boolean isPlace = false;

    public SchoolDoor(String name, Location location) {
        this.name = name;
        this.location = location;
        file = new File(new File(SDAnimation.getInstance().getDataFolder(), "Doors"), name + ".yml");
        this.config = new YamlConfiguration();

        SDAnimation.doors.add(this);
    }

    public void place() {
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
        }

        if (FurnitureUtil.findFurnitureAtLocation(location) != null) {
            this.furniture = FurnitureUtil.findFurnitureAtLocation(location);
        }

        if (this.furniture == null) {
            this.furniture = CustomFurniture.spawnPreciseNonSolid("elitecreatures:door_4", location);
        }

        replaceSchoolDoor("door_4");
        isPlace = true;

        SDAnimation.doors.remove(this);
        SDAnimation.doors.add(this);
    }

    public void startAnimation(AnimationType type) {
        if (furniture == null) { return; }
        if (isAnimation) { return; }

        String[] doorSequence;
        if (type == AnimationType.OPEN) {
            doorSequence = new String[]{"door_4", "door_1", "door_2", "door_3"};
        } else if (type == AnimationType.CLOSE) {
            doorSequence = new String[]{"door_3", "door_2", "door_1", "door_4"};
        } else {
            throw new IllegalArgumentException("Invalid type! Type must be OPEN or CLOSE.");
        }

        isAnimation = true;

        location.getWorld().getPlayers().forEach(player -> {
            if (player.getLocation().distance(location) <= 10) {
                player.playSound(location, "minecraft:block.barrel.open", SoundCategory.BLOCKS, 1.0f, 0.8f);
            }
        });

        new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (index >= doorSequence.length) {
                    isAnimation = false;
                    location.getWorld().getPlayers().forEach(player -> {
                        if (player.getLocation().distance(location) <= 10) {
                            player.stopSound("minecraft:block.barrel.open", SoundCategory.BLOCKS);
                        }
                    });

                    cancel();
                    return;
                }

                replaceSchoolDoor(doorSequence[index]);
                index++;
            }
        }.runTaskTimer(SDAnimation.getInstance(), 0L, SDAnimation.getInstance().getConfig().getLong("SchoolDoor.animation-speed"));
    }

    public void replaceSchoolDoor(String s) {
        if (!location.getChunk().isLoaded()) {
            return;
        }

        if (furniture == null) {
            return;
        }

        furniture.replaceFurniture("elitecreatures:" + s);

        Direction d = DirectionUtil.getDirection(location);
        locations.clear();

        if (d == Direction.EAST || d == Direction.WEST) {
            for (int y = 0; y < 3; y++) {
                for (int z = -1; z < 2; z++) {
                    Location l = new Location(location.getWorld(), location.getX(), location.getY() + y, location.getZ() + z);
                    locations.add(l);
                    Block block = location.getWorld().getBlockAt(l);

                    if (s.equals("door_3")) {
                        block.setType(Material.AIR);
                    } else if (s.equals("door_1") || s.equals("door_2")) {
                        if (z == 0) {
                            block.setType(Material.AIR);
                        } else {
                            block.setType(Material.BARRIER);
                        }
                    } else {
                        block.setType(Material.BARRIER);
                    }
                }
            }
        }
        else {
            for (int y = 0; y < 3; y++) {
                for (int x = -1; x < 2; x++) {
                    Location l = new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ());
                    locations.add(l);
                    Block block = location.getWorld().getBlockAt(l);

                    if (s.equals("door_3")) {
                        block.setType(Material.AIR);
                    } else if (s.equals("door_1") || s.equals("door_2")) {
                        if (x == 0) {
                            block.setType(Material.AIR);
                        } else {
                            block.setType(Material.BARRIER);
                        }
                    } else {
                        block.setType(Material.BARRIER);
                    }
                }
            }
        }
    }

    public void saveConfig() {
        config.set("name", name);
        config.set("location", location);

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public CustomFurniture getFurniture() {
        return furniture;
    }

    public void delete() {
        if (furniture != null) {
            replaceSchoolDoor("door_3");
            furniture.remove(false);
        }

        file.delete();
        SDAnimation.doors.remove(this);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public boolean isPlace() {
        return isPlace;
    }

    public void reload() {
        this.location = config.getLocation("location");
        delete();
        place();
        SDAnimation.doors.add(this);
    }
}
