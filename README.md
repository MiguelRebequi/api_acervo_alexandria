<a id="readme-top"></a>

<div align="center">
  
<h1>Acervo de Alexandria</h1>
<h6>Plataforma de E-Commerce literário e gestão de catálogo via API REST</h6>
</div>

***

<div align="center">
  
  [![Java](https://img.shields.io/badge/Java-21-%23F29111?logo=openjdk&logoColor=%23F29111)](https://docs.oracle.com/en/java/javase/21/index.html)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-%236DB33F?logo=springboot&logoColor=%236DB33F)](https://spring.io/projects/spring-boot#overview)
  [![Maven](https://img.shields.io/badge/Maven-3.9-%23C71A36?logo=apachemaven&logoColor=%23C71A36)](https://maven.apache.org/)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-%23336791?logo=postgresql&logoColor=%23336791)](https://www.postgresql.org/)
  [![Spring Security](https://img.shields.io/badge/Spring_Security-JWT_%26_BCrypt-%236DB33F?logo=springsecurity&logoColor=%236DB33F)](https://spring.io/projects/spring-security)
  
</div>
<hr>

<br>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Guia de conteúdo</summary>
  <ol>
    <li>
      <a href="#sobre-o-projeto">Sobre o Projeto</a>
      <ul>
        <li><a href="#contextualização">Contextualização</a></li>
        <li><a href="#a-proposta">A Proposta</a></li>
      </ul>
    </li>
    <li><a href="#tecnologias-utilizadas">Tecnologias Utilizadas</a></li>
    <li><a href="#como-funciona">Como Funciona</a></li>
    <li><a href="#arquitetura-do-projeto-das">Arquitetura do Projeto (DAS)</a></li>
    <li><a href="#estratégia-de-segurança-e-autenticação">Estratégia de Segurança e Autenticação</a></li>
    <li><a href="#estrutura-de-pastas">Estrutura de Pastas</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#guia-de-endpoints">Guia de Endpoints</a></li>
    <li><a href="#testando-a-api-na-prática-swagger">Testando a API na Prática (Swagger)</a></li>
    <li><a href="#licença">Licença</a></li>
    <li><a href="#agradecimentos">Agradecimentos</a></li>
    <li><a href="#contato">Contato</a></li>
  </ol>
</details>

<br>

## Sobre o Projeto
<p>
  O <b>Acervo de Alexandria</b> é uma aplicação web de comércio eletrônico literário composta por uma API RESTful desenvolvida em Java com Spring Boot e um Front-end desacoplado em HTML5, CSS3 e JavaScript modular. O sistema permite o gerenciamento completo de catálogo de livros, busca avançada por múltiplos atributos (editora, idioma, formato, avaliação, ano de publicação e faixa de preço), cadastro de clientes, carrinho de compras e processamento de pedidos com controle de estoque.
</p>

### Contextualização
<p>
  Plataformas de comércio eletrônico literário exigem organização eficiente de metadados bibliográficos (ISBN, editora, idioma, formato físico ou digital e avaliações) aliada à consistência transacional no controle de estoque e à proteção rigorosa das credenciais e dados pessoais dos usuários.
</p>

### A Proposta
<p>
  Nossa arquitetura propõe uma plataforma cliente-servidor desacoplada, onde o Back-end expõe endpoints REST padronizados protegidos por controle de acesso baseado em perfis (<code>USER</code> e <code>ADMIN</code>), enquanto o Front-end consome os dados assincronamente via Fetch API. Toda a persistência é garantida por um banco de dados relacional PostgreSQL com mapeamento objeto-relacional via Spring Data JPA.
</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Tecnologias Utilizadas

<div align="center">
  
| Tecnologia | Versão | Função |
| :--- | :--- | :--- |
| Java | 21 | Linguagem Base |
| Spring Boot | 3.5+ | Framework Backend |
| PostgreSQL | 17 | Banco de Dados Relacional |
| Maven | 3.9+ | Gerenciador de dependências e build |
| Spring Data JPA / Hibernate | - | Persistência de dados e mapeamento objeto-relacional |
| Spring Security | - | Filtros de segurança, CORS e controle de rotas por Role |
| Auth0 Java JWT | - | Emissão e validação de tokens de acesso Stateless |
| BCrypt | - | Hashing criptográfico unidirecional de senhas |
| Swagger / OpenAPI 3 | - | Documentação interativa e testes de endpoints |

</div>

<p><i>
  Nota: Este repositório contém o código-fonte da API (Backend). A interface gráfica do usuário (Frontend) foi desenvolvida utilizando HTML5, CSS3 e JavaScript Vanilla (ES Modules) e comunica-se com esta API via requisições assíncronas (Fetch API).
</i></p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Como Funciona
<p>
<ol>
  <li><b>Consulta Pública ao Catálogo:</b> Qualquer visitante pode navegar pela vitrine, pesquisar livros por título ou autor e aplicar filtros dinâmicos por editora, idioma, formato (Capa Comum, Capa Dura, E-book, Audiobook), avaliação, ano de publicação e preço.</li>
  <br>
  <li><b>Cadastro e Autenticação:</b> Novos clientes criam sua conta informando dados pessoais, endereço completo, CPF único e senha. As credenciais são armazenadas separadamente na tabela <code>credencial</code> com hash BCrypt.</li>
  <br>
  <li><b>Emissão de Token JWT:</b> Ao efetuar login (ou redefinir a senha), a API autentica as credenciais e emite um token JWT assinado, contendo o identificador do usuário e seu perfil de acesso (<code>ROLE_USER</code> ou <code>ROLE_ADMIN</code>).</li>
  <br>
  <li><b>Carrinho e Fechamento de Pedido:</b> Usuários autenticados adicionam livros ao carrinho e finalizam compras. O sistema valida a disponibilidade em estoque, abate as quantidades e registra o pedido com seus respectivos itens e preços unitários históricos.</li>
  <br>
  <li><b>Gestão Administrativa:</b> Usuários com perfil <code>ADMIN</code> possuem acesso exclusivo às rotas de cadastro, edição, inativação lógica (<i>soft delete</i>) e reativação de livros no acervo.</li>
</ol>
</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Arquitetura do Projeto (DAS)

A aplicação foi desenhada sob o paradigma **Client-Server (Desacoplado)**, separando a interface do usuário da lógica de negócio e da persistência de dados.

No Back-end, o sistema segue o padrão de **Arquitetura em Camadas (Layered Architecture)** integrado ao **PostgreSQL** via driver JDBC e pool de conexões **HikariCP**:

<ul>
  <li><b>Controller (Camada de Exposição):</b> Recebe as requisições HTTP/JSON, aciona as validações de entrada via Bean Validation (<code>@Valid</code>) e retorna respostas REST padronizadas.</li>
  <li><b>DTO (Data Transfer Objects):</b> Isola as entidades de domínio da camada de apresentação, evitando exposição indevida de atributos internos (como <code>senhaHash</code>) e recursão infinita na serialização JSON.</li>
  <li><b>Service (Camada de Negócio):</b> Orquestra as regras de domínio, incluindo verificação de unicidade de e-mail/CPF, cálculo de totais de carrinho, baixa de estoque e exclusão lógica (<code>ativo = false</code>).</li>
  <li><b>Repository (Camada de Persistência):</b> Interfaces que estendem <code>JpaRepository</code> para traduzir operações de domínio em consultas SQL otimizadas no PostgreSQL via Hibernate.</li>
  <li><b>Security (Camada de Blindagem):</b> Intercepta todas as requisições HTTP antes dos controllers para validar políticas de CORS, tokens JWT e permissões por perfil.</li>
</ul>

```text
┌─────────────────────────────────────────────────────────────┐
│                   Frontend (HTML/CSS/ES Modules)            │
│   (Vitrine, Filtros Dinâmicos, Fetch API, LocalStorage JWT) │
└──────────────────────┬──────────────────────────────────────┘
                       │ Requisições HTTP/JSON (CORS Enabled)
┌──────────────────────▼──────────────────────────────────────┐
│                   API REST (Spring Boot)                    │
│     (SecurityFilterChain, SecurityFilter JWT, Controllers)  │
└──────────────────────┬──────────────────────────────────────┘
                       │ Regras de Negócio (Services & DTOs)
┌──────────────────────▼──────────────────────────────────────┐
│              Camada de Persistência (Spring Data JPA)       │
│           (Hibernate ORM & Pool de Conexões HikariCP)       │
└──────────────────────┬──────────────────────────────────────┘
                       │ JDBC / SQL
┌──────────────────────▼──────────────────────────────────────┐
│                  Banco de Dados PostgreSQL                  │
│   (usuario, credencial, livro, carrinho, pedido, item_pedido)│
└─────────────────────────────────────────────────────────────┘
```

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Estratégia de Segurança e Autenticação

A segurança da API foi implementada de forma **Stateless** (sem armazenamento de sessão em memória no servidor), garantindo escalabilidade e proteção contra acessos não autorizados:

<ul>
  <li><b>Criptografia de Senhas com BCrypt:</b> Nenhuma senha é armazenada em texto plano. No cadastro ou redefinição de senha, o <code>BCryptPasswordEncoder</code> aplica um algoritmo de hash adaptativo com geração automática de <i>salt</i> aleatório, armazenando apenas o hash de 60 caracteres na entidade <code>Credencial</code> (relacionamento 1:1 com <code>Usuario</code>).</li>
  <li><b>Autenticação Stateless via JWT:</b> Após a validação das credenciais pelo <code>AuthenticationManager</code>, o <code>TokenService</code> gera um JSON Web Token assinado com algoritmo HMAC256, contendo o e-mail do usuário (<i>subject</i>), sua <code>role</code> e tempo de expiração dinâmico baseado no fuso horário do sistema.</li>
  <li><b>Interceptação e Autorização por Perfil (RBAC):</b> O filtro personalizado <code>SecurityFilter</code> extrai o cabeçalho <code>Authorization: Bearer &lt;token&gt;</code>, valida a assinatura e injeta o usuário autenticado no <code>SecurityContextHolder</code>. Rotas públicas (catálogo <code>GET /livros/**</code>, login e cadastro) são liberadas, rotas de gerenciamento de estoque exigem <code>ROLE_ADMIN</code> e operações de carrinho/pedidos exigem autenticação válida.</li>
</ul>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Estrutura de Pastas

```text
api-acervo-alexandria/
├── src/
│   ├── main/
│   │   ├── java/com/MMR/api_acervo_alexandria/
│   │   │   ├── controller/      # Endpoints REST (Autenticacao, Livro, Usuario, Carrinho, Pedido)
│   │   │   ├── dto/             # Records de Request e Response com validações Bean Validation
│   │   │   ├── infra/           # Tratamento global de exceções (TratadorDeErros)
│   │   │   ├── model/           # Entidades JPA e Enumerations (Role, FormatoLivro, StatusPedido)
│   │   │   ├── repository/      # Interfaces Spring Data JPA para acesso ao PostgreSQL
│   │   │   ├── security/        # Configurações de CORS, SecurityFilterChain, SecurityFilter e TokenService
│   │   │   └── service/         # Regras de negócio, controle de estoque e autenticação
│   │   └── resources/
│   │       ├── application.properties # Configurações de conexão PostgreSQL, JPA e chave secreta JWT
│   │       └── data.sql               # Carga inicial idempotente do catálogo de livros
│   └── test/                    # Suíte de testes da aplicação
├── pom.xml                      # Gerenciador de dependências e ciclo de vida do Maven
└── README.md                    # Documentação técnica e arquitetural do projeto
```

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Getting Started
<p>Siga estas instruções para configurar e executar o projeto em sua máquina local para fins de desenvolvimento e teste.</p>

### Pré-requisitos
<ul>
  <li><b>Java JDK 21:</b> Versão utilizada no desenvolvimento e compilação da API.</li>
  <li><b>Maven 3.9+:</b> Gerenciador de dependências (ou uso do wrapper <code>./mvnw</code>).</li>
  <li><b>PostgreSQL:</b> Banco de dados relacional ativo localmente (porta padrão <code>5432</code>).</li>
  <li><b>IDE:</b> IntelliJ IDEA, Eclipse ou VS Code com suporte a Spring Boot.</li>
</ul>

<p>1. Clone o repositório:</p>

```sh
git clone [https://github.com/SEU-USUARIO/api-acervo-alexandria.git](https://github.com/SEU-USUARIO/api-acervo-alexandria.git)
```

<p>2. Crie o banco de dados no PostgreSQL e configure as credenciais no arquivo <code>src/main/resources/application.properties</code>.</p>

<p>3. Execute a aplicação:</p>

```sh
mvn spring-boot:run
```

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Guia de Endpoints

<div align="center">
  
| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :---: | :--- |
| POST | `/usuarios` | Público | Registra um novo cliente validando unicidade de e-mail e CPF. |
| POST | `/login` | Público | Autentica o usuário e retorna o Token JWT. |
| POST | `/login/redefinir-senha` | Público | Atualiza a senha do usuário com novo hash BCrypt. |
| GET | `/livros` | Público | Lista o catálogo paginado de livros ativos. |
| GET | `/livros/{id}` | Público | Retorna os detalhes completos de um livro específico. |
| POST | `/livros` | `ADMIN` | Cadastra um novo livro no acervo. |
| PUT | `/livros/{id}` | `ADMIN` | Atualiza os dados de um livro existente. |
| DELETE | `/livros/{id}` | `ADMIN` | Realiza a inativação lógica (<i>soft delete</i>) de um livro. |
| PATCH | `/livros/{id}/reativar` | `ADMIN` | Reativa um livro previamente inativado. |
| GET / POST | `/carrinho` | Autenticado | Consulta ou adiciona itens ao carrinho do usuário logado. |
| POST | `/pedidos` | Autenticado | Converte o carrinho em pedido e abate o estoque. |
  
</div>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Testando a API na Prática (Swagger)

A API conta com documentação interativa integrada via **Swagger UI**, acessível em `http://localhost:8080/swagger-ui.html` com a aplicação em execução:

1. **Crie uma conta ou faça login:** Utilize o endpoint `POST /usuarios` para registrar um cliente e em seguida `POST /login` para obter o Token JWT.
2. **Autorize a sessão:** Clique no botão **Authorize** no topo direito do Swagger UI e cole o token retornado.
3. **Teste as rotas protegidas:** Com o cadeado fechado, realize operações em `/carrinho`, `/pedidos` ou rotas administrativas de `/livros` (caso o usuário possua `ROLE_ADMIN`).

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Licença
<p>Distribuído sob a licença MIT. Veja <code>LICENSE.txt</code> para mais informações.</p>

<br>

## Agradecimentos

<p>
  Agradecemos ao corpo docente da Universidade Anhembi Morumbi (UAM) pela mentoria acadêmica durante todo o primeiro semestre de 2026. Expressamos nossa gratidão especial aos professores que nos guiaram e impulsionaram o desenvolvimento prático desta solução focada em resolver problemas reais de uma sociedade conectada:

* **Prof. Paulo Cesar Lourenção:** Unidade Curricular de Modelos, métodos e técnicas da engenharia de software.
* **Prof. Francisco José Rodrigues da Silva Júnior:** Unidade Curricular de Modelos, métodos e técnicas da engenharia de software.
</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Contato

<table align="center">
  <tr>
    <td align="center" valign="top">
      <a href="https://github.com/MachadoCodes">
        <img src="https://avatars.githubusercontent.com/u/142549072" width="200px;" alt="Renato Gonçalves Machado"/>
      </a>
      <br />
      <p><b>Renato Gonçalves Machado</b></p>
      <p><a href="">LinkedIn</a> | <a href="https://github.com/MachadoCodes">GitHub</a></p>
    </td>
    <td align="center" valign="top">
      <a href="https://github.com/MiguelRebequi">
        <img src="https://avatars.githubusercontent.com/u/130229587" width="200px;" alt="Miguel Martinho Rebequi"/>
      </a>
      <br />
      <p><b>Miguel Martinho Rebequi</b></p>
      <p><a href="https://www.linkedin.com/in/miguel-martinho-rebequi-08753b271/">LinkedIn</a> | <a href="https://github.com/MiguelRebequi">GitHub</a></p>
    </td>
    <td align="center" valign="top">
      <a href="https://github.com/GuilhermeChiuchi">
        <img src="https://avatars.githubusercontent.com/u/130666846" width="200px;" alt="Guilherme Chiuchi Pereira"/>
      </a>
      <br />
      <p><b>Marcus Vinicius Correia</b></p>
      <p><a href="">LinkedIn</a> | <a href="https://https://github.com/mvcorreia">GitHub</a></p>
    </td>
  </tr>
</table>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>
