package kr.antos112.animation.api.manager;

import dev.lone.itemsadder.api.CustomFurniture;
import kr.antos112.animation.api.SchoolDoor;
import org.bukkit.Location;

import java.util.List;

public interface SDManager {
    SchoolDoor getSchoolDoor(CustomFurniture furniture);
    SchoolDoor getSchoolDoor(String name);
    SchoolDoor getSchoolDoor(Location location);

    List<SchoolDoor> getSchoolDoors();

    void loadAllData();
    void saveAllData();

    void placeAll();
    void replaceAll();
}
