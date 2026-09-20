# Android Live SDK

自研 Android 推拉一体直播 SDK（**WHIP 推流 + WHEP 拉流**），面向自建直播业务。

## 模块

| 模块 | 说明 |
|------|------|
| `:live-sdk` | Kotlin 库：`LivePublisher` / `LivePlayer` |
| `:demo` | 示例 App，可直接填 WHIP/WHEP 地址联调 |

## 快速接入

```kotlin
// Application.onCreate
LiveSdk.initialize(this)

// 推流
val publisher = LiveSdk.createPublisher()
publisher.attachPreview(previewView)
publisher.setEventListener { event -> /* Connecting / Connected / Error */ }
publisher.start(
    context,
    LiveSessionConfig(
        endpoint = "https://your-server/rtc/v1/whip/?app=live&stream=room1",
        token = "optional-bearer-token",
    ),
)

// 拉流
val player = LiveSdk.createPlayer()
player.attachRenderer(rendererView)
player.start(
    LiveSessionConfig(
        endpoint = "https://your-server/rtc/v1/whep/?app=live&stream=room1",
    ),
)
```

离开页面时调用 `stop()` / `release()`。

## 协议与服务端

- 推流：**WHIP**（RFC 9725）
- 拉流：**WHEP**（IETF Draft）
- 媒体：WebRTC（H.264 / Opus，硬件编解码优先）

推荐自建源站（任选其一）：

- [SRS](https://github.com/ossrs/srs) — WebRTC WHIP/WHEP
- [ZLMediaKit](https://github.com/ZLMediaKit/ZLMediaKit)
- [OvenMediaEngine](https://github.com/OvenMediaLabs/OvenMediaEngine)

### SRS 示例地址

模拟器访问本机常用 `10.0.2.2`：

```text
WHIP: http://<host>:1985/rtc/v1/whip/?app=live&stream=demo
WHEP: http://<host>:1985/rtc/v1/whep/?app=live&stream=demo
```

真机请改成电脑局域网 IP，并保证手机与服务器同一网络；跨网需配置 STUN/TURN（`LiveSdkConfig.iceServers`）。

## 工程打开方式

1. 用 **Android Studio**（Ladybug / 更近版本）打开本目录
2. Sync Gradle 后运行 `:demo`
3. 授予相机、麦克风权限后测试推流/拉流

## 目录结构

```text
android-live-sdk/
├── live-sdk/          # SDK
│   └── src/main/java/com/gzpu/livesdk/
│       ├── LiveSdk.kt
│       ├── publish/LivePublisher.kt
│       ├── play/LivePlayer.kt
│       └── internal/  # WHIP/WHEP 信令 + WebRTC
└── demo/              # 示例 App
```

## 后续可扩展

- 美颜 / 滤镜（Camera 帧回调）
- 屏幕共享推流
- HLS 大规模观看兜底
- 断线重连、自适应码率
- MoQ 实验通道

## License

内部项目，按你们公司约定使用。
