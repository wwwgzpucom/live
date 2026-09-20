/**
 * OvenMedia H5 直播页配置
 * 部署前请改成你们的 OME 可达地址（手机/App 能访问的 IP 或域名）。
 */
window.LIVE_CONFIG = {
  // 例：局域网 "192.168.1.10" 或 "live.example.com"
  omeHost: "192.168.1.10",
  // 非 TLS 默认 3333；若上了 HTTPS/WSS 按实际端口改
  omeHttpPort: 3333,
  omeWsPort: 3333,
  useTls: false,
  app: "app",
  // 默认流名；可被 URL ?stream= / ?room= 覆盖
  defaultStream: "demo",

  // 页面路径（相对本目录）
  hostPage: "host.html",
  audiencePage: "audience.html",
};

window.LiveUrls = {
  streamName() {
    const q = new URLSearchParams(location.search);
    return q.get("stream") || q.get("room") || q.get("id") || LIVE_CONFIG.defaultStream;
  },
  role() {
    const q = new URLSearchParams(location.search);
    return (q.get("role") || "").toLowerCase();
  },
  httpBase() {
    const scheme = LIVE_CONFIG.useTls ? "https" : "http";
    return `${scheme}://${LIVE_CONFIG.omeHost}:${LIVE_CONFIG.omeHttpPort}`;
  },
  wsBase() {
    const scheme = LIVE_CONFIG.useTls ? "wss" : "ws";
    return `${scheme}://${LIVE_CONFIG.omeHost}:${LIVE_CONFIG.omeWsPort}`;
  },
  whip(stream) {
    const s = stream || this.streamName();
    return `${this.httpBase()}/${LIVE_CONFIG.app}/${encodeURIComponent(s)}?direction=whip`;
  },
  webrtcPlay(stream) {
    const s = stream || this.streamName();
    return `${this.wsBase()}/${LIVE_CONFIG.app}/${encodeURIComponent(s)}`;
  },
  llhls(stream) {
    const s = stream || this.streamName();
    return `${this.httpBase()}/${LIVE_CONFIG.app}/${encodeURIComponent(s)}/llhls.m3u8`;
  },
};
