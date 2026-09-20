# 服务器

本目录存放**自建直播媒体服务器**相关内容。

## 子目录

| 文件夹 | 说明 | 搭建文档 |
|--------|------|----------|
| [SRS](./SRS) | OSSRS，WHIP/WHEP 友好，推荐与 Android SDK 联调 | [搭建文档](./SRS/搭建文档.md) |
| [ZLM](./ZLM) | ZLMediaKit，全协议高性能 | [搭建文档](./ZLM/搭建文档.md) |
| [OME](./OME) | OvenMediaEngine，超低延迟 | [搭建文档](./OME/搭建文档.md) |

每个子目录均包含：

- `搭建文档.md` — 安装、端口、WHIP/WHEP 地址、对接 SDK、排错
- `docker-compose.yml` — Docker 一键启动示例

## 与 Android SDK 联调建议

1. 优先起 **SRS** 或 **ZLM**（WHEP 拉流路径清晰）
2. 手机与服务器同一局域网，填写真实局域网 IP
3. 放行 WebRTC 媒体 UDP 端口（SRS: `8000`；ZLM: `8000`；OME: `10000–10009`）

SDK 工程见同级目录 [`../SDK`](../SDK)。
