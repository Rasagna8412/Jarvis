# JARVIS: Single Authoritative Master Specification & Technical Implementation Architecture

---

## 1. Executive Summary

This document serves as the **Single Authoritative Master Specification** for **JARVIS**, a multi-device, multilingual, voice-controlled, 3D personal AI assistant. 

JARVIS is designed around a mandatory core principle: **Strict separation of Intelligence (LLMs/Agents) from Execution (Deterministic Tool & Permission Engine)**.

- **LLMs request actions**; they never execute them directly.
- **Deterministic Python code validates, authorizes, executes, and empirically verifies** every action before returning a response to the user.
- System capabilities are delivered progressively across 12 distinct phases (Phase 0 through Phase 11).
- The system is built for local-first execution (Ollama) with optional, privacy-gated cloud model fallbacks.

---

## 2. Project Vision

JARVIS is an original implementation of a personal AI assistant inspired by advanced sci-fi AI concepts. It is engineered to perform real-world operating system interaction, voice communication, device automation, and research assistance without simulating results or fabricating capabilities.

### Target Vision Capabilities (Full Scope)
- Natural language text and voice interaction
- Hands-free activation: "Hey JARVIS" wake phrase and double-clap audio detection
- Multilingual understanding: English, Telugu, Hindi, Tamil, and mixed code-switching
- Local AI model execution (Ollama) with optional cloud AI model fallback
- Multi-agent architecture (Planning, Conversation, Computer, Memory, Research, Voice, Mobile, Vision, Device, Call Agent, IoT)
- Safe host computer automation (application control, screenshots, workspace management)
- Android companion integration and tethered ADB bridge
- Multi-tier persistent memory (Short-term conversation buffer, Long-term facts, Semantic Vector RAG)
- Multi-step web research with source extraction and citation tracking
- Real-time 3D sci-fi visual interface (Three.js / React Three Fiber)
- Safe UPI payment flow launching without PIN access
- Experimental call assistant and call screening capabilities
- Robust security boundaries, structured observability, and graceful failure recovery
- Clean modular design supporting Stage-2 custom model reimplementation

---

## 3. Stage 1 / Stage 2 Strategy

JARVIS is structured across two distinct evolutionary stages:

### STAGE 1 — Current Engineering Implementation (Production Reference Baseline)
- **Objective**: Build a fully functional, modular, multi-device personal assistant.
- **Implementation**: Utilizes production open-source libraries, frameworks, and inference engines (Python 3.11+, FastAPI, WebSockets, Ollama, React, Three.js, Android Kotlin SDK).
- **Role of AI Assistants**: Code generation, debugging, test creation, and component integration.
- **Architectural Deliverable**: A working baseline where every major component connects through clean, abstract Python protocols (`AIProvider`, `Agent`, `Orchestrator`, `Tool`, `PermissionEngine`, `MemoryStore`, `ResultVerifier`).

### STAGE 2 — Future Self-Built / Research-Oriented Version
- **Objective**: Deepen personal understanding of AI/ML concepts by rebuilding core AI subsystems post-coursework.
- **Extensibility Guarantee**: Stage 1 abstractions permit individual components (e.g., custom local orchestrator, custom fine-tuned model, custom vector retriever, custom planner) to replace Stage 1 components without rewriting surrounding application code.
- *Status Note*: Stage 1 is a working engineering reference baseline, not novel AI research.

---

## 4. Scope & Phase Boundaries

To prevent project failure from over-engineering, capabilities are strictly partitioned across 12 development phases.

### Strict Scope Control Policy
> [!IMPORTANT]
> **No Hidden Scope Expansion Rule**: A phase may depend ONLY on capabilities explicitly marked as available from prior completed phases. Future-phase features must not silently become dependencies of an earlier phase.

---

## 5. Hardware & Environment Stack

