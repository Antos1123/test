package com.antos.betterserver.api.event;

import com.antos.betterserver.BetterServer;
import com.antos.betterserver.api.Warning;
import com.antos.betterserver.api.manager.WarningManager;
import com.antos.betterserver.api.type.WarningType;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerWarningEvent extends @NotNull Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final WarningType warningType;
    private final int num;
    private final OfflinePlayer giver;
    private boolean isCancelled = false;
    private final OfflinePlayer player;
    private final WarningManager warningManager = BetterServer.getWarningManager();
    public PlayerWarningEvent(@NotNull OfflinePlayer who, @NotNull OfflinePlayer giver , @NotNull WarningType type, @NotNull int num) {
        this.warningType = type;
        this.num = num;
        this.giver = giver;
        this.player = who;
    }

    public static HandlerList getHandlerList() {return handlers;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull WarningType getWarningType() {
        return warningType;
    }

    public @NotNull Warning getWarning() {
        return warningManager.getWarning(player);
    }

    public @NotNull int getWarningNum() {
        return num;
    }


    public @NotNull OfflinePlayer getGiver() {
        return giver;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
}