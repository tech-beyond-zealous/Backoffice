(() => {
  const redirectToLogin = (reason = "idle_timeout") => {
    window.location.href = `/login?reason=${encodeURIComponent(reason)}`;
  };

  const fetchWithSessionHandling = async (url, options = {}) => {
    const headers = new Headers(options.headers || {});
    headers.set("X-Requested-With", "fetch");
    if (!headers.has("Accept")) {
      headers.set("Accept", "application/json");
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
})();
