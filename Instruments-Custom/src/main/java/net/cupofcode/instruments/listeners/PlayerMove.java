package net.cupofcode.instruments.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.cupofcode.instruments.Instrument;
import net.cupofcode.instruments.InstrumentType;
import net.cupofcode.instruments.Instruments;
import net.cupofcode.instruments.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private Instruments instance = Instruments.getInstance();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (instance.getInstrumentManager().containsKey(p)) {
            Instrument instrument = instance.getInstrumentManager().get(p);
            if (!instrument.isHotBarMode()) {return;}

            if (Utils.getRegions(p) == null && Utils.getRegions(p).getRegions().isEmpty()) {return;}

            for (ProtectedRegion region : Utils.getRegions(p)) {
                if (Utils.isWorldGuard(region.getId())) {
                    Utils.loadInventory(p);

                    instrument.setHotBarMode(false);

                    FileConfiguration config = instance.getConfig();

                    p.sendMessage(config.getString("worldguard.message").replaceAll("&", "§"));
                    p.playSound(p.getLocation(),
                            config.getString("worldguard.sound.sound-name"),
                            (float) config.getDouble("worldguard.sound.volume"),
                            (float) config.getDouble("worldguard.sound.pitch"));

                    instance.getInstrumentManager().remove(event.getPlayer());

                    return;
                }
            }
        }
    }
}
