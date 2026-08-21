# JARVIS: Technical Implementation & Architecture Specification

## 1. Project Objectives & Two-Stage Vision

JARVIS is a multi-device, multilingual, voice-controlled, 3D AI personal assistant designed around a mandatory core principle: **Strict separation of Intelligence (LLMs/Agents) from Execution (Deterministic Tool & Permission Engine)**.

The system is structured into two distinct evolutionary stages:

- **Stage 1 (Current Project - Engineering Baseline)**:
  - Implemented using production-ready frameworks: Python, FastAPI, WebSockets, Ollama, React, Three.js, and Android APIs.
  - Serves as a **working, modular engineering reference implementation**.
  - All components connect via abstract interfaces and protocols to guarantee clean separation of concerns.
- **Stage 2 (Future Self-Built Architectural Progression)**:
  - Serves as the baseline for deeper personal rebuild post AI/ML coursework.
  - Core conceptual contracts (`AIProvider`, `Agent`, `Orchestrator`, `Tool`, `PermissionEngine`, `MemoryStore`, `ResultVerifier`) are designed for drop-in replacement with custom ML models, custom memory retrievers, or specialized orchestrators without rewriting the surrounding application framework.

---

## 2. Hardware & Environment Stack

### Host Hardware Inspection
- **Operating System**: Windows 11 Home Single Language (64-bit)
- **CPU**: AMD Ryzen 5 7235HS (4 cores, 8 logical threads)
- **System RAM**: 24 GB DDR5
- **GPU**: NVIDIA GeForce RTX 3050 Laptop GPU (6 GB VRAM)
- **Local Runtimes**: Ollama v0.32.14, Node.js v25.8.0, npm 11.16.0, ADB v1.0.41

### Hardware & CUDA Runtime Dependency Stack
```
Hardware (RTX 3050 6GB)
   ↓
NVIDIA Driver (v591.66)
   ↓
CUDA Driver / Runtime Compatibility (v13.1 reported by OS driver)
   ↓
Framework & Package Builds (PyTorch / C++ Extensions)
   ↓
Ollama & Inference Execution Engine
```

### Python Version Strategy
- **System Installed Python**: Python 3.14.3
- **JARVIS Project Python Version**: `FINAL PYTHON VERSION: TO BE LOCKED AFTER DEPENDENCY COMPATIBILITY VERIFICATION`
- **Dependency Status**: `PENDING VERIFICATION` during Phase 0 virtualenv setup.
- *Policy*: The project Python version will be locked to a single, exact minor version (e.g., Python 3.11 or 3.12) based on binary wheel compatibility across PyTorch, `faster-whisper`, `sqlite-vec`, and FastAPI dependencies before Phase 0 coding starts.

---

## 3. High-Level Architecture Overview

```
+-----------------------------------------------------------------------+
|                            USER INTERFACES                            |
|       Text CLI / Web HUD / Voice Microphone / Optional 3D UI          |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                           INPUT PROCESSING                            |
|     VAD (Silero) -> STT (Faster-Whisper) -> Language ID (Confidence)   |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                          JARVIS ORCHESTRATOR                          |
|     State Management, Context Assembly, Agent Routing, Feature Flags  |
+-------------------+---------------+-------------------+---------------+
                    |               |                   |
                    v               v                   v
         +----------+----+  +-------+--------+  +-------+--------+
         | Context &     |  | Multi-Agent    |  | Model Router   |
         | Memory Engine |  | Engine (Core & |  | (Ollama Local /|
         | (SQLite+Vector|  |  Optional)     |  |  Cloud AI)     |
         +---------------+  +-------+--------+  +-------+--------+
                                                        |
                                                        v
                                             +--------------------+
                                             | Model Adapter      |
                                             | (Normalized        |
                                             |  ModelResponse)    |
                                             +----------+---------+
                                                        |
                                                        v
+-----------------------------------------------------------------------+
|                    DETERMINISTIC PERMISSION ENGINE                    |
|             Permission Levels: SAFE / CONFIRMATION / HIGH RISK        |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                          TOOL EXECUTION LAYER                         |
|      PC System Tools / Android ADB Bridge / Web / File System         |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                          RESULT VERIFICATION                          |
|           Empirical Post-Execution State & Artifact Checks            |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                      RESPONSE / UI TELEMETRY / TTS                    |
+-----------------------------------------------------------------------+
```

---

## 4. Normalized AI Provider & Model Adapter Protocol

