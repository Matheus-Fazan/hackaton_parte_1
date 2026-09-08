# Setup do Codex Desktop para Android Java + XML

Este guia configura o Codex Desktop para trabalhar no projeto Android usando **Java + XML Views**.

> Objetivo: dar ao Codex boas instruções de Android clássico, acesso ao Android CLI/ADB/Gradle e integração com GitHub, sem empurrar Kotlin ou Jetpack Compose para o projeto.

---

## 1. Stack obrigatória do projeto

O projeto deve permanecer em:

- Java
- XML Views
- AndroidX
- Material Components / Material 3
- ViewBinding
- ConstraintLayout
- RecyclerView quando necessário
- Retrofit + OkHttp para APIs
- Room quando persistência local for necessária
- Gradle Wrapper (`gradlew.bat`)

Evitar, salvo pedido explícito:

- Kotlin
- Jetpack Compose
- Kotlin Coroutines
- Compose Navigation
- migração automática de Java/XML para Compose

---

## 2. Pré-requisitos

Abra o **CMD** e valide:

```cmd
node --version
npx --version
git --version
java --version
```

Também é recomendado ter o Android Studio/Android SDK instalado.

Quando o SDK estiver configurado, valide:

```cmd
adb --version
```

O Gradle global não é obrigatório. Dentro do projeto, o Codex deve usar:

```cmd
gradlew.bat
```

---

# 3. Instalar Skills Android comunitárias

Antes de instalar, liste as skills disponíveis no repositório:

```cmd
npx skills add krutikJain/android-agent-skills --list
```

Instale globalmente para o **Codex** as skills úteis para Java + XML:

```cmd
npx skills add krutikJain/android-agent-skills --skill android-viewsystem-foundations --skill android-mobile-frontend-design --skill android-material3-design-system --skill android-gradle-build-logic --skill android-navigation-deeplinks --skill android-permissions-activity-results --skill android-ui-states-validation --skill android-networking-retrofit-okhttp --skill android-room-database --skill android-emulator-automation --skill android-security-best-practices --skill android-testing-unit --skill android-testing-ui -g -a codex -y
```

Principais:

- `android-viewsystem-foundations`: XML, ConstraintLayout, Fragments, ViewBinding, DataBinding e lifecycle clássico.
- `android-mobile-frontend-design`: qualidade e consistência de UI mobile.
- `android-material3-design-system`: Material 3 e design tokens.
- `android-gradle-build-logic`: Gradle e configuração de build.
- `android-networking-retrofit-okhttp`: APIs REST.
- `android-room-database`: persistência local.
- `android-emulator-automation`: automação de emulator/ADB.
- `android-testing-unit`: testes unitários.
- `android-testing-ui`: testes de UI.
- `android-security-best-practices`: boas práticas de segurança Android.

Não instalar skills focadas em Kotlin/Compose para este projeto, como:

- `android-kotlin-core`
- `android-coroutines-flow`
- `android-compose-foundations`
- `android-compose-state-effects`
- `android-compose-performance`
- `android-compose-accessibility`
- `android-coil-compose`
- `android-rxjava-to-coroutines-migration`

---

# 4. Instalar Skills extras de diagnóstico

Primeiro confira o catálogo:

```cmd
npx skills add rasy007/android-skills --list
```

Depois instale:

```cmd
npx skills add rasy007/android-skills --skill android-adb-toolkit --skill android-gradle-doctor --skill android-crash-analyzer --skill android-lib-lookup -g -a codex -y
```

Funções:

- `android-adb-toolkit`: ADB, logcat, screenshots e debugging.
- `android-gradle-doctor`: diagnosticar problemas de Gradle.
- `android-crash-analyzer`: analisar crashes e ANRs.
- `android-lib-lookup`: consultar classes/APIs de dependências Android.

---

# 5. Instalar o Android CLI oficial

No CMD:

```cmd
curl.exe -fsSL https://dl.google.com/android/cli/latest/windows_x86_64/install.cmd -o "%TEMP%\android-cli-install.cmd" && "%TEMP%\android-cli-install.cmd"
```

Feche o CMD e abra outro.

Teste:

```cmd
where android
android --version
```

---

## 5.1. Se `android` não aparecer no PATH

Confira se o executável existe:

```cmd
dir "%USERPROFILE%\AppData\AndroidCLI"
```

Teste diretamente:

```cmd
"%USERPROFILE%\AppData\AndroidCLI\android.exe" --version
```

Se funcionar, adicione ao PATH da sessão atual:

```cmd
set "PATH=%PATH%;%USERPROFILE%\AppData\AndroidCLI"
```

Teste novamente:

```cmd
android --version
```

Para adicionar permanentemente ao **PATH do usuário**:

```cmd
powershell.exe -NoProfile -Command "$d=$env:USERPROFILE+'\AppData\AndroidCLI'; $p=[Environment]::GetEnvironmentVariable('Path','User'); if (($p -split ';') -notcontains $d) { [Environment]::SetEnvironmentVariable('Path',($p.TrimEnd(';')+';'+$d),'User') }"
```

Depois:

1. faça logoff/login no Windows, ou reinicie a sessão;
2. abra um CMD novo;
3. rode:

```cmd
where android
android --version
```

Não é necessário colocar o Android CLI no PATH global da máquina. O PATH do usuário é suficiente para o Codex Desktop iniciado pela mesma conta.

---

# 6. Instalar a Skill oficial `android-cli`

Confira primeiro a sintaxe disponível na versão instalada:

```cmd
android skills add --help
```

Nas versões em que o ID da skill é um argumento posicional:

```cmd
android skills add android-cli --agent=codex
```

Depois valide:

```cmd
android skills list
```

Se uma versão futura do Android CLI mudar a sintaxe, siga exatamente o `android skills add --help`.

---

# 7. Conferir as Skills instaladas

Skills instaladas pelo `npx skills`:

```cmd
npx skills list -g -a codex
```

Confira também os diretórios comuns:

```cmd
dir "%USERPROFILE%\.agents\skills"
dir "%USERPROFILE%\.codex\skills"
```

É normal que algumas skills instaladas pelo ecossistema Agent Skills fiquem em:

```text
%USERPROFILE%\.agents\skills
```

e outras específicas do Codex possam aparecer em:

```text
%USERPROFILE%\.codex\skills
```

O importante é o Codex conseguir descobri-las.

---

# 8. Plugins recomendados no Codex Desktop

Plugins são instalados pelo **Diretório de Plugins** do Codex.

## GitHub — recomendado

No Codex Desktop:

1. Abra **Plugins**.
2. Pesquise por **GitHub**.
3. Abra o plugin.
4. Clique em **Install plugin / Instalar plugin**.
5. Se solicitado, clique em **Connect / Conectar**.
6. Autorize a conta GitHub que contém o repositório do projeto.

Isso permite ao Codex trabalhar melhor com:

- repositórios;
- issues;
- pull requests;
- histórico;
- CI/GitHub Actions;
- revisão de mudanças.

## Figma — opcional

Instale se o projeto usar Figma como fonte de verdade das telas.

No Codex Desktop:

1. Abra **Plugins**.
2. Pesquise por **Figma**.
3. Instale.
4. Conecte a conta quando solicitado.

Use principalmente para transformar design em:

- `activity_*.xml`
- `fragment_*.xml`
- `item_*.xml`
- recursos de `drawable`
- cores
- dimensões
- tipografia
- componentes Material

Não é necessário se a equipe não estiver usando Figma.

---

# 9. Reiniciar o Codex Desktop

Depois de instalar skills, plugins ou alterar o PATH:

1. feche completamente o Codex Desktop;
2. certifique-se de que o processo foi encerrado;
3. abra novamente.

Depois peça para o Codex executar:

```cmd
where android
android --version
where adb
adb --version
java --version
git --version
```

Dentro do repositório Android:

```cmd
gradlew.bat --version
```

---

# 10. Teste final do ambiente

Peça ao Codex:

> Verifique meu ambiente Android. Não altere nenhum arquivo. Confirme que consegue encontrar Java, ADB, Android CLI e o Gradle Wrapper. Liste também as skills Android disponíveis para você. Este projeto usa Java + XML Views e não deve ser migrado para Kotlin ou Jetpack Compose.

O Codex deve conseguir executar pelo menos:

```cmd
android --version
adb --version
java --version
gradlew.bat --version
```

---

# 11. Regras recomendadas para o `AGENTS.md`

Na raiz do projeto, mantenha algo equivalente a:

```md
# Android Project Rules

This is a native Android application using Java + XML Views.

## Required stack

- Java
- XML Views
- AndroidX
- Material Components
- ViewBinding
- ConstraintLayout
- RecyclerView where appropriate

## Do not introduce unless explicitly requested

- Kotlin
- Jetpack Compose
- Kotlin Coroutines
- Compose Navigation

Do not migrate existing Java/XML code to Kotlin or Compose.

## Architecture

Keep architecture simple and readable.

Prefer:

- MVVM when it improves separation of concerns
- ViewModel for presentation state
- Repository for data access
- Retrofit + OkHttp for HTTP APIs
- Room for local persistence when required

Avoid unnecessary layers and abstractions.

## Verification

Before considering a task complete:

1. Run `gradlew.bat assembleDebug`
2. Run `gradlew.bat test`
3. Run `gradlew.bat lint`
4. Fix errors introduced by the change
5. If an emulator/device is available, install and launch the app
6. Inspect logcat for crashes related to the change
7. Smoke-test the affected screen
```

---

# 12. Resumo do setup

```text
Codex Desktop
|
+-- Skills Android
|   +-- android-cli (Google)
|   +-- android-viewsystem-foundations
|   +-- android-mobile-frontend-design
|   +-- android-material3-design-system
|   +-- android-gradle-build-logic
|   +-- android-navigation-deeplinks
|   +-- android-permissions-activity-results
|   +-- android-ui-states-validation
|   +-- android-networking-retrofit-okhttp
|   +-- android-room-database
|   +-- android-emulator-automation
|   +-- android-security-best-practices
|   +-- android-testing-unit
|   +-- android-testing-ui
|   +-- android-adb-toolkit
|   +-- android-gradle-doctor
|   +-- android-crash-analyzer
|   +-- android-lib-lookup
|
+-- Ferramentas locais
|   +-- Android CLI
|   +-- Android SDK
|   +-- ADB
|   +-- JDK
|   +-- Gradle Wrapper
|
+-- Plugins
    +-- GitHub (recomendado)
    +-- Figma (opcional)
```

---

# Referências

- Android Skills oficial do Google: https://github.com/android/skills
- Android CLI skill: https://github.com/android/skills/blob/main/devtools/android-cli/SKILL.md
- Android Agent Skills (XML/Views etc.): https://github.com/krutikJain/android-agent-skills
- Android debugging skills: https://github.com/rasy007/android-skills
- Agent Skills CLI (`npx skills`): https://github.com/vercel-labs/skills
- Plugins no ChatGPT e Codex: https://help.openai.com/en/articles/20001256
