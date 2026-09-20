# live

自建直播工程仓库。

## 目录

```text
live/
├── MoQ/                 # 前沿研究 / 演进（Media over QUIC）
└── WHIP-WHEP/           # 当下最先进且可落地
    ├── OvenMedia-Web/   # 官方 Web 三件套（OvenLiveKit + OvenPlayer + OME）
    ├── SDK/             # Android（另述）
    └── 服务器/          # SRS / ZLM / OME
```

| 目录 | 说明 |
|------|------|
| [MoQ](./MoQ) | 前沿预研：Media over QUIC |
| [WHIP-WHEP](./WHIP-WHEP) | 可落地方案与工程 |
| → [OvenMedia-Web](./WHIP-WHEP/OvenMedia-Web) | **官方最完整 Web 端方案**（先做浏览器） |

## 怎么选

- **先做浏览器直播** → [`WHIP-WHEP/OvenMedia-Web`](./WHIP-WHEP/OvenMedia-Web)
- **安卓 App 套网页直播（当前产品方案）** → [`安卓访问网页端方案`](./WHIP-WHEP/OvenMedia-Web/安卓访问网页端方案.md)
- **跟踪下一代协议** → [`MoQ`](./MoQ)
