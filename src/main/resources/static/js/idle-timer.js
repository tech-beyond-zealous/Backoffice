(() => {
  const minutes = Number(window.GS_IDLE_TIMEOUT_MINUTES);
  const idleTimeoutMs = (Number.isFinite(minutes) && minutes > 0 ? minutes : 30) * 60 * 1000;
  const debug = window.GS_IDLE_DEBUG === undefined ? true : Boolean(window.GS_IDLE_DEBUG);
  const key = "gs.lastActivityAtMs";
  let expired = false;
  let lastActivityAtMs = Date.now();
  let lastActivityLogAtMs = 0;
  let lastStatusLogAtMs = 0;

  const log = (...args) => {
    if (!debug) return;
    console.log("[GoSmart][IdleTimer]", ...args);
  };

  const storage = (() => {
    try {
      const probe = "__gs_probe__";
      window.localStorage.setItem(probe, "1");
      window.localStorage.removeItem(probe);
      return window.localStorage;
    } catch (e) {
      return null;
    }
  })();

  const writeLastActivityAt = (ms) => {
    lastActivityAtMs = ms;
    if (!storage) return;
    try {
      storage.setItem(key, String(ms));
    } catch (e) {
    }
  };

  const readLastActivityAt = () => {
    if (!storage) return lastActivityAtMs;
    try {
      const raw = storage.getItem(key);
      const last = raw ? Number(raw) : NaN;
      return Number.isFinite(last) ? last : lastActivityAtMs;
    } catch (e) {
      return lastActivityAtMs;
    }
  };

  const markActive = (evt) => {
    if (expired) return;
    writeLastActivityAt(Date.now());
    const now = Date.now();
    if (now - lastActivityLogAtMs >= 15000) {
      lastActivityLogAtMs = now;
      const evtName = evt && evt.type ? evt.type : "manual";
      log("activity", evtName, "lastActivityAtMs=", readLastActivityAt());
    }
  };

  const onExpire = async () => {
    if (expired) return;
    expired = true;
    log("expired", "idleTimeoutMs=", idleTimeoutMs, "lastActivityAtMs=", readLastActivityAt());
    log("redirecting to /login?reason=idle_timeout");
    window.location.href = "/login?reason=idle_timeout";
  };

  const check = () => {
    if (expired) return;
    const lastActivityAt = readLastActivityAt();
    const now = Date.now();
    const idleForMs = now - lastActivityAt;
    if (now - lastStatusLogAtMs >= 30000) {
      lastStatusLogAtMs = now;
      const remainingMs = Math.max(0, idleTimeoutMs - idleForMs);
      log("check", "idleForMs=", idleForMs, "remainingMs=", remainingMs);
    }
    if (idleForMs >= idleTimeoutMs) {
      onExpire();
    }
  };

  ["click", "mousemove", "keydown", "scroll", "touchstart"].forEach((evt) => {
    window.addEventListener(evt, markActive, { passive: true });
  });
  window.addEventListener("focus", check);
  document.addEventListener("visibilitychange", check);
  window.addEventListener("storage", (e) => {
    if (e.key === key) check();
  });
  window.addEventListener("load", markActive);

  markActive({ type: "init" });

  log("init", { minutes, idleTimeoutMs, storage: Boolean(storage), lastActivityAtMs: readLastActivityAt() });

  window.setInterval(check, 5000);
  check();

})();
