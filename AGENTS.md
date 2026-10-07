# AGENTS.md — Scan App

Guia para agentes de IA que trabalham neste repositório. Leia este arquivo inteiro antes de escrever ou alterar código.

## 1. O que é o projeto

**Scan App** é um aplicativo **exclusivamente Android**, distribuído pela **Google Play**, que transforma uma foto de documento em um arquivo **PDF**.

Fluxo principal (a razão de existir do app):

1. O usuário abre o app e toca em **Escanear**.
2. A câmera abre dentro do fluxo do app (ML Kit Document Scanner).
3. As bordas do documento são detectadas e recortadas automaticamente; o usuário pode ajustar os cantos.
4. Ao confirmar, um **PDF** é gerado e salvo no armazenamento interno do app.
5. O usuário pode **compartilhar** (WhatsApp, Telegram, e-mail etc.), **salvar** no aparelho, **renomear** ou **excluir** o documento.

Objetivo de produto: chegar do toque em "Escanear" ao PDF compartilhado com o mínimo de toques possível.

## 2. Decisões fixas (não mudar sem pedido explícito do usuário)

| Tema | Decisão |
| --- | --- |
| Plataforma | Somente Android. Nada de iOS, Flutter, React Native ou KMP |
| Linguagem | Kotlin (sem Java em código novo) |
| UI | Jetpack Compose + Material 3. Sem XML de layout, sem Fragments |
| Back-end | **Não existe servidor.** Tudo roda no aparelho, 100% offline |
| Escaneamento | ML Kit Document Scanner (`play-services-mlkit-document-scanner`) |
| Monetização | App gratuito, **sem anúncios**, sem compras no app |
| Coleta de dados | Nenhuma. Sem analytics, sem Firebase, sem SDKs de rastreamento |
| Idioma | Português do Brasil (pt-BR); todos os textos em `strings.xml` |

Consequências práticas:

- **Não adicione** dependências que façam chamadas de rede, coletem dados ou exijam login.
- **Não peça** permissões de câmera (`CAMERA`) nem de armazenamento (`READ/WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`). O ML Kit roda a câmera dentro do Google Play services, e o salvamento externo usa o Storage Access Framework.
- A permissão `INTERNET` não deve ser declarada pelo app. (O download do módulo do scanner é feito pelo Google Play services, não pelo app.)
- Monitoramento de falhas = Android Vitals do Play Console. Não adicione Crashlytics ou similares.

## 3. Escopo

### v1 (atual)
- Escanear **uma página** por documento, com recorte automático e ajuste manual.
- Gerar PDF, listar documentos, compartilhar, salvar no aparelho, abrir em outro app, renomear, excluir.
- Tela de Configurações: tema (sistema/claro/escuro), padrão de nome de arquivo, política de privacidade, versão.

### Fora da v1 (não implementar sem pedido)
- Várias páginas por PDF (v1.1)
- Filtros de imagem (v1.2)
- Importar da galeria (v1.3)
- OCR, PDF pesquisável, juntar/dividir PDFs, senha (v2.0)

Ao escrever código, deixe essas evoluções fáceis (por exemplo, o limite de páginas vem de uma constante), mas não as implemente antecipadamente.

## 4. Stack técnica

| Peça | Tecnologia |
| --- | --- |
| Linguagem | Kotlin 2.x |
| UI | Jetpack Compose, Material 3, cores dinâmicas (Android 12+) |
| Navegação | Navigation Compose |
| Estado | ViewModel + `StateFlow`, Kotlin Coroutines |
| Injeção de dependência | Hilt |
| Banco local | Room (SQLite) |
| Preferências | DataStore (Preferences) |
| Scanner | ML Kit Document Scanner (`GmsDocumentScanning`) |
| Miniaturas | `PdfRenderer` (Android) + Coil |
| Compartilhamento | `FileProvider` + `Intent.ACTION_SEND` |
| Salvar externamente | Storage Access Framework (`ActivityResultContracts.CreateDocument`) |
| Splash | SplashScreen API (androidx.core) |
| Build | Gradle Kotlin DSL + catálogo de versões (`gradle/libs.versions.toml`) |
| Testes | JUnit, MockK, kotlinx-coroutines-test, Compose UI Test |

Configuração de SDK:

- `minSdk = 24`
- `targetSdk = 36` e `compileSdk = 36` (exigência da Google Play para apps novos e atualizações a partir de 31/08/2026)
- Revisar o `targetSdk` uma vez por ano, quando a Google Play elevar o requisito.

Toda dependência nova entra pelo `libs.versions.toml`, nunca com versão escrita direto no `build.gradle.kts`.

## 5. Arquitetura

MVVM com Clean Architecture simplificada, em **um único módulo** (`app`).

```
UI (Compose + ViewModel)
        │
        ▼
Domínio (casos de uso, Kotlin puro)
        │
        ▼
Dados (DocumentRepository)
   ├── Room (metadados)
   ├── Arquivos PDF (filesDir/documentos)
   └── ML Kit Scanner (Google Play services)
```

Regras:

- **A UI nunca acessa** Room, arquivos ou o scanner diretamente. Tudo passa por ViewModel → caso de uso → repositório.
- A camada `domain` é **Kotlin puro**: sem imports de `android.*` ou `androidx.*`.
- ViewModels expõem um único `StateFlow<UiState>` por tela e recebem ações por funções públicas (`onScanClick()`, `onRename(...)` etc.). Eventos únicos (abrir intent de compartilhar, mostrar snackbar) saem por `Channel`/`SharedFlow`.
- Operações de disco e banco rodam em `Dispatchers.IO`, injetado (não hardcoded) para facilitar testes.
- Erros são representados com `Result` ou `sealed class`, nunca engolidos em silêncio.

### Estrutura de pastas

```
app/src/main/java/br/com/seuapp/scanner/
├── ScannerApp.kt          Application + @HiltAndroidApp
├── MainActivity.kt        ponto de entrada, tema e NavHost
├── ui/
│   ├── theme/             cores, tipografia, tema claro/escuro
│   ├── navigation/        rotas entre telas
│   ├── home/              HomeScreen + HomeViewModel
│   ├── document/          DocumentScreen + DocumentViewModel
│   ├── settings/          SettingsScreen + SettingsViewModel
│   └── components/        componentes Compose reutilizáveis
├── domain/
│   ├── model/             Document
│   └── usecase/           SaveScan, ShareDocument, ExportDocument, RenameDocument, DeleteDocument
├── data/
│   ├── local/             Room: DocumentEntity, DocumentDao, AppDatabase
│   ├── files/             PdfFileStorage: copiar, renomear, apagar PDFs
│   ├── prefs/             DataStore: tema, padrão de nome
│   └── repository/        DocumentRepositoryImpl
├── scanner/               DocumentScannerLauncher: configura e abre o ML Kit
└── di/                    módulos Hilt
```

> O pacote `br.com.seuapp.scanner` é provisório. Se o `applicationId` definitivo já estiver no `build.gradle.kts`, ele prevalece.

## 6. Telas

| Tela | Conteúdo | Ações |
| --- | --- | --- |
| Início (`home`) | Lista de PDFs (miniatura, nome, data, tamanho), mais recentes primeiro; estado vazio na primeira vez; Extended FAB "Escanear" | Escanear, abrir documento, menu rápido (compartilhar, renomear, excluir) |
| Câmera e recorte | Fornecida pelo ML Kit (não personalizável) | Fotografar, ajustar cantos, confirmar ou cancelar |
| Documento (`document`) | Prévia da página, nome editável, tamanho | Compartilhar (em destaque), Salvar no aparelho, Abrir em outro app, Excluir |
| Configurações (`settings`) | Tema, padrão de nome, política de privacidade, versão | Alterar preferências |

Comportamento esperado:

- Cancelar na câmera ou no recorte volta à tela inicial **sem criar nada**.
- Ao confirmar o escaneamento, o PDF é salvo e a tela **Documento** abre direto.
- Excluir sempre pede confirmação em diálogo.

## 7. Detalhes de implementação importantes

### Scanner (ML Kit)

Configuração da v1:

```kotlin
GmsDocumentScannerOptions.Builder()
    .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE)
    .setPageLimit(MAX_PAGES) // MAX_PAGES = 1 na v1
    .setGalleryImportAllowed(false)
    .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
    .build()
```

- Iniciar com `GmsDocumentScanning.getClient(options).getStartScanIntent(activity)` + `ActivityResultContracts.StartIntentSenderForResult`.
- No primeiro uso o Google Play services pode precisar baixar o módulo do scanner. Trate a falha mostrando uma mensagem clara ("Conecte-se à internet uma vez para ativar o scanner") em vez de travar.
- Toda a configuração do scanner fica em `scanner/DocumentScannerLauncher`.

