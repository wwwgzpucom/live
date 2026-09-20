# 服务器

本目录用于存放**自建直播媒体服务器**相关内容（部署脚本、配置、运维说明）。

## 推荐方案（任选其一）

| 项目 | 仓库 | 说明 |
|------|------|------|
| SRS | https://github.com/ossrs/srs | 国内常用，支持 WebRTC WHIP/WHEP |
| ZLMediaKit | https://github.com/ZLMediaKit/ZLMediaKit | 全协议，C++ 高性能 |
| OvenMediaEngine | https://github.com/OvenMediaLabs/OvenMediaEngine | 超低延迟，配套 OvenPlayer |

## 与 Android SDK 联调

客户端在 `../SDK`。典型 SRS 地址示例：

```text
WHIP: http://<host>:1985/rtc/v1/whip/?app=live&stream=demo
WHEP: http://<host>:1985/rtc/v1/whep/?app=live&stream=demo
```

真机请使用局域网 IP；跨网需配置 STUN/TURN。

## 后续可放

- Docker Compose 一键启动
- `srs.conf` / ZLM `config.ini` 示例
- Nginx 反代与 HTTPS 证书说明
- 鉴权 / Token 校验钩子
