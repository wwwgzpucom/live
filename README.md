# live

自建直播工程仓库。

## 目录

```text
live/
├── MoQ/              # 前沿研究 / 演进（Media over QUIC）
└── WHIP-WHEP/        # 当下最先进且可落地（Android SDK + 服务器）
    ├── SDK/
    └── 服务器/       # SRS / ZLM / OME
```

| 目录 | 方向 | 说明 |
|------|------|------|
| [MoQ](./MoQ) | 前沿研究 / 演进 | 一套协议覆盖推流 + 分发；IETF 草案；大规模生产多在 2027+ |
| [WHIP-WHEP](./WHIP-WHEP) | 当下最先进且可落地 | WHIP（RFC 9725）推流 + WHEP 拉流；亚秒级；含工程代码 |

## 怎么选

- **马上做自家 Android 直播** → [`WHIP-WHEP`](./WHIP-WHEP)
- **跟踪下一代协议** → [`MoQ`](./MoQ)
