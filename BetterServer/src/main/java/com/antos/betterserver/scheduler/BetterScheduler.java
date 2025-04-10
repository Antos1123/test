package com.antos.betterserver.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BetterScheduler {
    static List<BukkitRunnable> tasks = new ArrayList<>();

    @NotNull
    abstract Runnable getRunnable();
    abstract void runTask();

    public static void cancelAllTasks() {
        for (BukkitRunnable runnable : tasks) {
            runnable.cancel();
        }
    }
}
