package io.github.extra04.footprint.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 플레이어가 해금권 아이템으로 발자국을 해금하기 직전에 발생한다.
 *
 * <p>취소하면 권한이 부여되지 않고 <b>아이템도 소모되지 않는다.</b> 플레이어가 아이템만
 * 잃는 일은 없다.
 *
 * <p>예: 해금 로그를 남기거나, 특정 조건(레벨·재화·시즌)을 만족해야만 해금되게 할 때 쓴다.
 *
 * <h2>주의</h2>
 * 해금은 LuckPerms 로 권한을 주는 방식이다. LuckPerms 가 없으면 이 이벤트는 발생하지 않고,
 * 플레이어에게는 안내 메시지가 나간다.
 */
public class FootprintUnlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String footprintId;
    private final String permission;
    private boolean cancelled;

    public FootprintUnlockEvent(
            @NotNull Player player,
            @NotNull String footprintId,
            @NotNull String permission) {
        super(player);
        this.footprintId = footprintId;
        this.permission = permission;
    }

    /** 해금하려는 발자국 ID. */
    @NotNull
    public String getFootprintId() {
        return footprintId;
    }

    /** 부여될 권한 노드. 예: {@code footprint.type.dog} */
    @NotNull
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
