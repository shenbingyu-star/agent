# AI 旅游规划助手

基于 Spring Boot 3 + Spring AI 的企业级 AI 旅游规划助手，支持多轮对话、记忆持久化、RAG 知识库检索，基于 ReAct 模式实现自主思考与工具调用，可为用户自动生成完整的旅游方案。

## 技术栈

- 后端：Spring Boot 3、Spring AI、Spring AI Alibaba
- AI 能力：RAG、Tool Calling、MCP、ReAct
- 模型：DashScope（通义千问）、DeepSeek、Ollama
- 存储：MySQL、PgVector
- 前端：SSE、Fetch API
- 部署：Docker、Serverless

## 功能特性

- 多轮对话与记忆持久化（基于文件系统 + Kryo 序列化）
- RAG 知识库检索与查询增强（查询重写、多查询扩展、上下文增强）
- 工具调用：文件操作、联网搜索、网页抓取、资源下载、终端操作、PDF 生成
- MCP 集成：高德地图、图片搜索
- 自主规划智能体：基于 ReAct 模式，支持任务分解、自主决策、循环执行
- SSE 流式响应，实时展示智能体执行进度

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+（如使用 PgVector）
- Ollama（可选，本地模型）

### 配置

修改 `application.yml `、 `mcp-servers.json `，填入你的 API Key 和数据库信息。

### 启动

```bash
mvn spring-boot:run
```