# 前沿研究 / 演进 · MoQ（Media over QUIC）

## 定位

| 项 | 说明 |
|----|------|
| 方向 | **前沿研究 / 演进** |
| 协议 | **MoQ**（Media over QUIC / MoQT） |
| 目标 | 用**一套协议**覆盖推流 + 分发，兼顾**低延迟**与 **CDN 级扩规模** |
| 标准状态 | IETF 工作组草案阶段；已有多厂演示与互通试验 |
| 生产预期 | 大规模生产落地多半仍在 **2027+**；2026 宜作为并行预研 |

## 要解决的问题

当前常见直播栈往往拆成多段协议：

```text
RTMP/SRT/WHIP 推流 → 源站转封装 → HLS/LL-HLS 分发
                 ↘ WebRTC/WHEP 低延迟观看
```

MoQ 希望在 **QUIC / WebTransport** 上用统一的 **publish / subscribe** 模型，减少协议切换与重复打包，同时保留可缓存、可中继的分发形态。

## 与本仓库另一方向的关系

| 方向 | 文件夹 | 建议 |
|------|--------|------|
| 当下可落地 | [`../当下最先进且可落地/WHIP-WHEP`](../当下最先进且可落地/WHIP-WHEP) | **主研发路径**：Android SDK + SRS/ZLM/OME |
| 本目录 MoQ | 当前目录 | **预研**：跟进草案、demo、interop，不阻塞产品上线 |

WHIP/WHEP 上积累的亚秒级运维经验（ICE、丢包、鉴权、观测）未来可迁移到 MoQ 试验。

## 建议跟踪资料

- IETF MoQ WG / MoQT Transport 草案（`draft-ietf-moq-transport`）
- WebTransport / HTTP/3 相关实现
- 各厂 NAB / 互通演示与开源 client（如 MoQKit 等，以当时生态为准）

## 本目录后续可放

- 草案摘要与变更记录
- 开源客户端 / 中继试用笔记
- 与 WHIP-WHEP 的对照评测（延迟、扩规模、成本）

---

*状态：预研占位。产品联调请使用「当下最先进且可落地 / WHIP-WHEP」。*
