# Roadmap de desenvolvimento — Scan App (v1)

Este documento divide a v1 em **módulos de desenvolvimento** (etapas), na ordem em que devem ser executados.
Ele complementa o `AGENTS.md` (regras e decisões fixas) e o plano do produto. "Módulo" aqui é uma **etapa de trabalho**,
não um módulo Gradle: o projeto continua com um único módulo `app`, como manda a arquitetura.

## Prioridades

| Prioridade | Significado |
| --- | --- |
| **P0** | Caminho crítico da v1: sem isso o app não cumpre o fluxo Escanear → PDF → Compartilhar |
| **P1** | Necessário para publicar na Google Play, mas não bloqueia o fluxo principal |
| **P2/P3** | Pós-v1 (backlog). Não implementar sem pedido explícito |

## Visão geral

| # | Módulo | Prioridade | Depende de | Estimativa | Status |
| --- | --- | --- | --- | --- | --- |
| M0 | Fundação: higiene do repositório, Hilt/KSP, tema e identidade | P0 | — | 1 dia | ✅ Concluído |
| M1 | Base de UI: navegação, telas e componentes | P0 | M0 | 2–3 dias | ✅ Concluído |
| M2 | Domínio e dados locais (Room, arquivos, DataStore, Hilt) | P0 | M0 | 4–5 dias | 🔶 Entregue, falta validar o build |
| M3 | Scanner (ML Kit) e fluxo "salvar escaneamento" | P0 | M2 | 3–4 dias | ⏳ Próximo |
| M4 | Tela Documento: prévia, compartilhar, salvar, abrir, renomear, excluir | P0 | M1, M3 | 4–5 dias | ⬜ |
| M5 | Configurações: tema, padrão de nome, privacidade, versão | P1 | M1, M2 | 2 dias | ⬜ |
| M6 | Qualidade: acessibilidade, testes, lint, R8, desempenho | P1 | M4, M5 | 3 dias | ⬜ |
| M7 | Release e Google Play | P1 | M6 | 3 semanas (teste fechado) | ⬜ |
| — | Backlog pós-v1 (v1.1 → v2.0) | P2/P3 | v1 publicada | — | ⬜ |

As estimativas consideram uma pessoa em tempo integral e somam ~4 semanas de código (M0–M6), em linha com o plano.
M1 e M2 podem andar em paralelo; M7 não pode ser acelerado (exigências da Google Play), então **recrute os testadores
durante o M4**.

---

## M0 — Fundação do projeto · P0 · ✅

**Objetivo:** partir do projeto gerado pelo Android Studio (já em Gradle) e deixá-lo limpo, com a identidade do app
e a infraestrutura que todos os outros módulos usam, seguindo o `AGENTS.md`.

Já feito no commit `refact: refact project`:

- [x] Projeto Android com Gradle Kotlin DSL, catálogo de versões, Gradle Wrapper 9.6.0 e AGP 9.4.1
- [x] Módulo `app` com Compose + Material 3, `compileSdk 37`, `minSdk 24`

Entregue neste módulo:

- [x] Higiene do repositório: remover o `pom.xml`; tirar do Git `.gradle/`, `local.properties` e os arquivos da IDE (`.idea/`, inclusive o `misc.xml` que ainda apontava para o Maven); `.gitignore` para Android
- [x] Kotlin 2.4.21 (no lugar do 2.2.10 embutido no AGP), KSP 2.3.12 e Hilt 2.60.1 no catálogo
- [x] `targetSdk 36` (era 37 no modelo) e `versionName 0.1.0`
- [x] `ScannerApp` (`@HiltAndroidApp`) e `MainActivity` (`@AndroidEntryPoint`)
- [x] `ScannerTheme`: cores dinâmicas no Android 12+ e paleta azul-petróleo própria (claro/escuro); tema roxo do modelo removido
- [x] Tela inicial provisória: estado vazio + Extended FAB "Escanear" (ainda sem ação), com `contentDescription` e previews claro/escuro
- [x] Textos em pt-BR no `strings.xml`; nome provisório "Scan App"
- [x] Manifesto **sem nenhuma permissão**; `allowBackup=false` (regras de backup de exemplo removidas)
- [x] Testes de exemplo trocados por testes de UI da tela inicial; MockK e coroutines-test prontos para os unitários