To prevent the Orchestrator from depending directly on vendor-specific LLM tool-calling formats, the AI Provider layer mandates a **Normalized Model Adapter**.

```
  Ollama API           Cloud OpenAI          Cloud Gemini          Future Provider
      │                     │                     │                     │
      └─────────────────────┴──────────┬──────────┴─────────────────────┘
                                       │
                                       v
                              +------------------+
                              |  Model Adapter   |
                              +--------+---------+
                                       │
                                       v
                             [ ModelResponse ]
                                       │
                                       v
                              [ ToolCall[] ]
```

### Conceptual Protocol Contracts

#### `ModelRequest`
- `prompt`: Input user query or system instruction.
- `context`: List of conversation turns and RAG context blocks.
- `tools`: List of registered tool JSON Schemas.
- `model_parameters`: Temperature, max tokens, stop sequences.

#### `ModelResponse`
- `text`: Natural language text generated by the model.
- `tool_calls`: List of normalized `ToolCall` objects.
- `finish_reason`: Termination status (`stop`, `tool_calls`, `length`).
- `metadata`: Provider name, model ID, latency.
- `usage`: Token usage statistics (prompt tokens, completion tokens).

#### `ToolCall`
- `tool_id`: Unique string identifier of target tool (e.g., `get_time`).
- `arguments`: Validated parameter dictionary.
- `call_id`: Unique identifier for tracking this execution instance.

#### `ToolResult`
- `call_id`: Matching execution identifier.
- `success`: Boolean indicator of execution result.
- `output`: Result payload or returned data structure.
- `error`: Error message string if execution failed.
- `verification_status`: Status from `ResultVerifier` (`VERIFIED`, `UNVERIFIED`, `FAILED`).

---

## 5. Cloud Fallback & Privacy Control Architecture

The architecture supports local Ollama inference with optional cloud provider fallback, protected by strict privacy controls:

```
                      [ User Request ]
                             |
                             v
                  [ Preferred Local Model ]
                             |
                   +---------+---------+
                   | Execution Success?|
                   +----+---------+----+
                     Yes|         |No
                        |         v
                        |   [ Local Failure Detected ]
                        |         |
                        |         v
                        |   +----------------------------+
                        |   | ALLOW_CLOUD_FALLBACK == True|
                        |   +-------------+--------------+
                        |              No / \ Yes
                        |                /   \
                        |    Fail Safely /    v
                        |   Alert User  +-----------------------------------+
                        |               | Request Allowed to Leave Device?  |
                        |               | (Privacy Classification Check)    |
                        |               +-----------------+-----------------+
                        |                              No / \ Yes
                        |                                /   \
                        |                   Fail Safely /     v
                        v                              [ Dispatch to Cloud ]
              [ Verified Output ]
```

### Privacy Settings & Policy Rules
- `ALLOW_CLOUD_FALLBACK`: Master boolean configuration (`false` by default).
- `PRIVACY_CLASSIFICATION`: Requests tagged as `RESTRICTED` (local device stats, user memories, credentials, private files) are **never** transmitted to cloud providers regardless of local failure.
- *Policy*: The system must **never silently send sensitive user information to a cloud API** merely because the local Ollama model failed or timed out.

---

## 6. Multi-Agent Architecture

JARVIS implements a multi-agent framework categorized into deployment tiers:

### Core Initial Agents (Phase 0 – Phase 5)
- **Conversation Agent**: Handles standard user dialogue, Q&A, and general reasoning.
- **Planning Agent**: Decomposes complex multi-step user tasks into structured tool DAGs.
- **Computer Agent**: Controls authorized local application launching, process management, screenshot capture, and system telemetry.
- **Memory Agent**: Manages retrieval, creation, modification, and deletion of persistent user facts.
- **Research Agent**: Performs multi-query web searches, extracts webpage contents, and synthesizes cited research summaries.
- **Voice Agent**: Coordinates audio streaming, STT transcription, and TTS speech generation.

### Secondary Agents (Phase 6 – Phase 8)
- **Mobile Agent**: Interacts with Android Companion app and local ADB bridge for phone status, SMS, and notifications.
- **Vision Agent**: Handles OCR, screenshot analysis, and image understanding.
- **Device Agent**: Manages authorized local network PC/IoT node handshakes and discovery.

