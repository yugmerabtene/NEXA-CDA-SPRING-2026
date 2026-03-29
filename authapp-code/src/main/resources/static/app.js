const registerForm = document.getElementById("registerForm");
const loginForm = document.getElementById("loginForm");
const loadMeButton = document.getElementById("loadMeButton");
const registerResult = document.getElementById("registerResult");
const loginResult = document.getElementById("loginResult");
const meResult = document.getElementById("meResult");

let accessToken = null;

function showAlert(element, ok, message) {
  element.classList.remove("d-none", "alert-success", "alert-danger");
  element.classList.add(ok ? "alert-success" : "alert-danger");
  element.textContent = message;
}

registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const payload = {
    username: document.getElementById("registerUsername").value,
    email: document.getElementById("registerEmail").value,
    password: document.getElementById("registerPassword").value
  };

  const response = await fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  const json = await response.json();
  if (response.ok) {
    showAlert(registerResult, true, `Inscription reussie: ${json.data.email}`);
  } else {
    showAlert(registerResult, false, json.message || "Erreur inscription");
  }
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const payload = {
    email: document.getElementById("loginEmail").value,
    password: document.getElementById("loginPassword").value
  };

  const response = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  const json = await response.json();
  if (response.ok) {
    accessToken = json.data.token;
    showAlert(loginResult, true, "Connexion reussie. Token charge en memoire.");
  } else {
    showAlert(loginResult, false, json.message || "Erreur connexion");
  }
});

loadMeButton.addEventListener("click", async () => {
  const headers = accessToken ? { Authorization: `Bearer ${accessToken}` } : {};
  const response = await fetch("/api/users/me", { headers });
  const json = await response.json();
  meResult.textContent = JSON.stringify(json, null, 2);
});