Fica para depois:

- Splash screen (SplashScreen API) → M1
- Ícone próprio (hoje é o robô padrão do Android Studio) → M7

**Critério de aceite:** `./gradlew assembleDebug test lint connectedAndroidTest` passa e o app abre no emulador
mostrando o estado vazio.

> O `.gitignore` novo ficou de fora dos commits do M0, e por isso `.gradle/`, `.idea/` e `local.properties` voltaram ao
> repositório; o M1 corrige isso (commit `chore`).

---

## M1 — Base de UI: navegação, telas e componentes · P0 · ✅

**Objetivo:** estrutura de telas pronta para receber os dados, sem lógica de negócio.

- [x] `navigation-compose` com rotas tipadas (`@Serializable`) em `ui/navigation/`: `HomeDestination`, `DocumentDestination(documentId)`, `SettingsDestination`
- [x] `ScannerNavHost` chamado pela `MainActivity`
- [x] Splash screen com a SplashScreen API (fundo azul-petróleo; ícone ainda é o provisório)
- [x] `HomeScreen` com `TopAppBar` (botão Configurações) e lista de documentos recebendo `HomeUiState`; estado vazio quando a lista está vazia
- [x] `HomeViewModel` com `StateFlow<HomeUiState>`, usando os documentos de exemplo (`ui/sample/SampleDocuments`) até o M2
- [x] `DocumentListItem` em `ui/components/` (miniatura provisória, nome, data e tamanho no formato do aparelho) e `BackTopAppBar` para as telas internas
- [x] `DocumentScreen` + `DocumentViewModel` (lê o `documentId` da rota) e `SettingsScreen`, como esqueletos navegáveis
- [x] `@Preview` claro/escuro em toda tela e componente novo
- [x] Testes unitários: `HomeViewModel` (mais recentes primeiro) e `DocumentViewModel` (id conhecido, desconhecido, ausente)
- [x] Testes de UI: tela inicial (vazia e com lista, cliques) e navegação ponta a ponta com a `MainActivity`

Ajustes de escopo em relação ao plano inicial:

- O menu rápido da lista (compartilhar, renomear, excluir) foi para o **M4**, junto com as ações que ele dispara, para não
  existir menu sem função.
- Eventos únicos por `Channel` entram quando houver o primeiro evento real (mensagem de erro do scanner no **M3**).

**Critério de aceite:** `./gradlew assembleDebug test lint connectedAndroidTest` passa; no app dá para navegar
Início → Documento → voltar e Início → Configurações → voltar.

> Os testes unitários do M1 (`app/src/test`) e este roadmap ficaram fora dos commits; voltaram no M2.

---

## M2 — Domínio e dados locais · P0 · 🔶

**Objetivo:** guardar e listar documentos com arquivo e registro sempre consistentes.

Domínio (`domain/`, Kotlin puro, sem `android.*`/`androidx.*`):

- [x] Modelos: `Document` (id, nome, caminho, tamanho, criado em, páginas), `ScannedPdf`, `AppSettings` + `ThemeMode`
- [x] `DocumentError` (`NotFound`, `InvalidName`, `Storage`), entregue dentro de `Result`
- [x] `DocumentNaming`: nome padrão `<prefixo>_AAAA-MM-DD_HHMM`, saneamento (caracteres proibidos viram `_`, até 100 caracteres) e sufixo `_2`, `_3` em colisões
- [x] Interfaces em `domain/repository/`: `DocumentRepository`, `SettingsRepository`, `Clock`
- [x] Casos de uso: `ObserveDocuments`, `ObserveDocument`, `SaveScan`, `RenameDocument` (saneia e valida o nome), `DeleteDocument`

Dados (`data/`):