### Verified Host Environment Facts
- **Operating System**: Windows 11 Home Single Language (64-bit) `[VERIFIED FACT]`
- **CPU**: AMD Ryzen 5 7235HS (4 cores, 8 logical threads) `[VERIFIED FACT]`
- **System Memory**: 24 GB DDR5 RAM `[VERIFIED FACT]`
- **GPU**: NVIDIA GeForce RTX 3050 Laptop GPU (6 GB VRAM) `[VERIFIED FACT]`
- **Installed Runtimes**: Ollama v0.32.14, Node.js v25.8.0, npm 11.16.0, ADB v1.0.41 `[VERIFIED FACT]`

### Hardware & CUDA Runtime Stack Relationship
```
Hardware (RTX 3050 6GB VRAM)
   ↓
NVIDIA Display Driver (v591.66)
   ↓
CUDA Driver / Runtime Compatibility (CUDA 13.1 driver API reported by system)
   ↓
Framework & C++ Extension Builds (PyTorch / CUDA runtime libraries)
   ↓
Ollama & Inference Execution Engine
```

### Python Version Strategy
- **System Python**: Python 3.14.3 `[VERIFIED FACT]`
- **JARVIS Project Python Version**: `FINAL PYTHON VERSION: TO BE LOCKED AFTER DEPENDENCY COMPATIBILITY VERIFICATION` `[DESIGN DECISION / PENDING]`
- **Dependency Compatibility Status**: `PENDING VERIFICATION` `[UNVERIFIED]`
- *Rule*: During Phase 0 virtual environment initialization, dependency wheel compatibility across PyTorch, `faster-whisper`, `sqlite-vec`, and `fastapi` will be tested. The project Python version will be locked to a single, exact minor release (e.g., Python 3.11.x or 3.12.x) and recorded in `.python-version`.

---

## 6. Architecture Principles

1. **Separation of Intelligence and Execution**: LLMs reason and generate tool requests; deterministic code authorizes and executes them.
2. **Zero Direct Shell Access**: LLMs are never granted direct access to raw shell or terminal execution strings.
3. **Empirical Result Verification**: JARVIS never claims an action succeeded without empirical evidence from OS, file system, or process inspect calls.
4. **Local-First Privacy**: Sensitive data remains local by default. Cloud access requires explicit user enablement and privacy classification approval.
5. **Headless Core**: Core functionality operates headlessly via CLI or REST API. UI and Voice are decoupled client layers.

---

## 7. High-Level Master Architecture

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

## 8. JARVIS Orchestrator

The **JARVIS Orchestrator** is the central state machine and coordinator.

### Responsibilities
- Request lifecycle management
- Session state maintenance
- Context assembly (system prompts, conversation history, RAG facts)
- Intent classification and Agent routing
- Task decomposition and multi-step planning
- Model routing and Model Adapter invocation
- Structured tool call dispatching to Permission Engine
- Result verification tracking
- Exception handling and fallback execution
- Observability and event emission (WebSocket UI updates)

*Design Constraint*: Business logic remains inside the Orchestrator and core managers, not scattered across UI code or LLM prompt templates.

---

## 9. AI Provider Architecture

The AI layer relies on an abstract base interface (`AIProvider`):

```python
class AIProvider(ABC):
    @abstractmethod
    async def generate_response(self, request: ModelRequest) -> ModelResponse:
        pass

    @abstractmethod
    async def generate_stream(self, request: ModelRequest) -> AsyncIterator[str]:
        pass
```

### Implementations
- `OllamaProvider`: Communicates with local Ollama daemon (`http://localhost:11434`).
- `CloudProvider`: Communicates with external API endpoints (OpenAI, Gemini, Anthropic) when enabled.

---

## 10. Normalized Model Protocol

To prevent Orchestrator lock-in to proprietary LLM output formats, all model responses are converted by a **Model Adapter** into normalized contracts:

```
  Ollama JSON / Cloud API Response
                 │
                 v
           Model Adapter
                 │
                 v
         [ ModelResponse ]
                 │
                 v
          [ ToolCall[] ]
```

### Conceptual Data Contracts

