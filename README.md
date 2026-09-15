# 智能退票客服系统

基于 LangChain4j + RAG + Tool Calling 的 LLM 业务应用，面向铁路退票场景，用户通过网页与 AI 多轮对话完成退票咨询与办理，实现“能聊天”到“能办事”的业务闭环。

## 技术栈

- 后端：Spring Boot 3.0、LangChain4j
- AI 能力：RAG、Tool Calling
- 模型：DeepSeek-Chat、Qwen Embedding
- 存储：MySQL、MyBatis-Plus
- 前端：SSE、Fetch API

## 功能特性

- 退票规则查询、订单状态查询、退票办理
- RAG 知识库检索，基于退票政策文档向量化
- 多轮对话与记忆持久化（按用户隔离）
- SSE 流式响应，前端打字机效果与 Markdown 渲染
- 订单 CRUD，乐观锁与逻辑删除保障并发安全

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 配置

修改 `application.properties`，填入你的 API Key 和数据库信息。

### 启动

```bash
mvn spring-boot:run
```