# Footprint API

[Footprint](https://morebetter.co.kr/wiki/footprint-plugin) 플러그인에 다른 플러그인을 붙이기 위한 공개 API입니다.

이 저장소에는 **인터페이스와 이벤트만** 들어 있습니다. 플러그인 본체는 포함되지 않습니다.

## 무엇을 할 수 있나요

- 플레이어가 어떤 발자국을 착용 중인지 읽고, 바꾸기
- 발자국 표시를 켜고 끄기
- 서버에 등록된 발자국 목록과 정보 조회
- 발자국이 찍히는 순간을 감지하거나 **막기**
- 착용 변경·해금을 감지하거나 막기

## 설치

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Extra-04:Footprint-API:1.0.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Extra-04:Footprint-API:1.0.0'
}
```

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Extra-04</groupId>
    <artifactId>Footprint-API</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

`compileOnly` / `provided` 로 넣으세요. API 클래스는 서버에 설치된 Footprint 플러그인이 이미 갖고 있으므로, 여러분의 jar에 함께 넣으면 클래스가 중복됩니다.

### plugin.yml

```yaml
softdepend: [footprint]
```

`softdepend` 를 적으면 Footprint가 먼저 켜지므로 `onEnable()` 에서 바로 API를 가져올 수 있습니다.

## 빠르게 시작하기

```java
FootprintAPI api = FootprintAPI.get();
if (api == null) {
    getLogger().warning("Footprint 가 설치되어 있지 않습니다.");
    return;
}

// 지금 착용 중인 발자국
String id = api.getSelectedId(player);

// 바꾸기 (권한을 확인하지 않습니다 — 아래 주의 참고)
api.setSelected(player, "dog");

// 표시 끄기
api.setEnabled(player, false);

// 등록된 발자국 둘러보기
for (String each : api.getFootprintIds()) {
    FootprintInfo info = api.getFootprint(each);
    getLogger().info(info.getId() + " → " + info.getDisplayName());
}
```

### 권한은 확인하지 않습니다

`setSelected()` 는 **일부러** 권한을 보지 않습니다. 이벤트 보상처럼 권한 없이 지급해야 하는 경우가 있기 때문입니다. 권한을 지켜야 한다면 직접 확인하세요.

```java
if (api.hasUnlocked(player, "dog")) {
    api.setSelected(player, "dog");
}
```

## 이벤트

### 발자국이 찍힐 때

```java
@EventHandler
public void onSpawn(FootprintSpawnEvent e) {
    if (e.getPlayer().getWorld().getName().equals("arena")) {
        e.setCancelled(true);   // 이 월드에서는 발자국을 남기지 않는다
    }
}
```

> **⚠ 이 이벤트는 매우 자주 발생합니다.**
> 걷는 플레이어마다, 설정된 간격(기본 1블록)마다 한 번씩 불립니다. 리스너 안에서 파일 입출력이나
> 데이터베이스 조회를 하면 서버가 느려집니다.
>
> 리스너가 하나도 없으면 플러그인은 이벤트 객체조차 만들지 않습니다. 비용은 리스너를 등록한
> 쪽이 지는 것입니다.
>
> 월드·게임모드·권한·투명화·잠수 상태로 막고 싶다면 **이벤트를 쓰지 마세요.** Footprint의
> `config.yml` 에 있는 `exceptions` 항목이 그걸 이미 처리하며 훨씬 쌉니다.

### 착용을 바꿀 때

```java
@EventHandler
public void onSelect(FootprintSelectEvent e) {
    // 자기가 부른 API 때문에 자기 리스너가 다시 도는 걸 피한다
    if (e.getCause() == FootprintSelectEvent.Cause.API) return;

    getLogger().info(e.getPlayer().getName() + ": " + e.getPreviousId() + " → " + e.getNewId());
}
```

`getCause()` 로 `GUI` / `COMMAND` / `API` 를 구분할 수 있습니다.

### 켜고 끌 때

```java
@EventHandler
public void onToggle(FootprintToggleEvent e) {
    if (inMinigame(e.getPlayer()) && !e.isEnabling()) {
        e.setCancelled(true);   // 미니게임 중에는 끄지 못하게
    }
}
```

### 해금할 때

```java
@EventHandler
public void onUnlock(FootprintUnlockEvent e) {
    if (!hasSeasonPass(e.getPlayer())) {
        e.setCancelled(true);   // 아이템도 소모되지 않습니다
    }
}
```

취소하면 권한이 부여되지 않고 **아이템도 그대로 남습니다.** 플레이어가 아이템만 잃는 일은 없습니다.

## 주의사항

- **메인 스레드에서만 호출하세요.** 발자국은 플레이어 상태와 패킷 전송을 건드립니다.
- **`FootprintInfo` 를 오래 들고 있지 마세요.** 관리자가 `/footprint reload` 를 하면 정의가 교체됩니다. 필요할 때마다 다시 조회하세요.
- **`FootprintAPI` 를 정적 필드에 캐시하지 마세요.** `FootprintAPI.get()` 은 매번 ServicesManager 에서 가져오므로 플러그인이 다시 로드돼도 안전합니다.

## 버전

API 버전은 플러그인 본체와 따로 움직입니다. 계약이 바뀌지 않으면 본체가 올라가도 API는 그대로입니다.

## 라이선스

MIT. 자유롭게 쓰셔도 됩니다.

이 라이선스는 **이 API 저장소에만** 적용됩니다. Footprint 플러그인 본체는 별도 상품입니다.
