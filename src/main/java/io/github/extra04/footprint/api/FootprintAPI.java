package io.github.extra04.footprint.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * Footprint 플러그인의 공개 진입점.
 *
 * <h2>가져오는 법</h2>
 * <pre>{@code
 * FootprintAPI api = FootprintAPI.get();
 * if (api == null) {
 *     getLogger().warning("Footprint 가 설치되어 있지 않습니다.");
 *     return;
 * }
 * }</pre>
 *
 * <h2>주의: 언제 가져오는가</h2>
 * 플러그인의 {@code onEnable()} 안에서 곧바로 부르면 {@code null} 이 나올 수 있다.
 * Footprint 가 아직 켜지지 않았을 수 있기 때문이다. 두 가지 중 하나를 쓴다.
 * <ul>
 *   <li>{@code plugin.yml} 에 {@code softdepend: [footprint]} 를 적는다 (권장)</li>
 *   <li>실제로 필요해지는 시점에 {@link #get()} 을 부른다</li>
 * </ul>
 *
 * <h2>스레드</h2>
 * 모든 메서드는 <b>메인 스레드에서만</b> 불러야 한다. 발자국은 플레이어 상태와 패킷 전송을
 * 건드리므로 비동기로 호출하면 서버가 불안정해진다.
 *
 * <h2>이벤트도 있다</h2>
 * 발자국이 찍히거나 플레이어가 선택을 바꾸는 순간을 감지하거나 막고 싶다면
 * {@link io.github.extra04.footprint.api.events} 패키지를 본다.
 */
public interface FootprintAPI {

    /**
     * 서버에 설치된 Footprint 의 API 를 가져온다.
     * 플러그인이 없거나 아직 켜지지 않았으면 {@code null}.
     *
     * <p>내부적으로 Bukkit 의 {@link org.bukkit.plugin.ServicesManager} 를 쓴다.
     * 정적 필드를 두지 않으므로 플러그인이 다시 로드돼도 오래된 참조가 남지 않는다.
     */
    @Nullable
    static FootprintAPI get() {
        var registration = Bukkit.getServicesManager().getRegistration(FootprintAPI.class);
        return registration == null ? null : registration.getProvider();
    }

    /**
     * {@link #get()} 의 Optional 판. 없을 때의 처리를 강제하고 싶을 때 쓴다.
     */
    @NotNull
    static Optional<FootprintAPI> getOptional() {
        return Optional.ofNullable(get());
    }

    // ─── 발자국 정의 조회 ───

    /**
     * 서버에 등록된 모든 발자국 ID.
     *
     * <p>돌려주는 컬렉션은 수정할 수 없다. 관리자가 리로드하면 내용이 바뀌므로
     * 오래 보관하지 말 것.
     */
    @NotNull
    Collection<String> getFootprintIds();

    /**
     * ID 로 발자국 정의를 찾는다. 없으면 {@code null}.
     *
     * @param id 대소문자를 가리지 않는다.
     */
    @Nullable
    FootprintInfo getFootprint(@NotNull String id);

    // ─── 플레이어 상태 조회 ───

    /**
     * 이 플레이어가 현재 고른 발자국 ID. 고른 적이 없으면 서버의 첫 번째 발자국이 된다.
     *
     * <p>발자국 표시를 꺼둔 상태여도 고른 값은 그대로 남는다. 켜져 있는지는
     * {@link #isEnabled(Player)} 로 따로 확인한다.
     */
    @Nullable
    String getSelectedId(@NotNull Player player);

    /** 이 플레이어의 발자국 표시가 켜져 있는지. */
    boolean isEnabled(@NotNull Player player);

    /** 이 플레이어가 다른 사람의 발자국을 숨기고 있는지. */
    boolean isHidingOthers(@NotNull Player player);

    /**
     * 이 플레이어가 해당 발자국을 쓸 권한이 있는지.
     *
     * <p>{@link FootprintInfo#getPermission()} 을 확인하는 것과 같다.
     * 없는 ID 를 넘기면 {@code false}.
     */
    boolean hasUnlocked(@NotNull Player player, @NotNull String id);

    // ─── 플레이어 상태 변경 ───

    /**
     * 플레이어의 발자국을 바꾼다.
     *
     * <p><b>권한을 확인하지 않는다.</b> 이벤트 보상처럼 권한 없이 지급해야 하는 경우가 있어서
     * 일부러 그렇게 두었다. 권한을 지켜야 한다면 {@link #hasUnlocked(Player, String)} 을
     * 먼저 부를 것.
     *
     * <p>이 호출은 {@link io.github.extra04.footprint.api.events.FootprintSelectEvent} 를
     * 발생시키며, 다른 플러그인이 그것을 취소하면 바뀌지 않는다.
     *
     * @return 실제로 바뀌었으면 {@code true}. 없는 ID 이거나 이벤트가 취소되면 {@code false}.
     */
    boolean setSelected(@NotNull Player player, @NotNull String id);

    /**
     * 발자국 표시를 켜거나 끈다.
     *
     * <p>{@link io.github.extra04.footprint.api.events.FootprintToggleEvent} 를 발생시킨다.
     *
     * @return 실제로 바뀌었으면 {@code true}. 이미 그 상태였거나 이벤트가 취소되면 {@code false}.
     */
    boolean setEnabled(@NotNull Player player, boolean enabled);

    /**
     * 다른 사람의 발자국을 숨길지 정한다.
     *
     * @return 실제로 바뀌었으면 {@code true}. 이미 그 상태였으면 {@code false}.
     */
    boolean setHidingOthers(@NotNull Player player, boolean hiding);
}
