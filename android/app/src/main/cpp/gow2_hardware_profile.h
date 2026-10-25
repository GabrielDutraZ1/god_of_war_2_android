#pragma once

#include <cstdint>
#include <string>

namespace GoW2 {

/**
 * Estrutura representativa de baixo nível correspondente
 * aos parâmetros do núcleo PCSX2 / AetherSX2.
 */
struct EmuCoreHardwareConfig {
    // === CPU / Emotion Engine & Speedhacks ===
    int32_t ee_cycle_rate;          // 0 = 100% (Normal), -1 = 75%, 1 = 130%
    int32_t ee_cycle_skip;          // 0 = Desativado (Crucial para nao quebrar HUD/QTEs no GoW2)
    bool mtvu1_thread;              // Multi-Threaded VU1 (Obrigatorio em ARM64)
    bool vu1_instant;               // Execucao instantanea de microcodigo VU1
    bool vu_flag_hack;              // Otimizacao de flags de branch de VU
    bool fastmem;                   // Mapeamento direto de memoria virtual do EE
    bool intc_stat_hack;
    bool wait_loop_hack;

    // === GS / Graphics Subsystem ===
    int32_t renderer_backend;       // 12 = Vulkan API, 1 = OpenGL
    int32_t upscale_multiplier;     // 1 = 1x Native (480p), 2 = 2x (720p HD), 3 = 3x (1080p FHD)
    int32_t aspect_ratio;           // 1 = 16:9 Widescreen
    bool enable_widescreen_patches;
    bool enable_fastboot;           // Ignora OSD da BIOS do PS2

    // === GS Hardware Fixes (Especificos para God of War II) ===
    bool enable_user_hacks;
    int32_t align_sprite_x;         // 1 = Ativado (Corrige linhas verticais em agua e nevoa)
    int32_t half_pixel_offset;      // 2 = Special - Texture (Elimina ghosting de luz/bloom em Kratos)
    int32_t round_sprite_offset;    // 1 = Ativado (Corrige artefatos de HUD)

    // === Precision Modes (Evitam instabilidade de colisao) ===
    int32_t fpu_clamp_mode;         // 0 = Normal
    int32_t vu0_clamp_mode;         // 0 = Normal
    int32_t vu1_clamp_mode;         // 0 = Normal
    int32_t round_mode;             // 0 = Toward Zero (Chop)
};

/**
 * Retorna o perfil de hardware de referencia testado e validado
 * para maxima taxa de quadros e zero artefatos no God of War 2.
 */
EmuCoreHardwareConfig GetDefaultGoW2Profile();

/**
 * Injeta forcadamente as configuracoes nas estruturas de memoria
 * do emulador durante o boot.
 */
void ApplyHardwareProfile(const EmuCoreHardwareConfig& config);

} // namespace GoW2
