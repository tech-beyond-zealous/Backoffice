(() => {
  const CSRF_COOKIE_NAME = "GS_CSRF";
  const CSRF_HEADER_NAME = "X-CSRF-TOKEN";

  const getCookie = (name) => {
    const value = document.cookie;
    if (!value) return null;
    const parts = value.split(";");
    for (let i = 0; i < parts.length; i++) {
      const part = parts[i].trim();
      if (part.startsWith(name + "=")) {
        return part.substring(name.length + 1);
      }
    }
    return null;
  };

  const redirectToLogin = (reason = "idle_timeout") => {
    window.location.href = `/login?reason=${encodeURIComponent(reason)}`;
  };

  const fetchWithSessionHandling = async (url, options = {}) => {
    const headers = new Headers(options.headers || {});
    headers.set("X-Requested-With", "fetch");
    if (!headers.has("Accept")) {
      headers.set("Accept", "application/json");
    }
    const method = (options.method || "GET").toUpperCase();
    if (method !== "GET" && method !== "HEAD" && method !== "OPTIONS") {
      if (!headers.has(CSRF_HEADER_NAME)) {
        const token = getCookie(CSRF_COOKIE_NAME);
        if (token) {
          headers.set(CSRF_HEADER_NAME, token);
        }
      }
    }

    const response = await fetch(url, { ...options, headers });
    if (response.status === 401 && response.headers.get("X-Session-Expired") === "true") {
      redirectToLogin(response.headers.get("X-Session-Reason") || "idle_timeout");
      throw new Error("Session timed out");
    }
    if (response.redirected && response.url && response.url.includes("/login")) {
      redirectToLogin("idle_timeout");
      throw new Error("Session timed out");
    }

    return response;
  };

  window.GSAuth = window.GSAuth || {};
  window.GSAuth.redirectToLogin = redirectToLogin;
  window.GSAuth.fetchWithSessionHandling = fetchWithSessionHandling;
  window.GSAuth.getCookie = getCookie;
  window.GSAuth.csrfCookieName = CSRF_COOKIE_NAME;
  window.GSAuth.csrfHeaderName = CSRF_HEADER_NAME;
})();
