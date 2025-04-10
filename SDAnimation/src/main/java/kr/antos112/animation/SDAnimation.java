package kr.antos112.animation;

import dev.lone.itemsadder.api.CustomFurniture;
import kr.antos112.animation.api.SchoolDoor;
import kr.antos112.animation.api.manager.SDManager;
import kr.antos112.animation.command.SDCommand;
import kr.antos112.animation.listener.InteractListener;
import kr.antos112.animation.listener.JoinListener;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SDAnimation extends JavaPlugin {
    static Plugin instance;
    static SDManager manager;
    public static List<SchoolDoor> doors = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("§bSDAnimation is loading....");

        instance = this;

        getConfig().options().copyDefaults(true);
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveConfig();
        }
        new File(getDataFolder(), "Doors");

        manager = new SDManager() {
            @Override
            public SchoolDoor getSchoolDoor(CustomFurniture furniture) {
                if (doors.isEmpty()) {return null;}

                for (SchoolDoor door : doors) {
                    if (door.getFurniture().getEntity() != null) {
                        if (door.getFurniture().getEntity().getLocation().equals(furniture.getEntity().getLocation())) {
                            return door;
                        }
                    }
                }

                return null;
            }

            @Override
            public SchoolDoor getSchoolDoor(String name) {
                if (doors.isEmpty()) {return null;}

                for (SchoolDoor door : doors) {
                    if (door.getName().equals(name)) {
                        return door;
                    }
                }
                return null;
            }

            @Override
            public SchoolDoor getSchoolDoor(Location location) {
                if (!doors.isEmpty()) {
                    for (SchoolDoor door : doors) {
                        for (Location l : door.getLocations()) {
                            Location loc = location.getWorld().getBlockAt(l).getLocation();
                            if (loc.equals(location)) {
                                return door;
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            public List<SchoolDoor> getSchoolDoors() {
                return doors;
            }

            @Override
            public void loadAllData() {
                File[] files = new File(getDataFolder(), "Doors").listFiles();
                if (files != null) {
                    for (File file : files) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        String name = config.getString("name");

                        Location location = config.getLocation("location");
                        SchoolDoor schoolDoor = new SchoolDoor(name, location);
                    }
                }
            }

            @Override
            public void saveAllData() {
                if (!doors.isEmpty()) {
                    for (SchoolDoor door : doors) {
                        door.saveConfig();
                    }
                }
            }

            @Override
            public void placeAll() {
                List<SchoolDoor> copyList = new ArrayList<>(doors);
                for (SchoolDoor door : copyList) {
                    door.place();
                }
            }

            @Override
            public void replaceAll() {
                Iterator<SchoolDoor> iterator = doors.iterator();

                while (iterator.hasNext()) {
                    SchoolDoor door = iterator.next();
                    if (door.getFurniture() != null) {
                        door.replaceSchoolDoor("door_3");
                        door.getFurniture().remove(false);
                    }
                }

                placeAll();
            }
        };

        manager.loadAllData();

        new SDCommand();

        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        getServer().getConsoleSender().sendMessage("§aSDAnimation is enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§bSDAnimation is saving data");

        getServer().getScheduler().cancelTasks(this);
        manager.saveAllData();

        getServer().getConsoleSender().sendMessage("§aSDAnimation is disabled");
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static SDManager getSDManager() {
        return manager;
    }
}
