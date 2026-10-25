package com.gow2.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Atividade principal e ponto de entrada exclusivo do God of War 2 Standalone.
 * Ignora completamente menus de emulador e lança o jogo direto em fullscreen.
 */
public class GodOfWarLauncherActivity extends Activity {
    private static final String PREF_SAVED_ISO_PATH = "gow2_custom_iso_path";
    private static final int REQUEST_CODE_PICK_ISO = 1001;
    private static final String TARGET_EMULATION_ACTIVITY = "xyz.aethersx2.android.EmulationActivity";

    private TextView statusTextView;
    private Button pickIsoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Força tela cheia sem títulos
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        applyImmersiveMode();

        // Injeta obrigatoriamente as configurações de hardware para GoW 2
        HardwareProfileManager.applyForcedGoW2Settings(this);

        // 1. Tenta carregar caminho salvo previamente
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedPath = prefs.getString(PREF_SAVED_ISO_PATH, null);
        if (savedPath != null && new File(savedPath).exists()) {
            launchGame(savedPath);
            return;
        }

        // 2. Busca automática da ISO nos diretórios padrões
        String autoFoundPath = IsoLocator.findGodOfWar2Iso(this);
        if (autoFoundPath != null) {
            prefs.edit().putString(PREF_SAVED_ISO_PATH, autoFoundPath).apply();
            launchGame(autoFoundPath);
            return;
        }

        // 3. Fallback: Exibe tela com identidade visual do God of War 2 caso a ISO ainda não esteja no local
        setupFallbackUI();
    }

    private void applyImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void setupFallbackUI() {
        setContentView(getResources().getIdentifier("activity_launcher", "layout", getPackageName()));

        int statusId = getResources().getIdentifier("tv_status", "id", getPackageName());
        int btnId = getResources().getIdentifier("btn_pick_iso", "id", getPackageName());

        if (statusId != 0) {
            statusTextView = findViewById(statusId);
        }
        if (btnId != 0) {
            pickIsoButton = findViewById(btnId);
            pickIsoButton.setOnClickListener(v -> openFilePicker());
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_ISO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_ISO && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Persiste permissão de leitura permanente para a URI selecionada
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (Exception ignored) {}

                String isoUriString = uri.toString();
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(PREF_SAVED_ISO_PATH, isoUriString)
                        .apply();

                launchGame(isoUriString);
            }
        }
    }

    /**
     * Dispara a execução direta do God of War 2 na atividade de emulação nativa.
     */
    private void launchGame(String bootPath) {
        try {
            Intent emuIntent = new Intent();
            emuIntent.setClassName(getPackageName(), TARGET_EMULATION_ACTIVITY);
            emuIntent.putExtra("bootPath", bootPath);
            emuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(emuIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar o núcleo de emulação: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersiveMode();
        }
    }
}