### Armazenamento

- O PDF retornado pelo ML Kit é **copiado** para `context.filesDir/documentos/`.
- Nome padrão: `Documento_AAAA-MM-DD_HHMM.pdf` (configurável nas preferências). Evite colisões acrescentando sufixo (`_2`, `_3`).
- Cada PDF tem um registro no Room: `id`, `nome`, `caminho`, `tamanhoBytes`, `criadoEm`, `paginas`.
- Arquivo e registro devem ficar consistentes: se um falhar, desfaça o outro.

### Compartilhar e salvar

- Compartilhar: `FileProvider` (authority `${applicationId}.fileprovider`, apontando só para `documentos/`), `Intent.ACTION_SEND`, tipo `application/pdf`, `FLAG_GRANT_READ_URI_PERMISSION`, sempre via `Intent.createChooser`.
- **Não** integre SDKs de WhatsApp, Telegram ou outras redes: o menu de compartilhamento do Android cobre todos.
- Salvar no aparelho: `CreateDocument("application/pdf")` e cópia por `ContentResolver.openOutputStream`. O original permanece no app.
- Abrir em outro app: `Intent.ACTION_VIEW` com URI do `FileProvider`.

## 8. Design

- Material 3 com cores dinâmicas no Android 12+; paleta própria nas versões anteriores (primária azul-petróleo).
- Tema claro/escuro seguindo o sistema, com opção manual.
- Fonte padrão do sistema; não adicione fontes customizadas.
- Áreas de toque de no mínimo 48 dp, `contentDescription` em todo ícone clicável, contraste adequado (TalkBack).
- Nenhum texto fixo no código Compose: use `stringResource`.
- O nome, ícone e textos do app **não podem** imitar o CamScanner ou outra marca.

## 9. Padrões de código

- Siga as convenções oficiais de Kotlin e Compose (funções `@Composable` em PascalCase, `Modifier` como primeiro parâmetro opcional).
- Composables de tela recebem estado e lambdas; não recebem ViewModel diretamente (exceto o composable de rota). Isso permite `@Preview` e testes.
- Inclua `@Preview` (claro e escuro) para componentes e telas novas.
- Nomes de classes, funções e variáveis em **inglês**; textos ao usuário e comentários de documentação podem ser em português.
- Sem `!!` em código de produção; trate nulos explicitamente.
- Sem `GlobalScope`; use `viewModelScope` ou escopos injetados.
- Mantenha funções pequenas e cada arquivo com uma responsabilidade clara.

## 10. Testes

- Casos de uso e repositório: testes unitários com JUnit + MockK + `kotlinx-coroutines-test`.
- ViewModels: testar transições de `UiState` (ex.: lista vazia → com documentos, erro ao renomear).
- Room: testes com banco em memória.
- UI: Compose UI Test para fluxos principais da tela inicial e da tela de documento.
- O ML Kit não é testável em unitários: isole-o atrás de uma interface (`DocumentScanner`) e use um fake nos testes.

## 11. Comandos

```bash
./gradlew assembleDebug          # build de debug
./gradlew test                   # testes unitários
./gradlew connectedAndroidTest   # testes instrumentados (precisa de emulador/aparelho)
./gradlew lint                   # Android Lint
./gradlew bundleRelease          # AAB para a Google Play
```

Antes de considerar uma tarefa concluída: o projeto compila, `test` e `lint` passam, e nenhuma permissão ou dependência proibida foi adicionada.

## 12. Publicação (contexto)

- Formato de envio: **AAB** com Play App Signing.
- Formulário de Segurança dos Dados: "nenhum dado coletado ou compartilhado". Qualquer mudança que colete dados quebra essa declaração e precisa ser aprovada pelo usuário.
- Conta pessoal nova exige teste fechado com pelo menos 12 testadores por 14 dias seguidos antes da produção.
- Nunca commite keystore, senhas de assinatura ou `local.properties`.

## 13. Quando estiver em dúvida

- Prefira a solução mais simples que respeite as decisões fixas da seção 2.
- Se uma tarefa exigir rede, nova permissão, coleta de dados, back-end ou mudança de stack, **pare e pergunte ao usuário** antes de implementar.