```python
@dataclass
class ModelRequest:
    prompt: str
    context_turns: List[Dict[str, Any]]
    system_instruction: Optional[str]
    tools_schema: Optional[List[Dict[str, Any]]]
    temperature: float = 0.2
    max_tokens: int = 1024

@dataclass
class ToolCall:
    call_id: str
    tool_id: str
    arguments: Dict[str, Any]

@dataclass
class ModelResponse:
    text_content: Optional[str]
    tool_calls: List[ToolCall]
    finish_reason: str  # "stop", "tool_calls", "length", "error"
    provider_name: str
    model_name: str
    latency_ms: float
    usage_tokens: Dict[str, int]

@dataclass
class ToolResult:
    call_id: str
    tool_id: str
    success: bool
    output_data: Dict[str, Any]
    error_message: Optional[str]
    verification_status: str  # "VERIFIED", "UNVERIFIED", "FAILED"
```

---

## 11. Multi-Agent Architecture

JARVIS defines a multi-agent framework categorized into deployment tiers:

### Core Initial Agents (Phase 0 – Phase 5)
- **Conversation Agent**: Dialogue, general reasoning, Q&A.
- **Planning Agent**: Multi-step task decomposition into tool DAGs.
- **Computer Agent**: Host application launching, process management, screenshots, telemetry.
- **Memory Agent**: Retrieval, creation, editing, and deletion of persistent facts.
- **Research Agent**: Multi-query web search, page extraction, cited research synthesis.
- **Voice Agent**: Audio streaming, STT transcription, TTS synthesis coordination.

### Secondary Agents (Phase 6 – Phase 8)
- **Mobile Agent**: Android app and local ADB bridge communication.
- **Vision Agent**: OCR, screenshot analysis, image grounding.
- **Device Agent**: Local network PC/IoT discovery and control.

### Experimental / Optional Agents (Phase 10)
- **Call Agent**: Experimental call screening, voice prompt generation, transcript logging (`EXPERIMENTAL FEATURE`).
- **Advanced IoT Agent**: Smart home endpoint automation (`EXPERIMENTAL FEATURE`).

---

## 12. Tool Registry Architecture

Every tool registered in `ToolRegistry` implements the `BaseTool` contract:

- `tool_id`: Unique string key (e.g., `system_get_status`).
- `name`: Human-readable name.
- `description`: Detailed function purpose for LLM tool selection.
- `input_schema`: Pydantic model / JSON Schema specifying exact parameters.
- `output_schema`: Structured result data model.
- `permission_level`: `SAFE`, `CONFIRMATION_REQUIRED`, or `HIGH_RISK`.
- `preconditions`: Prerequisite checks (e.g., app installed, network connected).
- `timeout_seconds`: Execution timeout limit.
- `rollback_handler`: Optional recovery function if partial failure occurs.

---

## 13. Deterministic Permission Engine

The **Permission Engine** is strictly deterministic Python code. The LLM cannot grant itself permissions, modify permission levels, or bypass rules.

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

### Permission Levels
1. **SAFE**: Read-only, deterministic, non-destructive tools (e.g., `get_time`, `calculator`, `system_status`). Auto-approved.
2. **CONFIRMATION_REQUIRED**: Actions modifying system state or transmitting external communications (e.g., `launch_app`, `write_file`, `send_sms`, `open_upi`). Requires explicit user confirmation.
3. **HIGH_RISK**: Destructive or security-sensitive actions (e.g., `delete_file`, `system_shutdown`, financial transactions). Requires double-challenge interactive confirmation.

---

## 14. Result Verification Component

JARVIS **never fabricates success**. Result verification is a required pipeline step:

```
AI REQUEST -> STRUCTURED ACTION -> VALIDATION -> PERMISSION CHECK -> EXECUTION -> RESULT VERIFICATION -> RESPONSE
```

### Execution Verification Pattern
1. Tool executes (e.g., `app_launch("chrome.exe")`).
2. `ResultVerifier` runs empirical check (scans OS process table via `psutil` to verify `chrome.exe` PID is running).
3. If check passes $\rightarrow$ `verification_status = "VERIFIED"`.
4. If check fails $\rightarrow$ `verification_status = "FAILED"`, error returned to LLM context to inform user truthfully.

---

