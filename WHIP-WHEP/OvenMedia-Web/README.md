# OvenMedia 官方 Web 三件套

> 本目录描述 **OvenMedia 官方最完整的浏览器直播方案**，暂不涉及 Android。

## 三件套是什么

| 组件 | 仓库 | 作用 | 许可 |
|------|------|------|------|
| **推流** | [OvenLiveKit-Web](https://github.com/OvenMediaLabs/OvenLiveKit-Web) | 浏览器 WebRTC / WHIP 推流 | MIT |
| **播放** | [OvenPlayer](https://github.com/OvenMediaLabs/OvenPlayer) | WebRTC / LLHLS / HLS / DASH 播放 | MIT |
| **服务端** | [OvenMediaEngine](https://github.com/OvenMediaLabs/OvenMediaEngine)（OME） | 超低延迟直播源站 | AGPL-3.0 |

```text
浏览器主播                源站                     浏览器观众
OvenLiveKit-Web  ──WHIP──► OvenMediaEngine ──WebRTC/LLHLS──► OvenPlayer
```

这就是官方口中的 **OME 一整套 Web 方案**：推流端 + 服务端 + 播放端协议配套完整。

## 适合什么场景

- 浏览器开播、浏览器观看
- 需要亚秒级（WebRTC）或数秒级可扩规模（LL-HLS）观看
- 希望少拼第三方推流/播放器，优先用官方组件

## 文档

- [搭建与联调](./搭建文档.md) — Docker 起 OME、OvenLiveKit 推流、OvenPlayer 播放
- [**web/**](./web/) — **网页端 H5 骨架**（入口 / 主播 / 观众）
- [**安卓访问网页端方案**](./安卓访问网页端方案.md) — App 用 WebView 打开 H5
- [**android/**](./android/) — 齐家婚姻客户端（已改造：媒体权限 / getUserMedia / JS Bridge）
- 服务器端口与 compose 也可对照：[`../服务器/OME`](../服务器/OME)

## 官方演示

- [OvenSpace Demo](https://space.ovenplayer.com/) — OME + OvenPlayer + OvenLiveKit 在线体验
- [OvenSpace 仓库](https://github.com/OvenMediaLabs/OvenSpace) — 参考实现

## 许可注意

- OvenLiveKit、OvenPlayer：**MIT**，可商用改造
- OvenMediaEngine：**AGPL-3.0**，闭源商用分发需自行评估合规
