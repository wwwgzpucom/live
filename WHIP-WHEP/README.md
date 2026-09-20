# WHIP + WHEP

## 定位

| 项 | 说明 |
|----|------|
| 方向 | **当下最先进且可落地** |
| 协议 | **WHIP + WHEP**（基于 WebRTC）及 OvenMedia 官方 Web 栈 |
| 能力 | 标准化推流 / 低延迟观看；Web 与后续移动端工程资料 |

## 本目录

| 文件夹 | 说明 |
|--------|------|
| [**OvenMedia-Web**](./OvenMedia-Web) | **官方 Web 三件套**：OvenLiveKit + OvenPlayer + OvenMediaEngine（先做浏览器，不含安卓） |
| [SDK](./SDK) | Android 推拉一体 SDK（另述） |
| [服务器](./服务器) | SRS / ZLM / OME 分目录搭建与 Docker Compose |

## 推荐阅读顺序

1. 打开 [`OvenMedia-Web`](./OvenMedia-Web) 了解官方 Web 三件套  
2. 按 [`OvenMedia-Web/搭建文档.md`](./OvenMedia-Web/搭建文档.md) 跑通浏览器推拉流  
3. 产品路径：[`OvenMedia-Web/安卓访问网页端方案.md`](./OvenMedia-Web/安卓访问网页端方案.md)（安卓壳 + H5 直播）  
4. 服务器细节对照 [`服务器/OME`](./服务器/OME)

MoQ 预研见 [`../MoQ`](../MoQ)。