### Experimental / Optional Agents (Phase 10)
- **Call Agent**: Experimental telephone assistant for call screening, voice prompts, and message transcription.
- **Advanced IoT Agent**: Controls authorized smart home endpoints.

---

## 7. Deterministic Permission Engine & Result Verification

### Deterministic Execution Boundary
The LLM can only **request** a tool call. The deterministic execution layer decides authorization and performs empirical result verification.

```
AI REQUEST -> STRUCTURED ACTION -> VALIDATION -> PERMISSION CHECK -> EXECUTION -> RESULT VERIFICATION -> RESPONSE
```

```
                        [ Tool Call Request ]
                                  |
                                  v
                      +-----------------------+
                      | Check Permission Level |
                      +-----------+-----------+
                                  |
         +------------------------+------------------------+
         |                        |                        |
         v                        v                        v
      [ SAFE ]         [ CONFIRMATION REQUIRED ]      [ HIGH RISK ]
         |                        |                        |
  Auto-Approved          Requires User Action      Requires Interactive
         |               (Prompt / Web UI Click)    Explicit Challenge
         v                        |                        |
  Execute Tool             +------+------+                 |
                           | Approved?   |                 |
                           +------+------+                 |
                             Yes / \ No                    |
                              /     \                      v
                     Execute Tool  Reject Call      +--------------+
                                                    | Approved?    |
                                                    +------+-------+
                                                      Yes / \ No
                                                       /     \
                                              Execute Tool  Reject Call
```

### Result Verification Strategy
JARVIS **never fabricates success**. After tool execution:
1. `ResultVerifier` queries the underlying OS, file system, or process table.
2. Example: For `app_launch("chrome.exe")`, `ResultVerifier` verifies that `chrome.exe` PID exists in the active OS process list via `psutil`.
3. If verification fails, JARVIS reports the failure truthfully rather than claiming the action succeeded.

---

## 8. Memory Architecture

Three tiered memory structures:
1. **Short-Term Memory**: In-memory ring buffer holding recent conversation turns with strict token limits.
2. **Long-Term Explicit Memory**: SQLite table (`memories`) storing user-specified facts, key preferences, and system settings. Supports `save`, `retrieve`, `update`, `delete`, and `forget`.
3. **Semantic / Vector Memory**: SQLite vector storage using local embeddings (`all-MiniLM-L6-v2`) for RAG retrieval during complex queries.

---

## 9. Voice Pipeline Architecture

Voice operation is an decoupled interface layer enabled via `ENABLE_VOICE`:

```
Microphone Stream -> Silero VAD -> Wake Word ("Hey JARVIS") / Clap Detector (Double-Clap)
   -> Language Detection -> STT (Faster-Whisper) -> Text -> Orchestrator -> LLM Response
   -> TTS Engine (Edge-TTS / Pyttsx3) -> Audio Output & 3D Visualizer State
```

If speech input fails, microphone is missing, or `ENABLE_VOICE=false`, the core system falls back headlessly to text CLI / Web API.

---

## 10. Multilingual Architecture & Code-Switching

- Supported Languages: **English**, **Telugu**, **Hindi**, **Tamil**, and **Code-Switched (Mixed)** speech.
- **Confidence-Aware Detection**: Language detection returns language code + confidence score (`0.0 - 1.0`).
- **Low Confidence Strategy**: If confidence < 0.65, context history is inspected or user is asked for clarification rather than making assumptions.
- **Caller Rule (For Call Agent)**: If a caller uses mixed English + regional language, the regional language takes priority in response generation.

---

## 11. Computer Control Layer

Narrowly scoped, deterministic system tools:
- `app_launch`: Launches allowed executable paths.
- `app_close`: Terminates specified application process.
- `system_telemetry`: Fetches CPU, RAM, GPU, VRAM, and disk metrics via system APIs.
- `take_screenshot`: Captures active monitor frame to local temp folder.
- `safe_file_read` / `safe_file_write`: Reads/writes files strictly within designated workspace allowlists.

*Security Constraint*: Direct unrestricted shell / terminal execution is prohibited.

---

## 12. Android Architecture & Security Specification

Android companion integration is scheduled for **Phase 6**.