- [x] Room: `DocumentEntity` (caminho único), `DocumentDao` (mais recentes primeiro, como `Flow`), `AppDatabase` v1; schema exportado em `app/schemas/` pelo argumento `room.schemaLocation` do KSP
- [x] `LocalPdfFileStorage` em `filesDir/documentos/`: importar de um URI, renomear, apagar; nunca deixa cópia pela metade
- [x] `DocumentRepositoryImpl`: desfaz o primeiro passo quando o segundo falha (apaga a cópia se o insert falhar, desfaz o renomear do arquivo, devolve o registro se o arquivo não puder ser apagado); escritas serializadas por `Mutex`
- [x] `DataStoreSettingsRepository`: tema e prefixo do nome dos arquivos (o prefixo substitui o "padrão de nome" do plano)
- [x] Hilt (`di/`): `DatabaseModule`, `StorageModule` (pasta de documentos, `ContentResolver`, DataStore), `DataModule` (`@Binds`), `DispatchersModule` (`@IoDispatcher`)

UI:

- [x] `HomeViewModel` e `DocumentViewModel` leem o repositório pelos casos de uso, com estado de carregamento para não piscar o estado vazio; `SampleDocuments` ficou só para previews e testes

Testes:

- [x] Unitários: `DocumentNaming`, `RenameDocument`, `DocumentRepositoryImpl` (sucesso e cada desfazer), `DataStoreSettingsRepository`, `HomeViewModel`, `DocumentViewModel` (com `FakeDocumentRepository` e `MainDispatcherRule`)
- [x] Instrumentados: `DocumentDao` (banco em memória), `LocalPdfFileStorage` (arquivos reais), navegação com Hilt (`HiltTestRunner` + `TestDatabaseModule` em memória)

Fica para depois: o app ainda não cria documentos sozinho (isso é o escaneamento do M3), então a tela inicial mostra o
estado vazio até lá. O tema escolhido só passa a valer quando a tela de Configurações existir (M5).

**Critério de aceite (pendente de validação local):** `./gradlew assembleDebug test lint connectedAndroidTest` passa;
documentos inseridos pelo teste de navegação aparecem na lista e abrem na tela Documento; renomear e excluir mantêm
arquivo e banco juntos (testes do repositório). Quando passar, marque o M2 como ✅.

---

## M3 — Scanner ML Kit e fluxo de escaneamento · P0

**Objetivo:** tocar em "Escanear", fotografar, recortar e ter o PDF salvo.

- [ ] Interface `DocumentScanner` + implementação `scanner/DocumentScannerLauncher` com `GmsDocumentScanning`
- [ ] Opções da v1: `SCANNER_MODE_BASE`, `setPageLimit(MAX_PAGES)` com `MAX_PAGES = 1`, galeria desativada, saída só PDF
- [ ] `StartIntentSenderForResult` na rota da Home; cancelamento volta à Home **sem criar nada**
- [ ] Ao confirmar: `SaveScan` copia o PDF e cria o registro; navega direto para a tela Documento
- [ ] Primeiro uso sem o módulo do scanner baixado: mensagem clara ("Conecte-se à internet uma vez para ativar o scanner"), emitida como evento único (`Channel`) do `HomeViewModel`
- [ ] **Verificar o manifesto mesclado** (`app/build/intermediates/merged_manifest`): se o ML Kit trouxer `INTERNET` ou outra permissão, remover com `tools:node="remove"` e validar que o scanner continua funcionando
- [ ] Testes com um `FakeDocumentScanner` (sucesso, cancelamento, falha)

**Critério de aceite:** em aparelho real, Escanear → foto → confirmar abre a tela Documento com o PDF salvo.

---

## M4 — Tela Documento e ações · P0

**Objetivo:** fechar o fluxo principal: compartilhar com no máximo dois toques após abrir o app.

- [ ] Prévia da página com `PdfRenderer` (fora da main thread) e miniaturas na Home com Coil
- [ ] `FileProvider` (authority `${applicationId}.fileprovider`, apenas `documentos/`)
- [ ] Compartilhar: `ACTION_SEND`, `application/pdf`, `FLAG_GRANT_READ_URI_PERMISSION`, sempre por `Intent.createChooser` (botão em destaque)
- [ ] Salvar no aparelho: `CreateDocument("application/pdf")` + cópia por `ContentResolver.openOutputStream`
- [ ] Abrir em outro app: `ACTION_VIEW` com URI do `FileProvider` (tratar "nenhum app encontrado")
- [ ] Menu rápido no item da lista da Home (compartilhar, renomear, excluir)
- [ ] Renomear (diálogo com validação) e excluir (sempre com diálogo de confirmação), na tela Documento e no menu rápido
- [ ] Mostrar o tamanho do arquivo antes de compartilhar
- [ ] Casos de uso `ShareDocument` e `ExportDocument`
- [ ] Testes: `DocumentViewModel` (renomear com erro, excluir) e Compose UI Test da tela Documento

