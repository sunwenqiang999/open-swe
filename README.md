<div align="center">
  <h1>🤖 Open SWE</h1>
  <p><em>This project is created and maintained by an AI coding agent.</em></p>
</div>

<div align="center">
  <img src="https://img.shields.io/badge/AI%20Created-Open%20SWE-8A2BE2" alt="AI Created">
  <img src="https://img.shields.io/badge/license-MIT-blue" alt="License">
  <img src="https://img.shields.io/badge/built%20with-LangGraph-1e3a5f" alt="Built with LangGraph">
</div>

<br>

Open SWE is an open-source framework for building internal coding agents — Slackbots, CLIs, and web apps that automate software engineering tasks. Built on [LangGraph](https://langchain-ai.github.io/langgraph/) and [Deep Agents](https://github.com/langchain-ai/deepagents), it orchestrates isolated cloud sandboxes, provides curated tooling, and supports invocation from Slack, Linear, and GitHub.

> [!NOTE]
> This repository is **entirely built by AI** — every line of code, every commit, and every feature in this project was authored autonomously by the Open SWE coding agent framework running in isolated cloud sandboxes. It serves as both a product and a living demonstration of the framework itself.

## Features

- **🤖 AI-Authored** — Every contribution in this repo is created by an AI coding agent operating autonomously
- **🏖️ Isolated Sandboxes** — Each task runs in its own cloud sandbox, fully contained
- **🔧 Curated Toolset** — Focused, purpose-built tools for software engineering tasks
- **🔗 Multi-Platform Triggers** — Invoke from Slack, Linear, or GitHub
- **🧩 Subagent Orchestration** — Spawn child agents for parallel subtasks
- **🔄 Automatic PR Creation** — Code changes are committed and PRs are opened automatically

## Architecture

Open SWE follows the same architecture used by elite engineering orgs for their internal coding agents:

| Component | Description |
|---|---|
| **Agent Harness** | Composed on Deep Agents framework with customizable system prompts and middleware |
| **Sandbox** | Remote Linux environments — supports Modal, Daytona, Runloop, LangSmith |
| **Tools** | Small, curated set (~15 tools) for file operations, API calls, and communication |
| **Context** | AGENTS.md + full issue/thread context injected at start |
| **Orchestration** | Subagents + deterministic middleware hooks |
| **Invocation** | Slack bot, Linear comments, GitHub PR comments |
| **Validation** | Linters, formatters, and tests run before every commit |

## Getting Started

To get started with your own instance of Open SWE, refer to the [main repository](https://github.com/langchain-ai/open-swe) for installation and customization guides.

## License

MIT