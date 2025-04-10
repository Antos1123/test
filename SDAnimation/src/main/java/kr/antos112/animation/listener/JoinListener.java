package kr.antos112.animation.listener;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import kr.antos112.animation.SDAnimation;
import kr.antos112.animation.api.SchoolDoor;
import kr.antos112.animation.api.manager.SDManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Iterator;

import static kr.antos112.animation.SDAnimation.doors;

public class JoinListener implements Listener {
    boolean isFirstJoin = true;
    SDManager manager = SDAnimation.getSDManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (isFirstJoin) {
            manager.placeAll();
            isFirstJoin = false;
        }
    }
}
