package io.github.extra04.footprint.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 발자국 하나가 찍히기 직전에 발생한다. 취소하면 그 발자국은 나타나지 않는다.
 *
 * <h2>⚠ 이 이벤트는 매우 자주 발생한다</h2>
 * 걷는 플레이어마다, 설정된 간격(기본 1블록)마다 한 번씩 불린다. 20명이 뛰어다니면
 * 초당 수십 번이다. <b>리스너 안에서 무거운 일을 하지 말 것</b> — 파일 입출력, 데이터베이스
 * 조회, 동기 네트워크 호출은 서버를 그대로 끌어내린다.
 *
 * <p>플러그인은 리스너가 하나도 없으면 이 이벤트 객체를 아예 만들지 않는다. 따라서
 * 이 이벤트를 쓰지 않는 서버에는 부담이 없다. 다만 리스너를 등록하는 순간 그 비용은
 * 등록한 쪽이 지는 것이다.
 *
 * <p>특정 상황에서만 막고 싶다면, 그 조건을 서버 설정으로 처리할 수 있는지 먼저 보라.
 * {@code config.yml} 의 {@code exceptions} 항목이 투명화·잠수·관전 모드·월드·게임모드·권한별
 * 예외를 이미 다룬다. 그걸로 되는 일이라면 이벤트보다 그쪽이 훨씬 싸다.
 *
 * <h2>취소했을 때</h2>
 * 그 자리에 발자국이 생기지 않을 뿐, 왼발·오른발 교대 순서는 그대로 유지된다.
 * 다음 걸음에서 정상적으로 이어진다.
 */
public class FootprintSpawnEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String footprintId;
    private final Location location;
    private final boolean leftFoot;
    private boolean cancelled;

    public FootprintSpawnEvent(
            @NotNull Player player,
            @NotNull String footprintId,
            @NotNull Location location,
            boolean leftFoot) {
        super(player);
        this.footprintId = footprintId;
        this.location = location;
        this.leftFoot = leftFoot;
    }

    /** 찍히려는 발자국의 ID. */
    @NotNull
    public String getFootprintId() {
        return footprintId;
    }

    /**
     * 발자국이 놓일 위치.
     *
     * <p>이미 지형 보정(반 블록, 울타리, 영혼모래 함몰 등)이 끝난 좌표다.
     * 돌려주는 객체는 복사본이라 고쳐도 실제 위치에 영향을 주지 않는다.
     */
    @NotNull
    public Location getLocation() {
        return location.clone();
    }

    /** 이번 발자국이 왼발이면 {@code true}, 오른발이면 {@code false}. */
    public boolean isLeftFoot() {
        return leftFoot;
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
