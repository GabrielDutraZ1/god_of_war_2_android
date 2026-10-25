package com.gow2.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Gerencia e força as configurações ideais de hardware para God of War 2
 * no ecossistema AetherSX2 / PCSX2, garantindo 60 FPS e extraindo a BIOS embutida.
 */
public class HardwareProfileManager {
    private static final String TAG = "GoW2_ProfileManager";
    private static final String BUNDLED_BIOS_NAME = "scph39001.bin";
    private static boolean nativeLibraryLoaded = false;

    static {
        try {
            System.loadLibrary("gow2_core_bridge");
            nativeLibraryLoaded = true;
            Log.i(TAG, "Biblioteca nativa gow2_core_bridge carregada com sucesso.");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "gow2_core_bridge nao presente no momento, utilizando injecao direta de SharedPreferences.");
        }
    }

    public static native void applyNativeHardwareOverrides();

    /**
     * Extrai a BIOS oficial dos assets para o diretório privado interno do app
     * no primeiro boot (seguindo a arquitetura de assets do Dusklight).
     */
    public static void ensureBundledBiosExtracted(Context context) {
        File biosDir = new File(context.getFilesDir(), "bios");
        if (!biosDir.exists()) {
            biosDir.mkdirs();
        }

        File targetBios = new File(biosDir, BUNDLED_BIOS_NAME);
        if (targetBios.exists() && targetBios.length() == 4194304) {
            Log.i(TAG, "BIOS oficial ja presente em: " + targetBios.getAbsolutePath());
            return;
        }

        Log.i(TAG, "Extraindo BIOS oficial embutida para o diretorio privado...");
        try (InputStream in = context.getAssets().open("bios/" + BUNDLED_BIOS_NAME);
             OutputStream out = new FileOutputStream(targetBios)) {
            byte[] buffer = new byte[65536];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            Log.i(TAG, "BIOS oficial extraida com sucesso (" + targetBios.length() + " bytes).");
        } catch (IOException e) {
            Log.e(TAG, "Falha ao extrair BIOS embutida: " + e.getMessage());
        }
    }

    /**
     * Aplica de forma irreversível os parâmetros ótimos para God of War 2
     * nas preferências compartilhadas do emulador e via chamadas nativas C++.
     */
    public static void applyForcedGoW2Settings(Context context) {
        ensureBundledBiosExtracted(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Log.i(TAG, "Injetando parametros de hardware dedicados para God of War 2...");

        // 1. Bypass do Wizard de Boas-vindas e seleção de BIOS
        editor.putBoolean("SetupWizardIncomplete", false);
        editor.putBoolean("first_run", false);
        editor.putString("BIOS", BUNDLED_BIOS_NAME);
        editor.putString("bios_path", new File(new File(context.getFilesDir(), "bios"), BUNDLED_BIOS_NAME).getAbsolutePath());

        // 2. Microarquitetura da CPU / Emotion Engine e VU Speedhacks
        editor.putBoolean("Speedhacks", true);
        editor.putBoolean("vuThread", true);         // MTVU1 (Multi-Threaded VU1) - ESSENCIAL
        editor.putBoolean("vu1Instant", true);       // VU1 Instantânea (menor latência)
        editor.putBoolean("vuFlagHack", true);
        editor.putString("EECycleRate", "0");        // 100% nominal (Sem underclock)
        editor.putString("EECycleSkip", "0");        // 0 = Desativado (Protege HUD e QTEs de flicker)
        editor.putBoolean("EnableFastmem", true);    // Fast memory MMU ARM64

        // 3. Subconjunto Gráfico e Renderer
        editor.putString("Renderer", "12");          // 12 = Vulkan API
        editor.putString("AspectRatio", "16:9");     // Widescreen
        editor.putBoolean("EnableWideScreenPatches", true);
        editor.putBoolean("EnableFastBoot", true);   // Ignora tela de abertura do console PS2
        editor.putBoolean("StartFullscreen", true);  // Fullscreen imersivo

        // 4. GS Hardware Fixes Oficiais para GoW 2 (SCUS-97481 / SCES-54206)
        editor.putBoolean("UserHacks", true);
        editor.putBoolean("UserHacks_align_sprite_X", true); // Fix de linhas verticais em água e névoa
        editor.putString("UserHacks_HalfPixelOffset", "2");  // Fix do bloom/glow desalinhado em Kratos (Special Texture)
        editor.putString("UserHacks_round_sprite_offset", "1"); // Fix de aberração cromática de sprites
        editor.putString("UserHacks_AutoFlushLevel", "0");

        // 5. Clamping e Rounding Modes Seguros
        editor.putString("FPUClampMode", "0");       // Normal
        editor.putString("VU0ClampMode", "0");       // Normal
        editor.putString("VU1ClampMode", "0");       // Normal
        editor.putString("Roundmode", "0");          // Chop / Zero

        // 6. UI Limpa
        editor.putBoolean("OsdShowSpeed", false);
        editor.putBoolean("OsdShowFPS", false);
        editor.putBoolean("OsdShowIndicators", false);

        editor.apply();
        Log.i(TAG, "Perfil de hardware para God of War 2 aplicado com sucesso.");

        if (nativeLibraryLoaded) {
            try {
                applyNativeHardwareOverrides();
            } catch (Throwable t) {
                Log.e(TAG, "Falha ao invocar applyNativeHardwareOverrides(): " + t.getMessage());
            }
        }
    }
}