**Critério de aceite:** um PDF escaneado chega ao WhatsApp/Gmail pelo menu do sistema e pode ser salvo em Downloads.

---

## M5 — Configurações · P1

- [ ] Tema: sistema / claro / escuro (DataStore → `ScannerTheme(darkTheme = …)`)
- [ ] Padrão de nome dos arquivos (com prévia do nome gerado)
- [ ] Link para a política de privacidade (abre no navegador; o app continua sem permissão `INTERNET`)
- [ ] Versão do app (`BuildConfig.VERSION_NAME`)
- [ ] Testes do `SettingsViewModel`

---

## M6 — Qualidade · P1

- [ ] Acessibilidade: toque ≥ 48 dp, `contentDescription` em todo ícone clicável, contraste, navegação com TalkBack
- [ ] Revisar que nenhum texto está fixo no código (tudo em `strings.xml`)
- [ ] Compose UI Test dos fluxos principais (Início e Documento)
- [ ] `./gradlew lint` sem avisos relevantes; R8 do release testado em aparelho real
- [ ] Testar Android 7 (API 24), Android 12+ (cores dinâmicas) e Android 16 (API 36)
- [ ] Tamanho dos PDFs aceitável para WhatsApp
- [ ] (Opcional) GitHub Actions rodando `./gradlew lint test assembleDebug` a cada push

---

## M7 — Release e Google Play · P1

Código:

- [ ] Assinatura de release lendo `keystore.properties` (fora do Git) e geração de **AAB** (`./gradlew bundleRelease`)
- [ ] Nome e ícone definitivos (originais, sem lembrar o CamScanner)
- [ ] `versionCode`/`versionName` da primeira versão pública

Play Console (checklist do plano):

- [ ] Conta de desenvolvedor (US$ 25) e verificação de identidade
- [ ] Play App Signing ativado
- [ ] Política de privacidade publicada ("nenhum dado é coletado")
- [ ] Formulário de Segurança dos Dados: nenhum dado coletado ou compartilhado
- [ ] Classificação indicativa e público-alvo
- [ ] Ficha da loja: ícone 512×512, destaque 1024×500, ≥ 2 capturas, descrições curta e longa
- [ ] Teste interno → teste fechado com 15–20 testadores por 14 dias seguidos → pedido de produção

---

## Backlog pós-v1 · P2/P3

| Versão | Item | Observação |
| --- | --- | --- |
| v1.1 | Várias páginas por PDF | Basta aumentar `MAX_PAGES` e ajustar a prévia |
| v1.2 | Filtros de imagem | Modo completo do ML Kit (`SCANNER_MODE_FULL`) |
| v1.3 | Importar da galeria | `setGalleryImportAllowed(true)` |
| v2.0 | OCR offline, PDF pesquisável, juntar/dividir, senha | ML Kit Text Recognition |

---

## Decisões pendentes (precisam do dono do projeto)

| Decisão | Situação atual | Por que importa |
| --- | --- | --- |
| `applicationId` definitivo | `br.com.tmvinicius.scanner_app` (escolhido ao gerar o projeto) | **Não pode mudar** depois da primeira publicação na Play |
| `targetSdk` | 36, como pede o `AGENTS.md` (o modelo do Android Studio gerou 37) | 37 também é aceito pela Play, mas exige testar as mudanças de comportamento do Android 17 |
| Nome do app | "Scan App" (provisório) | Precisa ser original para a ficha da loja |
| Backup automático | `allowBackup=false` (nada sai do aparelho) | Com `true`, os PDFs iriam para o backup do Google do usuário e sobreviveriam à troca de aparelho |
| Hospedagem da política de privacidade | Não definida | Obrigatória para publicar |
| Ícone definitivo | Robô padrão do Android Studio | Ficha da loja e identidade visual; não pode lembrar outras marcas |
