# oficina-execution-service

**Responsabilidade:** Fila de execução, diagnóstico, reparos e finalização das OS.

Tech Challenge SOAT — Fase 4 | Microsserviço 3 de 3

---

## Stack

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Banco | **MongoDB** (NoSQL) |
| Mensageria | RabbitMQ (Spring AMQP) |
| Segurança | OAuth2 / JWT (Keycloak) |
| Observabilidade | Micrometer + Prometheus |
| Testes | JUnit 5 + Cucumber (BDD) + Flapdoodle Embedded MongoDB |
| Qualidade | JaCoCo ≥ 80% + SonarCloud |

> **MongoDB** foi escolhido por oferecer esquema flexível para o histórico de execução (lista de eventos com campos variáveis) e satisfazer o requisito NoSQL do enunciado.

---

## Eventos publicados / consumidos

| Direção | Routing Key | Descrição |
|---|---|---|
| Consome | `pagamento.confirmado` | Adiciona OS à fila de execução |
| Consome | `pagamento.falhou` | Remove OS da fila (compensação) |
| Publica | `execucao.iniciada` | Técnico iniciou diagnóstico |
| Publica | `execucao.finalizada` | OS finalizada com sucesso |
| Publica | `execucao.falhou` | Falha durante execução (compensação) |

---

## Como rodar localmente

```bash
# Subir infraestrutura (do oficina-os-service)
docker compose -f ../oficina-os-service/docker-compose.infra.yml up -d

./mvnw spring-boot:run
```

Porta: **8083** | Swagger: `http://localhost:8083/api/swagger-ui.html`

---

## Estrutura MongoDB

Coleção `fila_execucao`:
- `os_id` — ID da OS (indexado, único)
- `status` — StatusExecucao enum
- `tecnico_responsavel`
- `historico` — array de eventos (status + timestamp + observação)
- `criado_em`, `atualizado_em`

---

## CI/CD

- `.github/workflows/ci.yml` — build, testes, SonarCloud, push Docker
