package com.antos.betterserver.scheduler;

import com.antos.betterserver.BetterServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class MuteScheduler extends BetterScheduler {
    private final BukkitRunnable bukkitRunnable = new BukkitRunnable() {
        @Override
        public void run() {
            for (OfflinePlayer player : BetterServer.getMuteManager().getMutedPlayers()) {
                if (player.isOnline() && BetterServer.getMuteManager().getMute(player).isMute()) {
                    BetterServer.getMuteManager().getMute(player).removeSecond(1);
                }
            }
        }
    };

    @Override
    public @NotNull Runnable getRunnable() {
        return bukkitRunnable;
    }

    @Override
    public void runTask() {
        BetterScheduler.tasks.add(bukkitRunnable);
        bukkitRunnable.runTaskTimer(BetterServer.getInstance(), 0, 20);
    }

    public void cancelTask() {
        bukkitRunnable.cancel();
    }
}
