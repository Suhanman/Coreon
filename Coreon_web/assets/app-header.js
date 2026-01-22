const HEADER_URL = "/main/header.html";
const API_BASE = "http://localhost:8080"; // Auth-Service 고정

async function loadHeader() {
  const host = document.getElementById("app-header");
  if (!host) return;

  const res = await fetch(HEADER_URL);
  if (!res.ok) throw new Error("Failed to load header.html");
  host.innerHTML = await res.text();
}

async function fillHeaderUser() {
  const welcomeEl = document.getElementById("welcome");
  if (!welcomeEl) return;

  try {
    const res = await fetch(`${API_BASE}/api/auth/check-session`, {
      method: "GET",
      credentials: "include"
    });

    let data = {};
    try { data = await res.json(); } catch (_) {}

    // 로그인 안 됨(또는 세션 없음)
    if (!data.id) {
      welcomeEl.textContent = "";
      return;
    }

    // 세션 값이 비어있으면 디버깅용
    const username = (data.username ?? data.username ?? "").trim();
    const position = (data.position ?? "").trim();

    if (!username || !position) {
      welcomeEl.textContent = "오류/오류";
      return;
    }

    welcomeEl.textContent = `${username} / ${position} 님`;
  } catch (e) {
    console.error("check-session failed:", e);
    const el = document.getElementById("welcome");
    if (el) el.textContent = "오류/오류";
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  await loadHeader();       // 1) 헤더 DOM 주입
  await fillHeaderUser();   // 2) 주입된 헤더(#welcome)에 사용자 표시
});