### Security & Pairing Requirements
- **Secure Device Pairing**: PIN / QR-code mutual handshake.
- **Device Identity & Token Auth**: Cryptographic device token assigned upon pairing; token rotation enabled.
- **Capability Authorization**: Granular permissions per action (Notifications, SMS, Call State, Battery).
- **Transport Security**: TLS encrypted local WebSockets / mDNS communication.
- **Replay Protection**: Timestamped request signing to prevent replay attacks.
- **Revocation**: One-click device revocation from PC Privacy Panel.
- **ADB Fallback**: Controlled local USB/Wi-Fi ADB bridge for tethered device management.
- *Principle of Least Privilege*: The Android app is a gated endpoint, never an unrestricted remote control daemon.

---

## 13. Vision Architecture

Integrates local vision models (`llama3.2-vision` / `qwen2-vl` via Ollama) or cloud vision APIs during Phase 7 for OCR, screenshot analysis, and camera frame understanding.

---

## 14. Web Research Architecture

Scheduled for **Phase 8**. Future requirements:
- Search provider abstraction (Tavily / SearXNG / DuckDuckGo).
- Web page text extraction & cleaning.
- Multi-source citation & attribution tracking.
- Conflicting source detection and summarization.
- Failed / blocked page handling with search retries.
- Strict anti-hallucination citation checks.

---

## 15. Call Agent Limitations & Strategy

- Marked as **EXPERIMENTAL / OPTIONAL** (`ENABLE_CALL_AGENT=false` by default, Phase 10).
- **Platform Limitations**: Android OS strictly regulates call audio routing and automated dialer access on unrooted devices.
- Operates strictly as an isolated optional module that does not block core JARVIS execution.

---

## 16. UPI & Payment Security Architecture

- Flow: User requests payment -> Orchestrator parses recipient & amount -> Confirmation requested -> Backend fires Android UPI deep-link intent (`upi://pay?...`).
- **CRITICAL SECURITY REQUIREMENT**:
  - JARVIS NEVER requests, reads, stores, or inputs the user's UPI PIN.
  - User personally inputs PIN inside the official third-party UPI app (GPay / PhonePe / Paytm).

---

## 17. 3D Sci-Fi UI Architecture

- Built using **React 18**, **TypeScript**, **Three.js**, **React Three Fiber (`@react-three/fiber`)**, and **Drei (`@react-three/drei`)**.
- Visual Orb Core reflects backend states (`IDLE`, `LISTENING`, `THINKING`, `EXECUTING`, `SPEAKING`, `ERROR`).
- **Decoupled Architecture**: 3D UI is purely a client-side visual layer (`ENABLE_3D_UI=true`). The backend functions 100% headlessly if UI is disconnected.

---

## 18. Database Architecture & Future Requirements

### Phase 0 Core Database
- SQLite database (`jarvis.db`) in WAL mode.
- Core schema: basic settings, simple key-value memories, audit log table.

### Future Database Requirements (Phase 2+)
- Explicit relational schemas (`users`, `devices`, `memories`, `conversations`, `messages`, `tasks`, `tool_calls`, `call_records`, `audit_logs`).
- Foreign key constraints, performance indexes, and schema migration framework (`alembic`).
- Data retention policies, privacy deletion handlers ("forget user"), and automated backups.
- Vector extension (`sqlite-vec`) for 384-dimensional semantic embeddings.

---

## 19. Project Directory Structure

```
jarvis/
├── backend/
│   ├── app/
│   │   ├── main.py                # FastAPI entry point
│   │   ├── api/                   # REST & WebSocket routes
│   │   ├── core/                  # Orchestrator & lifecycle
│   │   ├── agents/                # Multi-agent implementations
│   │   ├── ai/                    # AI Provider abstractions & Model Adapter
│   │   ├── tools/                 # Tool Registry & tool definitions
│   │   ├── security/              # Permission Engine & safety rules
│   │   ├── memory/                # SQLite DB & Vector RAG store
│   │   ├── voice/                 # STT, TTS, VAD, wake-word pipeline
│   │   ├── devices/               # Device Registry & ADB bridge
│   │   ├── calls/                 # Experimental Call Agent
│   │   ├── vision/                # Image & OCR handlers
│   │   ├── config/                # Environment & Feature Flags
│   │   ├── observability/         # Structured logging & tracing
│   │   └── models/                # Pydantic schemas & DB models
│   └── tests/                     # Unit, integration, security tests
├── frontend/
│   ├── src/
│   │   ├── components/            # React UI panels & HUD
│   │   ├── three/                 # Three.js Sci-Fi Orb & shaders
│   │   ├── stores/                # Zustand state stores
   │   ├── services/              # API & WebSocket client
   │   └── types/                 # TypeScript interfaces
├── android/                        # Companion Kotlin app
├── data/                           # SQLite database & local models
├── docs/                           # Documentation
├── .env.example
├── .gitignore
├── README.md
└── implementation.md              # This specification document
```

