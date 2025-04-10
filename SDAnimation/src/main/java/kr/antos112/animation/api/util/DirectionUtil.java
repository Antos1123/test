package kr.antos112.animation.api.util;

import kr.antos112.animation.api.type.Direction;
import org.bukkit.Location;

public final class DirectionUtil {
    public static Direction getDirection(Location location) {
        double yaw = (location.getYaw() + 360) % 360;

        if (yaw >= 45 && yaw < 135) {
            return Direction.EAST;
        } else if (yaw >= 135 && yaw < 225) {
            return Direction.SOUTH;
        } else if (yaw >= 225 && yaw < 315) {
            return Direction.WEST;
        } else {
            return Direction.NORTH;
        }
    }
}