## 15. Memory Architecture

Three memory tiers are maintained:

1. **Short-Term Memory**: In-memory ring buffer holding recent context turns within token budgets.
2. **Long-Term Explicit Memory**: SQLite table (`memories`) storing explicit user key-value facts, preferences, and configurations (`save`, `retrieve`, `update`, `delete`, `forget`).
3. **Semantic Vector Memory**: SQLite vector extension (`sqlite-vec`) storing 384-dimensional text embeddings for semantic RAG retrieval.

*Privacy Control*: Conversations are not auto-persisted permanently unless explicitly commanded by the user.

---

## 16. Voice Pipeline Architecture

Voice processing is a client/interface layer (`ENABLE_VOICE=true`):

```
Microphone Stream -> Silero VAD -> Wake Word ("Hey JARVIS") / Clap Detector (Double-Clap)
   -> Language Detection -> STT (Faster-Whisper) -> Text -> Orchestrator -> LLM Response
   -> TTS Engine (Edge-TTS / Pyttsx3) -> Speaker Output & 3D Orb Animation
```

If speech input fails, microphone is absent, or `ENABLE_VOICE=false`, JARVIS operates headlessly via CLI / REST API.

---

## 17. Multilingual Architecture & Code-Switching

- Supported Languages: **English**, **Telugu**, **Hindi**, **Tamil**, and **Code-Switched (Mixed)** speech.
- **Confidence-Aware Detection**: Language detection returns language code + confidence score (`0.0 - 1.0`).
- **Low Confidence Strategy**: If confidence < 0.65, context history is checked or user is asked for clarification rather than assuming intent.
- **Caller Rule (For Call Agent)**: If a caller uses mixed English + regional language, the regional language takes priority in response generation.

---

## 18. Computer Control Layer

Narrowly scoped, deterministic system tools:
- `app_launch`: Launches allowed executable paths.
- `app_close`: Terminates specified process.
- `system_telemetry`: Fetches CPU, RAM, GPU, VRAM, disk metrics.
- `take_screenshot`: Captures active screen frame to temp folder.
- `safe_file_read` / `safe_file_write`: Operates strictly within designated workspace allowlists.

*Security Rule*: Unrestricted raw shell execution is prohibited.

---

## 19. Android Architecture & Security Specification

Scheduled for **Phase 6**.

```
JARVIS PC Backend <--- (TLS Encrypted WS / mDNS) ---> Android Companion Kotlin App
JARVIS PC Backend <--- (Local Tethered USB/Wi-Fi) ---> ADB Bridge Fallback
```

### Security Requirements
- **Mutual Device Pairing**: PIN / QR-code handshake.
- **Token Auth & Rotation**: Cryptographic session tokens with rotation.
- **Granular Authorization**: Capability permissions per feature (SMS, Calls, Battery, Notifications).
- **Transport Encryption**: TLS for all network sockets.
- **Replay Protection**: Timestamped request signatures.
- **Revocation**: One-click device removal from PC Privacy Panel.
- *Least Privilege Principle*: The companion app is a gated endpoint, never an unrestricted remote control service.

---

## 20. Vision Architecture

Scheduled for **Phase 7**. Responsibilities:
- Screenshot and image input processing.
- Image understanding via multimodal local models (`llama3.2-vision` / `qwen2-vl`) or cloud APIs.
- Desktop UI visual grounding and OCR text extraction.

---

## 21. Web Research Architecture

Scheduled for **Phase 8**. Future requirements:
- Search provider abstraction (Tavily / SearXNG / DuckDuckGo).
- Web page content extraction and cleaning.
- Multi-source citation and URL attribution tracking.
- Conflicting source detection.
- Failed / blocked page retries and search provider fallbacks.
- Strict anti-hallucination source validation.

---

## 22. UPI & Payment Security Architecture

- Flow: User requests payment $\rightarrow$ Orchestrator parses details $\rightarrow$ Confirmation requested $\rightarrow$ Backend fires Android UPI deep-link intent (`upi://pay?...`).
- **CRITICAL SECURITY REQUIREMENT**:
  - JARVIS NEVER requests, reads, stores, or inputs the user's UPI PIN.
  - User personally verifies details and inputs PIN inside the official third-party UPI app (GPay / PhonePe / Paytm).

