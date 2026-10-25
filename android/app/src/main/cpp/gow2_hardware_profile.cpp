#include "gow2_hardware_profile.h"
#include <android/log.h>

#define LOG_TAG "GoW2_Core"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace GoW2 {

EmuCoreHardwareConfig GetDefaultGoW2Profile() {
    EmuCoreHardwareConfig profile{};

    // Emotion Engine & VU Pipeline
    profile.ee_cycle_rate = 0;              // 100% nominal. Evita descompasso do audio de streaming e da fisica
    profile.ee_cycle_skip = 0;              // 0 = Desativado. ESSENCIAL: GoW2 sofre com flicker de HUD e perdas de colisao se cycle skip estiver ativo
    profile.mtvu1_thread = true;            // MTVU1: Executa a VU1 em um nucleo ARM isolado (Desempenho dobra em SoC mobile)
    profile.vu1_instant = true;             // VU1 Instant: Minimiza latencia na fila de microcodigo de animacao de vertices
    profile.vu_flag_hack = true;
    profile.fastmem = true;                 // Fastmem: ARM64 direct virtual addressing
    profile.intc_stat_hack = true;
    profile.wait_loop_hack = true;

    // Graphics Subsystem & Backend
    profile.renderer_backend = 12;          // 12 = Vulkan. Muito mais rapido que OpenGL ES em Adreno e Mali modernos
    profile.upscale_multiplier = 2;         // 2x Resolucao Nativa (720p HD) padrao
    profile.aspect_ratio = 1;               // 16:9 Widescreen
    profile.enable_widescreen_patches = true;
    profile.enable_fastboot = true;          // Ignora a animacao inicial do console PS2 para carregamento transparente instantaneo

    // GS Hardware Fixes Oficiais de God of War 2 (GameIndex SCUS-97481 / SCES-54206)
    profile.enable_user_hacks = true;
    profile.align_sprite_x = 1;             // Corrige as linhas verticais que cruzam a superficie da agua no Colosso e pantano
    profile.half_pixel_offset = 2;          // Modo 2 (Special - Texture). Corrige o bloom/glow borrado deslocado ao redor do Kratos
    profile.round_sprite_offset = 1;        // Corrige distorcoes de escala e linhas nos medidores de orbs e vida

    // Precision & Clamping
    profile.fpu_clamp_mode = 0;             // Normal
    profile.vu0_clamp_mode = 0;             // Normal
    profile.vu1_clamp_mode = 0;             // Normal
    profile.round_mode = 0;                 // Chop / Zero

    return profile;
}

void ApplyHardwareProfile(const EmuCoreHardwareConfig& config) {
    LOGI("=================================================================");
    LOGI("[GoW2 Standalone] Aplicando Perfil de Hardware Dedicado...");
    LOGI("  Backend Grafico: %s", (config.renderer_backend == 12) ? "Vulkan" : "OpenGL ES");
    LOGI("  Speedhacks: MTVU1=%s, VU1_Instant=%s, Fastmem=%s",
         config.mtvu1_thread ? "SIM" : "NAO",
         config.vu1_instant ? "SIM" : "NAO",
         config.fastmem ? "SIM" : "NAO");
    LOGI("  Ciclos EE: Rate=%d (100%%), CycleSkip=%d (Desativado/Protecao de HUD)",
         config.ee_cycle_rate, config.ee_cycle_skip);
    LOGI("  GS Hardware Fixes: AlignSprite=%d, HalfPixelOffset=%d, RoundSprite=%d",
         config.align_sprite_x, config.half_pixel_offset, config.round_sprite_offset);
    LOGI("  Aspect Ratio: 16:9 Widescreen, FastBoot=SIM");
    LOGI("=================================================================");
}

} // namespace GoW2
