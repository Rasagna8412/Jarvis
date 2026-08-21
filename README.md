# ⚡ JARVIS AI — Autonomous Personal Assistant & Mobile/Laptop System Engine

![JARVIS UI Banner](https://img.shields.io/badge/JARVIS-3D_Sci--Fi_HUD-00f0ff?style=for-the-badge)
![AI Model](https://img.shields.io/badge/LLM-Llama_3.1_8B-0077ff?style=for-the-badge)
![Status](https://img.shields.io/badge/System-ONLINE_100%25-00ff66?style=for-the-badge)

JARVIS is a deterministic, multi-tier personal AI assistant engineered for **hands-free voice control**, **laptop host computer automation**, **Android Mobile OS integration**, and **24/7 cloud server deployment**.

---

## 🌟 Key Features

- 🎙️ **Always-Listening Low-Power Standby Voice Engine**: Wakes instantly on **"Hey JARVIS"**, **"JARVIS"**, or **Double Clap**. Responds out loud in a smooth female voice (`en-US-AvaNeural`).
- 🧠 **Local & Cloud AI Engine**: Powered by **Llama 3.1 8B** (via Ollama) with zero-cost cloud fallback.
- 🌐 **3D Sci-Fi Holographic Web HUD**: Live WebGL Three.js interactive visualizer at `http://localhost:8000`.
- 📱 **Android Mobile OS Dual-SIM Control**: Dial contacts (**BSNL / SIM 1 / SIM 2**), send WhatsApp messages automatically, dispatch SMS, and launch apps.
- 💻 **Laptop Host Automation**: Open/close desktop applications, take screenshots, check CPU/RAM usage.
- ☁️ **24/7 Cloud Deployment**: Deploy to **Railway.app** or **Render.com** so JARVIS stays 100% active on your phone even when your laptop is powered completely OFF!

---

## 💻 Operating JARVIS on Your Laptop

### 1. Requirements
- **Python 3.11+**
- **Ollama** (`ollama pull llama3.1`)

### 2. Startup Commands
```bash
# Navigate to backend directory
cd backend

# Activate virtual environment
.venv\Scripts\activate

# Run JARVIS server
python -m app.main
```

Or simply double-click **[`backend/run_jarvis.bat`](file:///c:/Users/LOQ/OneDrive/Desktop/jarvis1/backend/run_jarvis.bat)**!

### 3. Open 3D Sci-Fi Web UI
Open your browser and visit:
> 🔗 **http://localhost:8000**

---

## 📱 Operating JARVIS on Your Mobile Phone (3 Ways)

### Method 1: Web PWA App Icon (Recommended Instant Setup) 📲
1. Connect your mobile phone to the same Wi-Fi as your laptop.
2. Open Chrome on your phone and go to:
   > 🔗 **`http://<YOUR_LAPTOP_IP>:8000`** (e.g., `http://192.168.0.2:8000`)
3. Tap Chrome menu (**⋮ 3 dots**) $\rightarrow$ **"Add to Home Screen"** or **"Install App"**.
4. Tap **"🎙️ START VOICE MODE"** on your phone home screen app.
5. Speak **"Hey JARVIS"** into your phone anywhere in your house!

---

### Method 2: Direct USB / Wireless ADB Phone Control ⚡
Connect your phone via USB or Wireless Wi-Fi ADB:
```bash
# Verify phone connection
adb devices

# Restart in Wireless TCP Mode
adb tcpip 5555
```
JARVIS will now execute calls, WhatsApp messages, and app launches directly on your phone hardware.

---

### Method 3: 24/7 Cloud Server Mode (When Laptop is Powered OFF) ☁️
Deploy your JARVIS backend online so your mobile phone can use JARVIS 24/7 even when your laptop is turned off:

#### 1-Click Railway.app Deployment:
1. Upload this repository to GitHub.
2. Go to **[https://railway.app](https://railway.app)** $\rightarrow$ **New Project** $\rightarrow$ **Deploy from GitHub repo**.
3. Railway automatically builds using included [`nixpacks.toml`](file:///c:/Users/LOQ/OneDrive/Desktop/jarvis1/nixpacks.toml) and [`Procfile`](file:///c:/Users/LOQ/OneDrive/Desktop/jarvis1/Procfile).
4. Point your mobile phone to your permanent HTTPS URL:
   > 🔗 **`https://your-app-name.up.railway.app`**

---

## 🗣️ Voice Commands Reference

| What You Say Out Loud | Action Executed |
| :--- | :--- |
| 🗣️ *"JARVIS"* / *"Hey JARVIS"* | Wakes up JARVIS: *"Yes boss, what can I do for you?"* |
| 🗣️ *"Call Amma via BSNL"* | Dials Amma (`+919030187412`) on SIM 1. |
| 🗣️ *"Send a WhatsApp message to Rasagna saying hi"* | Opens WhatsApp and automatically transmits "hi". |
| 🗣️ *"Open Free Fire on my mobile"* | Launches Free Fire MAX on phone screen. |
| 🗣️ *"Open WhatsApp on my laptop"* | Launches WhatsApp Desktop on PC. |
| 🗣️ *"What is my phone battery?"* | Checks live phone telemetry (e.g. 48% Charging). |
| 🗣️ *"Go to sleep"* / *"Stop listening"* | Returns JARVIS to low-power standby mode. |

---

## 🔒 Security & Privacy Architecture

1. **Local-First Audio Privacy**: Standby audio is processed 100% locally on your device. Standby voice data is **never** uploaded to cloud servers or stored to disk.
2. **Explicit Permission Engine**: Configurable autonomous execution (`AUTO_APPROVE_CONFIRMATIONS=true/false`).
3. **SSRF & Sandboxing**: Restricts file reading and HTTP calls strictly within safe boundaries.

---

## 📄 License
MIT License. Built with FastAPI, Three.js, PyAutoGUI, Edge-TTS, Faster-Whisper, and Ollama.
