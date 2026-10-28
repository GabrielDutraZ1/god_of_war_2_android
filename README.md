 <div align="center">

# ⚔️ God of War II — Standalone Launcher (Android) ⚔️

### Experiência de Source Port Dedicado e Otimizado para PlayStation 2 no Android

[![Gambiarra Games](https://img.shields.io/badge/Projeto_por-Gambiarra_Games-red?style=for-the-badge&logo=playstation)](https://gambiarra.games)
[![Target Architecture](https://img.shields.io/badge/ABI-arm64--v8a-blue?style=for-the-badge)](https://developer.android.com/ndk)
[![Graphics Backend](https://img.shields.io/badge/API-Vulkan_1.1+-green?style=for-the-badge&logo=vulkan)](https://www.khronos.org/vulkan/)
[![License](https://img.shields.io/badge/License-GPL_LGPL-orange?style=for-the-badge)](LICENSE)

<br/>

**Desenvolvido por Gambiarra Games • [gambiarra.games](https://gambiarra.games)**

---

### 📦 [CLIQUE AQUI PARA BAIXAR O APK COMPILADO (arm64-v8a)](releases/GodOfWar2-Standalone-arm64-v8a.apk)

*(Tamanho: 10.89 MB • Versão Standalone Pronta para Instalar)*

---

</div>

## 📖 Visão Geral

Este projeto busca oferecer uma experiência dedicada para executar o clássico **God of War II** no Android. Inspirado na arquitetura e filosofia de projetos de standalone port, como Dusklight e God of War Collection I & II para PC, o objetivo é reduzir a necessidade de interfaces genéricas de emulador, seletores de jogos e menus intermediários.

A proposta do aplicativo é:

1. Iniciar o jogo God of War II diretamente, em tela cheia, quando a configuração estiver concluída.
2. Aplicar configurações de renderização e desempenho compatíveis com o hardware disponível.
3. Simplificar a inicialização e o uso dos controles, evitando configurações desnecessárias.

**Observação:** os resultados de desempenho dependem do dispositivo, do backend gráfico, da versão do emulador e das configurações utilizadas. A estabilidade de 60 FPS não é garantida em todos os aparelhos.

---

## ⚡ Perfil de Hardware (C++ / NDK)

O projeto prevê um perfil de configuração voltado à execução do God of War II em dispositivos Android com arquitetura ARM64, incluindo aparelhos com GPUs Qualcomm Adreno e ARM Mali.

Os parâmetros abaixo representam o perfil pretendido e devem ser confirmados na implementação efetiva do projeto:

| Parâmetro | Configuração pretendida | Objetivo |
| :--- | :--- | :--- |
| **Backend gráfico** | Vulkan, quando compatível | Reduzir o overhead gráfico e melhorar o desempenho. |
| **MTVU** | Ativado, quando suportado | Aproveitar o processamento multithread da unidade vetorial. |
| **VU1 Instant** | Conforme suporte do emulador | Ajustar o processamento de microprogramas vetoriais. |
| **EE Cycle Rate** | 100% nominal | Preservar a sincronização e a compatibilidade do jogo. |
| **EE Cycle Skip** | 0, inicialmente | Evitar problemas de sincronização e compatibilidade. |
| **Fastmem** | Ativado, quando compatível | Otimizar o acesso à memória em implementações que suportem esse recurso. |
| **Half-Pixel Offset** | Conforme necessidade | Corrigir problemas de posicionamento e renderização de texturas. |
| **Align Sprite** | Conforme necessidade | Corrigir artefatos gráficos em sprites. |
| **Round Sprite** | Conforme necessidade | Melhorar a renderização de determinados elementos gráficos. |
| **Proporção** | 16:9, quando suportado | Utilizar uma apresentação widescreen compatível com o jogo. |

A aplicação dessas configurações depende do mecanismo de emulação integrado e das opções realmente disponíveis. Os parâmetros não devem ser considerados injetados ou forçados até que isso seja confirmado no código.

---

## 🕹️ Como Usar (Guia do Jogador)

> [!IMPORTANT]
> **Preservação e direitos autorais:** este projeto não deve distribuir imagens de jogos protegidos por direitos autorais sem autorização. Utilize uma cópia obtida legalmente da sua própria mídia de PlayStation 2, respeitando a legislação aplicável.

### Passo 1: Instale o APK

Baixe e instale o pacote Android ARM64:

[GodOfWar2-Standalone-arm64-v8a.apk](releases/GodOfWar2-Standalone-arm64-v8a.apk)

O arquivo precisa existir nesse caminho do repositório para que o link funcione.

### Passo 2: Posicione sua ISO

O aplicativo prevê a busca de imagens do jogo nos seguintes locais:

- `/storage/emulated/0/GodOfWar2/gow2.iso`
- `/storage/emulated/0/Download/gow2.iso`
- `/storage/emulated/0/PS2/gow2.iso`
- Armazenamento privado do aplicativo, conforme a implementação e as permissões disponíveis.

Formatos previstos: `.iso`, `.cso`, `.chd` e `.gz`, dependendo do suporte efetivamente implementado.

Identificadores de referência:

- Versão americana: `SCUS-97481`
- Versão europeia: `SCES-54206`

O suporte a cada formato e região deve ser validado no mecanismo de inicialização utilizado.

### Passo 3: Primeiro Boot

Quando uma imagem compatível for localizada e todas as configurações necessárias estiverem concluídas, o aplicativo poderá iniciar o jogo.

Se nenhuma imagem for encontrada, o comportamento esperado é apresentar uma interface para selecionar o arquivo e conceder as permissões necessárias. A persistência dessas permissões depende da implementação do aplicativo.

---

## 🛠️ Arquitetura do Workspace

A estrutura abaixo representa a organização pretendida para o projeto. Os arquivos e diretórios devem ser conferidos no repositório para confirmar sua existência.

```text
Gof_of_war_2/
├── android/
│   └── app/
│       └── src/
│           └── main/
│               ├── AndroidManifest.xml
│               ├── java/
│               │   └── com/gow2/launcher/
│               │       ├── GodOfWarLauncherActivity.java
│               │       ├── IsoLocator.java
│               │       └── HardwareProfileManager.java
│               ├── cpp/
│               │   ├── gow2_hardware_profile.h
│               │   ├── gow2_hardware_profile.cpp
│               │   └── gow2_native_bridge.cpp
│               ├── assets/
│               │   └── patches/
│               └── res/
│                   ├── drawable/
│                   │   └── gow_button_background.xml
│                   └── layout/
│                       └── activity_launcher.xml
├── dist/
├── releases/
├── tools/
│   ├── patch_apk.ps1
│   └── build_release_apk.ps1
├── README.md
└── LICENSE
```

### Componentes previstos

- **GodOfWarLauncherActivity.java:** atividade principal e interface de inicialização.
- **IsoLocator.java:** busca e localização da imagem do jogo.
- **HardwareProfileManager.java:** gerenciamento das configurações de desempenho.
- **gow2_hardware_profile.h / .cpp:** parâmetros de configuração nativa.
- **gow2_native_bridge.cpp:** integração entre Java e código nativo por JNI.
- **patch_apk.ps1:** automação de modificações previstas no pacote.
- **build_release_apk.ps1:** automação da compilação de uma versão de lançamento.

A disponibilidade e a função de cada componente dependem dos arquivos presentes na versão atual do projeto.

---

## ⚖️ Isenção Legal / Disclaimer

Este projeto é uma iniciativa comunitária associada à marca **Gambiarra Games** (`gambiarra.games`).

- Não possui afiliação, patrocínio ou endosso da Sony Interactive Entertainment ou dos desenvolvedores originais de God of War.
- God of War II e suas marcas, personagens e elementos relacionados pertencem aos respectivos titulares de direitos.
- Este repositório não deve incluir conteúdo protegido por direitos autorais sem autorização.
- A compatibilidade, o desempenho e o funcionamento dependem do dispositivo Android, dos componentes utilizados e da configuração do usuário.
- Referências a tecnologias, projetos e mecanismos de emulação não implicam parceria ou endosso por seus respectivos responsáveis.

<br/>

<div align="center">

**⚔️ Criado por Gambiarra Games • [gambiarra.games](https://gambiarra.games) ⚔️**

</div>