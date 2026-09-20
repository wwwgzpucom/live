# 当下最先进且可落地 · WHIP + WHEP

## 定位

| 项 | 说明 |
|----|------|
| 方向 | **当下最先进且可落地** |
| 协议 | **WHIP + WHEP**（基于 WebRTC） |
| 推流 | **WHIP**（WebRTC-HTTP Ingestion）— **RFC 9725**（已定稿） |
| 拉流 | **WHEP**（WebRTC-HTTP Egress）— IETF 草案，已广泛试用 |
| 能力 | 标准化推流入口 / 拉流出口，**亚秒级**延迟 |

## 本目录内容（已从仓库根目录迁入）

| 文件夹 | 说明 |
|--------|------|
| [SDK](./SDK) | Android 推拉一体 SDK（WHIP 推流 + WHEP 拉流）及 Demo |
| [服务器](./服务器) | 自建媒体服务器：SRS / ZLM / OME 搭建文档与 Docker Compose |

## 推荐联调路径

1. 按 [服务器/SRS/搭建文档.md](./服务器/SRS/搭建文档.md) 或 [服务器/ZLM/搭建文档.md](./服务器/ZLM/搭建文档.md) 起源站  
2. Android Studio 打开 [SDK](./SDK) 运行 Demo  
3. 填写 WHIP / WHEP 地址联调

## 与 MoQ 预研的关系

MoQ 见 [`../../前沿研究-演进/MoQ`](../../前沿研究-演进/MoQ)。  
**产品主路径以本目录为准**；MoQ 作并行跟踪，不替代当前 WHIP/WHEP 交付。
