# 💼 Desafio Técnico – Sistema de Cadastro de Clientes

## 📘 Descrição

Este projeto é uma solução para o desafio de implementar um sistema de **cadastro de clientes**, com as funcionalidades de criar, editar, listar e excluir **clientes e logradouros**. A aplicação utiliza:

- Frontend: **JSF + PrimeFaces**, HTML, CSS, JavaScript
- Backend: **Spring Boot (Java 8)**
- Banco de Dados: **SQL Server 2022** (executado via Docker)
- IDE recomendada: Eclipse ou IntelliJ
- Gerenciador de banco: **DBeaver**

---

## 📐 Arquitetura da Solução

### Backend
- Spring Boot com Java 8
- API RESTful
- JPA (Hibernate) para mapeamento de entidades
- SQL Server 2022 para persistência dos dados
- Autenticação HTTP Basic

### Frontend
- JSF (JavaServer Faces)
- PrimeFaces
- HTML, CSS, JS
- Servidor de aplicação: WildFly 26 (Java EE)

---

## 🧱 Modelagem de Dados

```sql
CREATE TABLE Cliente (
    id_cliente INT PRIMARY KEY IDENTITY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    logotipo VARBINARY(MAX)
);

CREATE TABLE Logradouro (
    id_logradouro INT PRIMARY KEY IDENTITY,
    id_cliente INT,
    logradouro VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)
);

### 1. Instalar Java 8
Baixe e instale o Java 8 (JDK 1.8):

🔗 https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html

---

### 2. Instalar Maven
Maven é necessário para compilar o backend:

🔗 https://maven.apache.org/install.html

---

### 3. Instalar Docker
Docker será usado para executar o SQL Server 2022:

🔗 https://docs.docker.com/get-docker/

---

### 4. Subir SQL Server 2022 com Docker

Execute o comando abaixo no terminal:

```bash
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Your_password123" \
   -p 1433:1433 --name sql_server_2022 \
   -d mcr.microsoft.com/mssql/server:2022-latest
