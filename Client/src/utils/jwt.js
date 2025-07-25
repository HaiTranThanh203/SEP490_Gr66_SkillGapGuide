// utils/jwt.js
export function decodeJWT(token) {
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload;
  } catch (err) {
    console.error("Lỗi khi giải mã token:", err);
    return null;
  }
}

export function setupTokenExpiryWatcher(token, onExpire, onWarnBefore = null, warnBeforeMs = 60000) {
  const payload = decodeJWT(token);
  if (!payload || !payload.exp) return;

  const expTimeMs = payload.exp * 1000;
  const now = Date.now();
  const timeLeftMs = expTimeMs - now;

  if (timeLeftMs <= 0) {
    onExpire();
  } else {
    if (onWarnBefore && timeLeftMs > warnBeforeMs) {
      setTimeout(() => {
        onWarnBefore();
      }, timeLeftMs - warnBeforeMs);
    }

    setTimeout(() => {
      onExpire();
    }, timeLeftMs);
  }
}
