# 齐家婚姻 Android 客户端（OvenMedia Web 壳）

包名：`com.qjmarriage.app`  
首页：https://qjmarriage.com

用 Android Studio 打开本目录后编译安装。主要配置在 `app/src/main/assets/swv.properties`。

## 与直播方案的关系

本工程作为 **安卓访问网页端** 的壳：WebView 打开站点内 H5，由 **OvenLiveKit / OvenPlayer + OME** 完成推拉流。

详细方案见上级目录：

- [安卓访问网页端方案.md](../安卓访问网页端方案.md)
- [OvenMedia-Web README](../README.md)

## 已为直播做的改造

| 项 | 说明 |
|----|------|
| `RECORD_AUDIO` / `MODIFY_AUDIO_SETTINGS` | 开播麦克风 |
| `WebChromeClient.onPermissionRequest` | 把相机/麦克风授给网页 getUserMedia |
| `mediaPlaybackRequiresUserGesture=false` | 便于观众端自动播放 |
| 明文 HTTP（联调） | `network_security_config` + `usesCleartextTraffic`（生产请上 HTTPS） |
| JS Bridge | `AndroidInterface.requestLiveMediaPermissions` / `setKeepScreenOn` / `hasLiveMediaPermissions` |
| 外链白名单 | `external.url.exception.list` 可填 OME 域名；且 `feature.open.external.urls=false`，**始终留在壳内** |

## H5 调用示例

```javascript
// 进入主播页
if (window.AndroidInterface) {
  AndroidInterface.setKeepScreenOn(true);
  AndroidInterface.requestLiveMediaPermissions(true, true);
}
// 离开直播
AndroidInterface.setKeepScreenOn(false);
```

## 配置提示

在 `swv.properties` 中：

```properties
# OME 或直播相关域名（逗号分隔，不含协议）
external.url.exception.list=live.qjmarriage.com,ome.example.com
```
