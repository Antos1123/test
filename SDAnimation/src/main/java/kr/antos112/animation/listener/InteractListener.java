package kr.antos112.animation.listener;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import kr.antos112.animation.SDAnimation;
import kr.antos112.animation.api.SchoolDoor;
import kr.antos112.animation.api.manager.SDManager;
import kr.antos112.animation.api.type.AnimationType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
    SDManager manager = SDAnimation.getSDManager();

    @EventHandler
    public void onFurnitureInteractEvent(FurnitureInteractEvent e) {
        String id = e.getNamespacedID();
        if (manager.getSchoolDoor(e.getFurniture()) != null) {
            SchoolDoor sd = manager.getSchoolDoor(e.getFurniture());

            if (id.equals("elitecreatures:door_4")) {
                sd.startAnimation(AnimationType.OPEN);
            } else if (id.equals("elitecreatures:door_3")) {
                sd.startAnimation(AnimationType.CLOSE);
            }
        }
    }

    @EventHandler
    public void onBlockInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.BARRIER) {
                    Block block = e.getClickedBlock();
                    if (manager.getSchoolDoor(block.getLocation()) != null) {
                        manager.getSchoolDoor(block.getLocation()).startAnimation(AnimationType.OPEN);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock() != null) {
            if (event.getBlock().getType() == Material.BARRIER) {
                Block block = event.getBlock();
                if (manager.getSchoolDoor(block.getLocation()) != null) {
                    manager.getSchoolDoor(block.getLocation()).delete();
                    event.getPlayer().sendMessage("§a교문이 성공적으로 제거되었습니다");
                }
            }
        }
    }

    @EventHandler
    public void onFurnitureBreakEvent(FurnitureBreakEvent e) {
        if (manager.getSchoolDoor(e.getFurniture()) != null) {
            manager.getSchoolDoor(e.getFurniture()).delete();
            e.getPlayer().sendMessage("§a교문이 성공적으로 제거되었습니다");
        }
    }
}
