const API_URL = "https://localhost:5000";

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const tokenPreview = document.getElementById("tokenPreview");

    if (loginForm) {
        loginForm.addEventListener("submit", login);
    }

    if (tokenPreview) {
        const token = localStorage.getItem("token");

        if (!token) {
            window.location.href = "index.html";
            return;
        }

        tokenPreview.textContent = token.substring(0, 60) + "...";
    }
});

async function login(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const mfa_code = document.getElementById("mfa_code").value;
    const message = document.getElementById("message");

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username,
                password,
                mfa_code
            })
        });

        const data = await response.json();

        if (!response.ok) {
            message.textContent = data.error || "Error al iniciar sesión";
            message.className = "error";
            return;
        }

        localStorage.setItem("token", data.token);

        message.textContent = "Inicio de sesión exitoso. Redirigiendo...";
        message.className = "success";

        window.location.assign("./dashboard.html");

    } catch (error) {
        console.error(error);
        message.textContent = "No se pudo conectar con el servidor HTTPS.";
        message.className = "error";
    }
}

async function callApi(endpoint, method, actionName) {
    const token = localStorage.getItem("token");

    const output = document.getElementById("responseOutput");
    const lastAction = document.getElementById("lastAction");
    const statusCode = document.getElementById("statusCode");
    const statusMessage = document.getElementById("statusMessage");

    lastAction.textContent = actionName;
    statusCode.textContent = "Cargando...";
    statusMessage.textContent = "Procesando solicitud...";
    output.textContent = "Esperando respuesta del servidor...";

    try {
        const response = await fetch(`${API_URL}${endpoint}`, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        let data;

        try {
            data = await response.json();
        } catch {
            data = {
                message: "La respuesta no tiene formato JSON."
            };
        }

        const result = {
            accion: actionName,
            endpoint: endpoint,
            metodo: method,
            estado_http: response.status,
            respuesta: data
        };

        output.textContent = JSON.stringify(result, null, 4);
        statusCode.textContent = response.status;

        if (response.ok) {
            statusMessage.textContent = "Solicitud exitosa";
            statusMessage.className = "success";
        } else {
            statusMessage.textContent = data.error || "Solicitud rechazada";
            statusMessage.className = "error";
        }

        addToHistory(actionName, endpoint, response.status);

    } catch (error) {
        console.error(error);

        output.textContent = "Error al conectar con el backend HTTPS.";
        statusCode.textContent = "ERROR";
        statusMessage.textContent = "No se pudo conectar con el servidor.";
        statusMessage.className = "error";

        addToHistory(actionName, endpoint, "ERROR");
    }
}

function addToHistory(actionName, endpoint, status) {
    const historyList = document.getElementById("historyList");

    if (!historyList) return;

    if (historyList.children.length === 1 && historyList.children[0].textContent.includes("No hay acciones")) {
        historyList.innerHTML = "";
    }

    const item = document.createElement("li");
    const time = new Date().toLocaleTimeString();

    item.textContent = `[${time}] ${actionName} → ${endpoint} → Estado: ${status}`;

    historyList.prepend(item);
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "index.html";
}