---

## 23. Call Agent Limitations & Strategy

- Marked as **EXPERIMENTAL / OPTIONAL** (`ENABLE_CALL_AGENT=false` by default, Phase 10). `[EXPERIMENTAL FEATURE]`
- **Platform Limitations**: Unrooted Android OS strictly regulates call audio routing and automated dialer access.
- Call Agent is an isolated optional module that does not block core system operation.

---

## 24. 3D Sci-Fi UI Architecture

- Built using **React 18**, **TypeScript**, **Three.js**, **React Three Fiber (`@react-three/fiber`)**, and **Drei (`@react-three/drei`)**.
- Visual Orb Core reflects backend states (`IDLE`, `LISTENING`, `THINKING`, `EXECUTING`, `SPEAKING`, `ERROR`).
- **Decoupled Architecture**: 3D UI is purely a client visual layer (`ENABLE_3D_UI=true`). The backend functions 100% headlessly if UI is disconnected.

---

## 25. Database Architecture & Schema Requirements

### Phase 0 Core Database
- SQLite database (`jarvis.db`) in WAL mode.
- Schema: basic settings, key-value memories, audit log table.

### Future Database Requirements (Phase 2+)
- Explicit relational schemas (`users`, `devices`, `memories`, `conversations`, `messages`, `tasks`, `tool_calls`, `call_records`, `audit_logs`).
- Foreign keys, performance indexes, migration framework (`alembic`).
- Data retention policies, privacy deletion handlers ("forget user"), and automated backups.
- Vector extension (`sqlite-vec`) for 384-dimensional semantic embeddings.

---

## 26. API & Event Architecture

- `POST /api/v1/chat`: Synchronous text interaction endpoint.
- `WS /api/v1/ws`: Bidirectional real-time WebSocket stream for UI telemetry, audio streaming, and tool logs.
- `GET /api/v1/system/telemetry`: Hardware metrics endpoint.
- Transport concerns (FastAPI / WebSockets) are strictly decoupled from core Orchestrator logic.

---

## 27. Configuration & Secret Management

- Environment variables loaded via `pydantic-settings` from `.env`.
- Secrets (API keys, tokens) are never committed to git.
- Parameter separation: `SystemConfig`, `ModelConfig`, `FeatureFlags`.

---

## 28. Security Architecture Summary

- **Least Privilege Execution**: Scoped tool capabilities.
- **No Direct Shell Access**: Arbitrary command execution disabled.
- **Immutable Audit Logging**: Tool calls and permission checks logged to SQLite audit table.
- **Zero Credential / PIN Access**: Financial app PINs completely isolated.
- **Cloud Fallback Privacy Check**: `ALLOW_CLOUD_FALLBACK=false` default; restricted user data blocked from leaving local device.

---

## 29. Observability & Tracing

- Structured JSON logs with unique `request_id` context tracing.
- Tracked metrics: Model latency, STT/TTS processing time, tool execution duration, memory retrieval latency, permission decisions.
- Secrets automatically redacted from log streams.

---

## 30. Failure Recovery & Graceful Degradation

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

## 31. Feature Flags

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