---

## 20. Configuration, Security & Observability

### Configuration Management
- Loaded via `pydantic-settings` from `.env`. Secrets are never committed.

### Security Boundaries
- Least privilege execution, zero unrestricted shell access, immutable audit logging, zero credential/PIN storage.

### Observability & Tracing
- Structured JSON logging with `request_id` tracing across LLM latency, STT/TTS timing, tool execution, and permission decisions.

---

## 21. Failure Recovery & Graceful Degradation

| Failure Scenario | Fallback Action |
|---|---|
| Ollama / Local Model Down | Route query to Cloud AI provider (if `ALLOW_CLOUD_FALLBACK=true` & non-restricted) or alert user |
| Cloud AI API Outage | Fallback to local Ollama inference |
| Speech-to-Text Failure | Prompt user for text input fallback |
| TTS Engine Failure | Output text response silently without crashing audio loop |
| Tool Execution Error | Capture error trace, report failure truthfully, execute rollback if available |
| Android Device Offline | Disable mobile tools; continue executing PC & web tools |
| 3D UI Rendering Error | Fallback to standard 2D HTML/CLI Interface |

---

## 22. Feature Flags

- `ENABLE_VOICE`: `false` (Phase 0), `true` (Phase 3+)
- `ENABLE_WAKE_WORD`: `false` (Phase 0), `true` (Phase 3+)
- `ENABLE_MULTILINGUAL`: `false` (Phase 0), `true` (Phase 4+)
- `ENABLE_COMPUTER_CONTROL`: `true` (Phase 0+)
- `ENABLE_ANDROID`: `false` (Phase 0), `true` (Phase 6+)
- `ENABLE_VISION`: `false` (Phase 0), `true` (Phase 7+)
- `ENABLE_WEB_RESEARCH`: `false` (Phase 0), `true` (Phase 8+)
- `ENABLE_3D_UI`: `false` (Phase 0), `true` (Phase 9+)
- `ENABLE_CALL_AGENT`: `false` (Experimental Phase 10)
- `ALLOW_CLOUD_FALLBACK`: `false` (Phase 0+)

---

## 23. Local Model Benchmarking Strategy

Before hardcoding default inference models, a systematic benchmarking suite will run on host hardware (RTX 3050 6GB VRAM + 24GB RAM):
- **Metrics**: Startup time, time-to-first-token (TTFT), average latency, tokens/sec, VRAM utilization, RAM footprint, context window handling, structured JSON tool precision, multilingual performance (EN, TE, HI, TA, code-switching), and long-session stability.
- **Candidate Models**: `qwen2.5:3b-instruct`, `llama3.2:3b`, `qwen2.5:7b-instruct-q4_K_M`, `mistral:7b-instruct`.
- Model with best tool calling precision and lowest TTFT under 6GB VRAM constraint will be set as default.

---

## 24. Phased Implementation Roadmap & Scope Control

*Strict Scope Rule*: Features in later phases are **not** permitted to silently become Phase 0 dependencies.

```
PHASE 0: JARVIS CORE FOUNDATION
  └─ Minimum foundation required for core functioning (CLI, Orchestrator, Ollama Provider, Model Adapter, Minimal Tools: get_time, calculator, system_status, Basic SAFE permission check, Basic verification).

PHASE 1: COMPLETE TOOL & PERMISSION ENGINE
  └─ Expand foundation into full system (Complete Tool Registry, Full Schemas, Preconditions, Timeouts, CONFIRMATION_REQUIRED, HIGH_RISK interactive approval, Audit logging, Rollback handlers, Bypass tests).

PHASE 2: MEMORY ENGINE & VECTOR RAG
  └─ Short-term, Long-term explicit facts, SQLite-vec semantic search.

PHASE 3: VOICE ENGINE
  └─ Silero VAD, Wake word ("Hey JARVIS"), Clap detection, STT (Faster-Whisper), TTS (Edge-TTS).

PHASE 4: MULTILINGUAL & CODE-SWITCHING ENGINE
  └─ Confidence-aware language identification (EN, TE, HI, TA, Mixed).

PHASE 5: COMPUTER CONTROL LAYER
  └─ App launch/close, process state, screenshots, safe workspace files.

PHASE 6: ANDROID COMPANION & ADB BRIDGE
  └─ Authenticated Kotlin app endpoints, token rotation, local ADB tethering.

PHASE 7: VISION ENGINE
  └─ OCR, screenshot understanding, camera snapshot analysis.

PHASE 8: WEB RESEARCH ENGINE
  └─ Search provider abstraction, webpage extraction, multi-source citations.

PHASE 9: 3D SCI-FI UI ENGINE
  └─ Three.js / React Three Fiber Sci-Fi Orb visualizer & state synchronization.

PHASE 10: ADVANCED / EXPERIMENTAL FEATURES
  └─ Experimental Call Agent, IoT device ecosystem.

PHASE 11: END-TO-END TESTING & HARDENING
  └─ Complete system security audits, failure recovery stress tests.
```

