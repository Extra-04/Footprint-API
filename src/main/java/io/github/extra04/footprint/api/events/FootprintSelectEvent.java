package io.github.extra04.footprint.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어가 착용할 발자국이 바뀌기 직전에 발생한다. 취소하면 이전 발자국이 유지된다.
 *
 * <p>GUI 클릭, {@code /footprint set}, 그리고 다른 플러그인의 API 호출까지 모두 이 이벤트를
 * 거친다. {@link #getCause()} 로 어디서 온 변경인지 구분할 수 있다 — 자기가 부른 API 때문에
 * 자기 리스너가 다시 도는 걸 피하려면 이 값을 보면 된다.
 */
public class FootprintSelectEvent extends PlayerEvent implements Cancellable {

    /** 무엇이 이 변경을 일으켰는지. */
    public enum Cause {
        /** 플레이어가 발자국 선택 GUI 에서 골랐다. */
        GUI,
        /** 관리자가 {@code /footprint set} 을 썼다. */
        COMMAND,
        /** 다른 플러그인이 API 로 바꿨다. */
        API,
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final String previousId;
    private final String newId;
    private final Cause cause;
    private boolean cancelled;

    public FootprintSelectEvent(
            @NotNull Player player,
            @Nullable String previousId,
            @NotNull String newId,
            @NotNull Cause cause) {
        super(player);
        this.previousId = previousId;
        this.newId = newId;
        this.cause = cause;
    }

    /** 바뀌기 전에 쓰던 발자국 ID. 처음 고르는 경우라면 {@code null} 일 수 있다. */
    @Nullable
    public String getPreviousId() {
        return previousId;
    }

    /** 새로 고른 발자국 ID. */
    @NotNull
    public String getNewId() {
        return newId;
    }

    /** 이 변경을 일으킨 주체. */
    @NotNull
    public Cause getCause() {
        return cause;
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