## 32. Project Directory Structure

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
│   │   ├── services/              # API & WebSocket client
│   │   └── types/                 # TypeScript interfaces
├── android/                        # Companion Kotlin app
├── data/                           # SQLite database & local models
├── docs/                           # Documentation
├── .env.example
├── .gitignore
├── README.md
└── implementation.md              # Single Master Specification Document
```

---

## 33. Local Model Benchmarking Strategy

Before hardcoding default inference models, a systematic benchmarking suite will run on host hardware (RTX 3050 6GB VRAM + 24GB RAM): `[UNVERIFIED / TO BE TESTED]`
- **Metrics**: Startup time, time-to-first-token (TTFT), average latency, tokens/sec, VRAM utilization, RAM footprint, context window handling, structured JSON tool precision, multilingual performance (EN, TE, HI, TA, code-switching), and long-session stability.
- **Candidate Models**: `qwen2.5:3b-instruct`, `llama3.2:3b`, `qwen2.5:7b-instruct-q4_K_M`, `mistral:7b-instruct`.
- Model with best tool calling precision and lowest TTFT under 6GB VRAM constraint will be set as default.

---

## 34. Phased Roadmap Summary

- **PHASE 0: JARVIS CORE FOUNDATION**
- **PHASE 1: COMPLETE TOOL & PERMISSION ENGINE**
- **PHASE 2: MEMORY ENGINE & VECTOR RAG**
- **PHASE 3: VOICE ENGINE**
- **PHASE 4: MULTILINGUAL & CODE-SWITCHING ENGINE**
- **PHASE 5: COMPUTER CONTROL LAYER**
- **PHASE 6: ANDROID COMPANION & ADB BRIDGE**
- **PHASE 7: VISION ENGINE**
- **PHASE 8: WEB RESEARCH ENGINE**
- **PHASE 9: 3D SCI-FI UI ENGINE**
- **PHASE 10: ADVANCED / EXPERIMENTAL FEATURES**
- **PHASE 11: END-TO-END HARDENING**

---

## 35. Phase Dependency Matrix

| Phase | Goal | Inputs / Dependencies | Components Introduced | Components Modified | Components NOT Included | Acceptance Criteria | Tests | Outputs / Artifacts | Risks | Exit Conditions |
|---|---|---|---|---|---|---|---|---|---|---|
| **Phase 0** | Working Core CLI Foundation | Host environment, Python, Ollama daemon | Project scaffolding, Config, Logging, AIProvider (`OllamaProvider`), Model Adapter, Orchestrator, Minimal `ToolRegistry`, Minimal SAFE permission check, Result Verifier, Text CLI | None | Voice, Android, Vision, 3D UI, Web Research, Call Agent | Text CLI responds to time, calculation, system status via Ollama; result verified; safe errors on invalid calls | Unit tests, Integration tests, Security tests, CLI Smoke test | `app/main.py`, Core modules, CLI interface | Ollama daemon connection failure | All Phase 0 tests pass; manual CLI smoke test succeeds |
| **Phase 1** | Production Tool & Permission System | Phase 0 Core | Full `ToolRegistry`, Full Schemas, Preconditions, Timeouts, Multi-tier `PermissionEngine` (`SAFE`, `CONFIRMATION`, `HIGH_RISK`), Interactive prompts, Audit logger, Rollback handlers | Minimal Tool Registry & Permission Check | Voice, Android, Vision, 3D UI, Web Research | Structured confirmation flow for sensitive tools; immutable audit logging; permission bypass attempt rejected | Security bypass tests, Malformed schema tests, Audit log tests | `app/tools/`, `app/security/` | Permission bypass vulnerabilities | 100% security test pass; zero permission bypass |
| **Phase 2** | Persistent Memory & Vector RAG | Phase 1 Tools | SQLite Memory Store, Vector RAG Store (`sqlite-vec`), Embedding Generator (`all-MiniLM-L6-v2`), Memory Agent | Orchestrator context assembly | Voice, Android, Vision, 3D UI | Explicit facts saved, retrieved, updated, deleted; semantic RAG retrieves relevant facts into prompt | RAG precision tests, Memory CRUD tests | `app/memory/` | Vector extension compatibility | Memory CRUD & RAG retrieval verified |
| **Phase 3** | Local Voice Pipeline | Phase 2 Memory | VAD (`silero-vad`), Wake Word ("Hey JARVIS"), Clap Detector, STT (`faster-whisper`), TTS (`edge-tts`), Audio Streamer | Orchestrator audio input/output handlers | Android, Vision, 3D UI | Hands-free wake word & double-clap wake JARVIS; speech correctly transcribed and spoken | Voice STT accuracy tests, VAD latency tests | `app/voice/` | Microphone audio driver issues | Voice wake & response loop operating smoothly |
| **Phase 4** | Multilingual & Code-Switching | Phase 3 Voice | Language Identifier (Confidence-aware), Code-switching Segmenter, Regional TTS voices (EN, TE, HI, TA) | Voice Agent & Prompt Builder | Android, Vision, 3D UI | Detects EN/TE/HI/TA and mixed speech; responds in caller/user primary regional language | Multilingual intent recognition tests | `app/voice/multilingual.py` | Low-confidence language misclassification | Multilingual test suite passes with >90% accuracy |
| **Phase 5** | Host Computer Control Layer | Phase 1 Tools | Computer Agent, Process Manager, Window Controller, Screenshot Tool, Safe Workspace File Manager | Tool Registry | Android, Vision, 3D UI | Launches/closes authorized apps; captures screenshots; checks OS process state empirically | App launch verification tests, File allowlist tests | `app/tools/computer.py` | OS permission restriction on process kill | All computer tools verified empirically via `psutil` |
| **Phase 6** | Android Companion & ADB Bridge | Phase 5 Computer | Kotlin Companion App, ADB Bridge Server, Mutual Pairing Protocol, Phone Telemetry Listener, Mobile Agent | Device Registry | Vision, 3D UI | Phone pairs securely; PC backend reads phone battery/notifications and sends SMS via ADB/Companion | ADB connection tests, Pairing auth tests | `android/`, `app/devices/` | ADB USB disconnects | Secure pairing & ADB tethering operational |
| **Phase 7** | Vision Engine | Phase 5 Computer | Multimodal Model Integration (`llama3.2-vision`/`qwen2-vl`), Vision Agent, OCR Engine | Orchestrator image processing | 3D UI, Call Agent | Analyzes screenshots and camera snapshots; performs desktop OCR | Image grounding tests, OCR accuracy tests | `app/vision/` | High VRAM usage during vision inference | Vision model runs within VRAM limits |
| **Phase 8** | Web Research Engine | Phase 2 Memory | Web Research Agent, Search Provider Abstraction (Tavily/SearXNG), Page Extractor, Citation Synthesizer | Tool Registry | 3D UI, Call Agent | Performs multi-query search; extracts text; synthesizes summary with valid source URLs | Search provider failover tests, Citation verification tests | `app/tools/research.py` | Web scraping rate limits | Research reports contain 100% verified source links |
| **Phase 9** | 3D Sci-Fi UI Engine | Phase 3 Voice & Phase 1 Tools | React 3D HUD, Three.js Orb visualizer, WebSocket Telemetry Streamer, Visual State Synchronizer | Frontend UI store | Call Agent | Sci-Fi 3D Orb animates according to assistant state (`IDLE`, `LISTENING`, `THINKING`, `EXECUTING`, `SPEAKING`, `ERROR`) | 3D render smoke tests, WebSocket latency tests | `frontend/src/three/` | WebGL GPU performance bottlenecks | 3D UI renders smoothly without crashing backend |
| **Phase 10** | Advanced & Experimental Features | Phase 6 & Phase 9 | Experimental Call Agent (`ENABLE_CALL_AGENT`), Smart Home IoT Agent, UPI Payment Flow Intent Launcher | Orchestrator | None | Screens incoming calls; captures caller message; launches UPI payment app safely without PIN access | Call screening mock tests, UPI intent tests | `app/calls/`, `app/tools/upi.py` | Android Telecom API OS version limitations | Call screening & UPI intent launch verified |
| **Phase 11** | End-to-End Testing & Hardening | All prior phases | Security Audit Suite, Failure Stress Tester, Performance Profiler | Entire System | None | System survives local AI crash, network outage, malformed input; 100% test pass across all modules | Security audit tests, Chaos failure tests | `tests/e2e/`, Final Documentation | Production security vulnerabilities | Definition of Done satisfied for full system |

---

## 36. Testing Strategy

Testing is integrated into every phase across five distinct test categories:

1. **Unit Tests**: Verify individual functions, schema validators, tool logic, and memory stores.
2. **Integration Tests**: Verify Orchestrator $\rightarrow$ Model Adapter $\rightarrow$ Tool Registry $\rightarrow$ Result Verifier pipeline.
3. **Security Tests**: Verify Permission Engine rejects unauthorized/high-risk tool requests, malformed payloads, and injection attempts.
4. **Failure & Resilience Tests**: Verify system degrades gracefully when local AI, network, or tools throw exceptions.
5. **Acceptance Smoke Tests**: End-to-end manual and automated scenarios verifying user-facing criteria.

---

## 37. Universal Definition of Done

A phase is declared **COMPLETE** if and only if all 9 conditions are met:

1. **Required Implementation Exists**: All components defined for the phase are implemented.
2. **Unit Tests Pass**: 100% pass rate on phase unit tests.
3. **Integration Tests Pass**: Pipeline integration tests execute successfully.
4. **Security Tests Pass**: Permission boundaries and schema validations pass security checks.
5. **Failure Scenarios Tested**: Graceful degradation verified under failure conditions.
6. **Acceptance Criteria Satisfied**: Every phase acceptance criterion is demonstrably fulfilled.
7. **Documentation Updated**: Technical specifications, API docs, and setup instructions are updated.
8. **Zero Blocking Defects**: No high-severity bugs or unhandled crashes remain open.
9. **No Unapproved Architectural Deviation**: Implementation adheres strictly to `implementation.md`.

---

## 38. Architectural Change Control Policy

> [!CAUTION]
> **Architectural Change Control Policy**: No coding agent or developer may silently alter core interfaces (`AIProvider`, `Agent`, `Orchestrator`, `Tool`, `PermissionEngine`, `ResultVerifier`), security boundaries, permission levels, data contracts, or phase boundaries.

If an architectural change is required during implementation:
1. Document the problem and justification.
2. Evaluate impact on security, testing, and Stage-2 extensibility.
3. Submit a formal proposal for user review and approval before mutating architectural specifications or code structures.

---

## 39. Known Platform Limitations & Trade-Offs

1. **UPI PIN Automation**: Programmatic PIN entry is impossible due to Android OS security boundaries. Payment flow launches the official UPI app; user enters PIN manually. `[VERIFIED FACT]`
2. **Unrooted Call Audio Access**: Direct in-call audio recording/streaming on unrooted Android devices is restricted by carrier and platform security policies. Call screening relies on telecom framework audio hooks or speaker/mic loopback where permitted. `[VERIFIED FACT]`
3. **VRAM Constraints**: 6 GB VRAM limits local inference to 3B–7B 4-bit quantized models for real-time responsiveness. `[VERIFIED FACT]`

---

## 40. Stage-2 Extensibility & Verification Checklist

- Core interfaces (`AIProvider`, `Agent`, `Tool`, `MemoryStore`, `PermissionEngine`, `ResultVerifier`) are decoupled and ready for Stage-2 custom model implementations.
- Zero hardcoded vendor lock-in inside orchestration loops.
- All application modules controlled via explicit environment feature flags.

---

## 41. Final Verification Checklist

- [x] Single master specification document finalized (`implementation.md`).
- [x] Full original JARVIS vision and target capabilities preserved.
- [x] Stage 1 (Engineering Baseline) and Stage 2 (Self-Built Version) clearly separated.
- [x] LLM execution boundary strictly enforced (Intelligence vs. Execution).
- [x] Provider-independent Model Adapter and normalized contracts defined.
- [x] Phase 0 defined as minimal functional core foundation.
- [x] Phase 1 defined as complete tool + permission engine infrastructure.
- [x] All 12 development phases mapped in Phase Dependency Matrix.
- [x] Python version strategy specified with `PENDING VERIFICATION` status.
- [x] Cloud fallback privacy controls (`ALLOW_CLOUD_FALLBACK=false`) defined.
- [x] Android security and pairing protocol specified.
- [x] Empirical result verification mandated for all tools.
- [x] Universal Definition of Done and Architectural Change Control Policy established.

---

### Specification Status
- Architecture specification **100% complete, rigorous, and verified**.
- Ready for explicit Phase 0 implementation directive.
