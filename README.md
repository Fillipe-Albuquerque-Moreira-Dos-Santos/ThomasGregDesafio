# 💼 Desafio Técnico – Sistema de Cadastro de Clientes

## 📘 Descrição

Este projeto é uma solução para o desafio de implementar um sistema de **cadastro de clientes**, com as funcionalidades de criar, editar, listar e excluir **clientes e logradouros**. A aplicação utiliza:

- Frontend: **JSF + PrimeFaces**, HTML, CSS, JavaScript  
- Backend: **Spring Boot (Java 8)**  
- Banco de Dados: **SQL Server 2022** (executado via Docker)  
- IDE Utilizada: IntelliJ  
- Gerenciador de banco: **DBeaver**

---

## 📐 Arquitetura da Solução

### Backend
- Spring Boot com Java 8  
- API RESTful  
- Swagger UI para documentação da API  
- JPA (Hibernate) para mapeamento de entidades  
- SQL Server 2022 para persistência dos dados  
- Autenticação HTTP Basic  

> 📄 A documentação da API pode ser acessada via Swagger em:  
> `http://localhost:8080/swagger-ui.html` 

### Frontend
- JSF (JavaServer Faces)  
- PrimeFaces  
- HTML, CSS, JS  
- Servidor de aplicação: WildFly 18.0.1 (Java EE)

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
