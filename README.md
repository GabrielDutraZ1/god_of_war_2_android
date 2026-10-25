<div align="center">

# ⚔️ God of War II — Standalone Launcher (Android) ⚔️

### Experiência de Source Port Dedicado e Otimizado para PlayStation 2 no Android

[![Gambiarra Games](https://img.shields.io/badge/Projeto_por-Gambiarra_Games-red?style=for-the-badge&logo=playstation)](https://gambiarra.games)
[![Target Architecture](https://img.shields.io/badge/ABI-arm64--v8a-blue?style=for-the-badge)](https://developer.android.com/ndk)
[![Graphics Backend](https://img.shields.io/badge/API-Vulkan_1.1+-green?style=for-the-badge&logo=vulkan)](https://www.khronos.org/vulkan/)
[![License](https://img.shields.io/badge/License-GPL_/_LGPL-orange?style=for-the-badge)](LICENSE)

<br/>

**Desenvolvido por Gambiarra Games • [gambiarra.games](https://gambiarra.games)**

---

### 📦 [CLIQUE AQUI PARA BAIXAR O APK COMPILADO (arm64-v8a)](releases/GodOfWar2-Standalone-arm64-v8a.apk)
*(Tamanho: 10.89 MB • Versão Standalone Pronta para Instalar)*

---

</div>

## 📖 Visão Geral

Este projeto transforma a experiência de executar o clássico **God of War II** no Android. Inspirado na arquitetura e filosofia de projetos de standalone port (como **Dusklight** e **God of War Collection I & II para PC**), eliminamos qualquer interface genérica de emulador, seletores de ROMs poluídos e menus intrusivos.

Ao abrir o aplicativo:
1. O jogo **God of War II** inicia de forma transparente e em tela cheia (1-clique).
2. As configurações de hardware, speedhacks e renderização são forçadas nativamente para garantir **60 FPS** estáveis sem quebras visuais.
3. Não há menus de emulador intermediários nem necessidade de configurar controles do zero.

---

## ⚡ Perfil de Hardware Hardcoded (C++ / NDK)

Para garantir que God of War II rode sem os conhecidos problemas de ghosting, linhas pretas e slowdowns em chips móveis (Qualcomm Snapdragon Adreno e ARM Mali), os seguintes parâmetros são injetados obrigatoriamente no boot:

| Parâmetro | Valor Forçado | Benefício Técnico no God of War 2 |
| :--- | :--- | :--- |
| **Backend Gráfico** | `Vulkan (12)` | Draw calls otimizadas e overhead reduzido de CPU no Android. |
| **MTVU1** | `Ativado (True)` | Separa a Vector Unit 1 em um núcleo ARM dedicado (dobro de performance em geometria). |
| **VU1 Instant** | `Ativado (True)` | Reduz latência na fila de microcódigo de animação de vértices de Kratos e inimigos. |
| **EE Cycle Rate** | `100% Nominal (0)` | Evita descompasso de áudio nas cutscenes e lentidão no motor de física. |
| **EE Cycle Skip** | `0 (Desativado)` | **Crucial:** O frame skipping de ciclos no EE quebra os QTEs e gera flickering violento no HUD. |
| **Fastmem** | `Ativado (True)` | Mapeamento direto de memória virtual do EE para 64 bits. |
| **Half Pixel Offset** | `Modo 2 (Texture)` | **GS Fix:** Elimina o efeito de ghosting / bloom deslocado ao redor de Kratos. |
| **Align Sprite** | `Ativado (1)` | **GS Fix:** Corrige linhas verticais que cruzam a água e névoa no Colosso de Rodes. |
| **Round Sprite** | `Ativado (1)` | **GS Fix:** Corrige artefatos de borda nos medidores de orbs e vida. |
| **Proporção** | `16:9 Widescreen` | Widescreen nativo via PNACH Hor+ sem deformação de imagem. |

---

## 🕹️ Como Usar (Guia do Jogador)

> [!IMPORTANT]
> **Política de Preservação e Direitos Autorais:** 
> Este pacote **NÃO** acompanha e **NÃO** fornece imagens protegidas por direitos autorais (arquivos ISO). O usuário deve utilizar o dump legal da sua própria mídia física de PlayStation 2.

### Passo 1: Instale o APK
Baixe e instale o pacote [releases/GodOfWar2-Standalone-arm64-v8a.apk](releases/GodOfWar2-Standalone-arm64-v8a.apk) em seu celular ou tablet Android (ARM64).

### Passo 2: Posicione a sua ISO
O aplicativo possui o sistema autônomo **IsoLocator**, que busca automaticamente nos seguintes locais:
- `/storage/emulated/0/GodOfWar2/gow2.iso`
- `/storage/emulated/0/Download/gow2.iso`
- `/storage/emulated/0/PS2/gow2.iso`
- Ou no armazenamento privado do próprio app (`Android/data/xyz.aethersx2.android/files/`).

Formatos aceitos: `.iso`, `.cso`, `.chd` e `.gz` (Versões Americana `SCUS-97481` e Europeia `SCES-54206`).

### Passo 3: Primeiro Boot
- Se a ISO for encontrada nas pastas padrão, o jogo iniciará **instantaneamente**.
- Caso contrário, uma tela com a identidade visual espartana será exibida com um botão de pedra e borda carmim para você selecionar o arquivo da sua cópia uma única vez. O app salvará a permissão de forma permanente.

---

## 🛠️ Arquitetura do Workspace

```text
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml             # Entrada única e stripping de UI
│   │   │   ├── java/com/gow2/launcher/
│   │   │   │   ├── GodOfWarLauncherActivity.java # Atividade de lançamento Fullscreen
│   │   │   │   ├── IsoLocator.java             # Motor de busca autônoma de ISO
│   │   │   │   └── HardwareProfileManager.java # Injeção de perfil de hardware
│   │   │   ├── cpp/
│   │   │   │   ├── gow2_hardware_profile.h     # Definições C++20 de baixo nível
│   │   │   │   ├── gow2_hardware_profile.cpp   # Perfil de speedhacks e fixes
│   │   │   │   └── gow2_native_bridge.cpp      # Ponte JNI
│   │   │   ├── assets/
│   │   │   │   └── patches/                    # Patches PNACH Widescreen 16:9 & Skip FMV
│   │   │   └── res/
│   │   │       ├── drawable/gow_button_background.xml # Botão pedra/borda carmim
│   │   │       └── layout/activity_launcher.xml       # UI com marca d'água
├── dist/                                       # Pasta com o APK gerado
├── releases/                                   # Download direto do APK para o GitHub
└── tools/
    ├── patch_apk.ps1                           # Script de stripping automatizado
    └── build_release_apk.ps1                   # Pipeline de compilação Release ARM64
```

---

## ⚖️ Isenção Legal / Disclaimer

Este projeto é uma iniciativa comunitária sem fins comerciais mantida por **Gambiarra Games** (`gambiarra.games`).
- Não possui afiliação, patrocínio ou endosso da **Sony Interactive Entertainment** ou dos desenvolvedores originais de God of War.
- Todas as marcas registradas, títulos e logos pertencem aos seus respectivos proprietários.

<br/>

<div align="center">

**⚔️ Criado por Gambiarra Games • [gambiarra.games](https://gambiarra.games) ⚔️**

</div>
