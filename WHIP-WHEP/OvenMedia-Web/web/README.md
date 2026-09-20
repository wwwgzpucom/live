# 网页端（H5 直播页骨架）

供 **浏览器** 与 **安卓 WebView 壳** 共用的最简直播页。

## 文件

| 文件 | 说明 |
|------|------|
| `config.js` | **必改**：OME 主机、端口、app、默认流名 |
| `index.html` | 入口：选房间 → 主播 / 观众 |
| `host.html` | 主播：OvenLiveKit WHIP 推流 |
| `audience.html` | 观众：OvenPlayer WebRTC / LL-HLS |
| `common.css` | 样式 |

## 使用前

1. 按 [`../服务器/OME`](../服务器/OME) 或 [`../搭建文档.md`](../搭建文档.md) 启动 **OvenMediaEngine**
2. 编辑 `config.js`，把 `omeHost` 改成手机/电脑都能访问的 IP 或域名
3. 用任意静态服务器打开本目录（勿直接 `file://` 开播，权限受限）

本地快速起静态服务示例：

```bash
cd WHIP-WHEP/OvenMedia-Web/web
npx --yes serve -p 8088
```

浏览器访问：`http://本机IP:8088/`

安卓壳：把站点首页指到该入口，或打开  
`http://本机IP:8088/index.html?stream=demo&role=host`

## URL 参数

| 参数 | 说明 |
|------|------|
| `stream` / `room` / `id` | 流名 |
| `role=host\|audience` | 入口页可自动跳转 |
| `mode=webrtc\|llhls` | 观众播放协议 |

## 与安卓壳

- 始终在 App WebView 内打开本 H5
- 主播页会调用 `AndroidInterface.requestLiveMediaPermissions` / `setKeepScreenOn`（若存在）

## 注意

- 公网请上 **HTTPS / WSS**，并把 `config.js` 里 `useTls` 设为 `true`
- 本骨架不含登录鉴权；上线时自行加 Token