---

## 25. Detailed Clarification: Phase 0 vs Phase 1

> [!NOTE]
> **Phase 0 contains the minimal tool/permission foundation required for the core to function. Phase 1 expands that foundation into the complete permission and tool-execution system.**

### Phase 0 Scope (Immediate Target)
- Base tool protocol / interface (`BaseTool`).
- Minimal `ToolRegistry` registering ONLY basic `SAFE` tools: `get_time`, `calculator`, `system_status`.
- Basic `SAFE` permission check logic (auto-approves `SAFE` category).
- Basic `ModelAdapter` output parsing.
- Basic `ResultVerifier` checking output validity.
- CLI text interface.

### Phase 1 Scope (Follow-up Milestone)
- Full `ToolRegistry` expansion with rich metadata, JSON Schemas, preconditions, timeouts, validation, and rollback handlers.
- Multi-tier `PermissionEngine` (`SAFE`, `CONFIRMATION_REQUIRED`, `HIGH_RISK`).
- Interactive user confirmation flows for sensitive actions.
- Audit log database persistence for all permission decisions.
- Comprehensive security test suite (permission bypass tests, malformed tool-call injection tests).

---

## 26. Phase 0 Detailed Acceptance Criteria

Phase 0 is complete **only** when all of the following measurable criteria are met:

### Functional Criteria
- JARVIS backend starts up clean without errors via CLI (`python -m app.main`).
- User can input text commands and receive valid responses.
- Local Ollama model (`qwen2.5:3b` / `llama3.2:3b`) connects and generates output via `OllamaProvider`.
- Orchestrator parses user intent, creates normalized `ModelResponse`, and generates structured `ToolCall` objects.
- Tool schemas are validated before execution.
- Phase 0 `SAFE` tools (`get_time`, `calculator`, `system_status`) execute correctly.
- `ResultVerifier` verifies output state (e.g. valid status dict returned) before final response.
- JARVIS never claims an action succeeded if execution failed.

### Security Criteria
- LLM cannot execute raw arbitrary shell/terminal commands.
- Unknown tool requests are rejected cleanly.
- Invalid tool parameters fail schema validation safely.
- Tool execution strictly adheres to permission engine checks.
- Secrets, API keys, and sensitive environment variables are masked in log outputs.
- Malformed model outputs (invalid JSON tool calls) are caught and reported safely.

### Failure Handling Criteria
- If Ollama daemon is offline/unreachable, orchestrator catches the error and reports a clean offline message to user.
- Invalid LLM outputs trigger graceful fallbacks without crashing the backend process.
- Tool execution timeouts or errors return structured error results to orchestrator.

### Testing Criteria
- Unit test suite for Orchestrator, Model Adapter, Tool Registry, and Permission Engine passes.
- Integration tests for end-to-end text query $\rightarrow$ tool execution $\rightarrow$ verified response pipeline pass.
- Permission-bypass and malformed-input security tests pass.
- Manual Phase 0 CLI smoke test succeeds (`"What's the time?"`, `"Calculate 25 * 18"`, `"Show my system status"`).

---

## 27. Stage-2 Extensibility & Verification Checklist

- Abstract contracts (`AIProvider`, `Agent`, `Tool`, `MemoryStore`, `PermissionEngine`, `ResultVerifier`) are decoupled and ready for Stage 2 custom ML implementations.
- Zero hardcoded vendor lock-in in core orchestration loops.
- All capabilities controlled by explicit environment feature flags.

---

### Specification Status
- Architecture fully finalized and validated.
- Ready for explicit Phase 0 implementation directive.
