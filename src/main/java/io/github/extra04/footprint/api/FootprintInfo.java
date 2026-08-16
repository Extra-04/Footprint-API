package io.github.extra04.footprint.api;

import org.jetbrains.annotations.NotNull;

/**
 * 발자국 한 종류에 대한 읽기 전용 정보.
 *
 * <p>이 객체로 설정을 바꿀 수는 없다. 발자국 정의는 서버 관리자가 웹 에디터나 YAML 로
 * 관리하는 것이고, 다른 플러그인이 임의로 바꾸면 관리자가 만든 구성과 어긋나기 때문이다.
 * 플레이어가 무엇을 착용하는지를 다루려면 {@link FootprintAPI} 쪽을 쓴다.
 *
 * <p>구현체는 불변(immutable)이다. 다만 <b>이 객체를 오래 들고 있지 말 것.</b>
 * 관리자가 {@code /footprint reload} 를 하면 정의가 통째로 교체되므로, 필요할 때마다
 * {@link FootprintAPI#getFootprint(String)} 으로 다시 조회하는 편이 안전하다.
 */
public interface FootprintInfo {

    /**
     * 설정 파일에서 쓰는 고유 ID. 예: {@code dog}
     *
     * <p>{@link FootprintAPI} 의 모든 메서드가 이 값을 기준으로 동작한다.
     * YAML 파일의 최상위 키이거나, 발자국 하나만 담은 파일이라면 그 파일 이름이다.
     */
    @NotNull
    String getId();

    /**
     * GUI 와 채팅에 표시되는 이름. 색 코드({@code §b} 등)가 포함될 수 있다.
     *
     * <p>이 값은 <b>번역되지 않는다.</b> 서버 관리자가 설정 파일에 적은 문장 그대로다.
     * 플러그인이 번역하는 것은 안내 문구이지 관리자가 만든 콘텐츠가 아니다.
     */
    @NotNull
    String getDisplayName();

    /**
     * 이 발자국을 쓰기 위해 필요한 권한 노드. 예: {@code footprint.type.dog}
     *
     * <p>설정에서 {@code permission} 을 직접 지정하지 않았다면
     * {@code footprint.type.<id>} 가 쓰인다.
     */
    @NotNull
    String getPermission();

    /** 3D 발자국이면 {@code true}. GUI 에서 별도 탭에 표시된다. */
    boolean is3d();

    /** 발자국 엔티티가 쓰는 베이스 아이템 이름. 예: {@code STICK} */
    @NotNull
    String getMaterial();

    /** 왼발 모델의 CustomModelData 번호. */
    int getLeftModelData();

    /** 오른발 모델의 CustomModelData 번호. */
    int getRightModelData();
}
