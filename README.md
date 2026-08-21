# JARVIS: Multi-Device, Multilingual, Voice-Controlled, 3D AI Personal Assistant

JARVIS is designed around a mandatory core principle: **Strict separation of Intelligence (LLMs/Agents) from Execution (Deterministic Tool & Permission Engine)**.

## Project Vision & Evolution
- **Stage 1 (Current Project - Engineering Baseline)**: A working engineering reference implementation built with modular interfaces.
- **Stage 2 (Future Self-Built Version)**: Rebuilt independently by replacing Stage 1 components (like AI Provider, Orchestrator, or Vector Memory) with custom local AI models or custom modules without changing the surrounding system.

---

## Phase 0: JARVIS Core Foundation (Complete)

Phase 0 provides a working headless text CLI and REST/WebSocket API backend.

### Setup Instructions

1. **Prerequisites**
   - Ensure [Ollama](https://ollama.com/) is installed and running on `http://localhost:11434`.
   - Download the default reasoning model:
     ```bash
     ollama pull qwen2.5:3b-instruct
     ```

2. **Installation**
   - Navigate to the `backend` directory:
     ```bash
     cd backend
     ```
   - Virtual environment is initialized under `.venv`.
   - Activate the virtual environment:
     - **Windows (PowerShell)**: `.venv\Scripts\Activate.ps1`
     - **Bash/Mac**: `source .venv/bin/activate`
   - Install dependencies (already installed in Phase 0):
     ```bash
     pip install -r requirements.txt
     ```

3. **Running the Text CLI Interface**
   - Execute the CLI script:
     ```bash
     python -m app.cli
     ```
   - Ask standard conversational questions or trigger safe tool invocations:
     - `"What's the current time?"` (calls `get_time`)
     - `"Calculate 25 * 18"` (calls `calculator`)
     - `"Show system diagnostic status"` (calls `system_status` to view active CPU/RAM specs)

4. **Running the Web / API Server**
   - Start the FastAPI application:
     ```bash
     uvicorn app.main:app --reload --port 8000
     ```
   - Access Health endpoints:
     - REST API: `http://localhost:8000/api/v1/health`
     - WebSocket: `ws://localhost:8000/api/v1/ws`

5. **Executing Tests**
   - Run the automated test suite from the `backend` folder:
     ```bash
     python -m pytest
     ```

---

## Technical Specifications (Phase 0)
- **Runtimes**: Python 3.14.3, FastAPI, Pydantic v2
- **Supported Tools**: `get_time`, `calculator`, `system_status`
- **Security Check**: Auto-approves `SAFE` permissions and enforces verification of outputs via `ResultVerifier`. Unrestricted terminal commands are blocked.
