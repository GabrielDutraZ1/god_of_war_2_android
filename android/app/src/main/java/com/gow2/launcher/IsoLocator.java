package com.gow2.launcher;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitário responsável por localizar a ISO oficial de God of War 2 no dispositivo,
 * priorizando armazenamento interno, cartões SD e pastas dedicadas.
 */
public class IsoLocator {
    private static final String TAG = "GoW2_IsoLocator";

    // Padrões de nomes aceitos para a ISO/imagem de God of War 2
    private static final String[] GOW2_FILENAMES = {
        "gow2.iso",
        "gow2.cso",
        "gow2.chd",
        "gow2.gz",
        "God of War 2.iso",
        "God of War 2.cso",
        "God of War 2.chd",
        "God of War II.iso",
        "God of War II.cso",
        "God of War II.chd",
        "God of War II (USA).iso",
        "God of War II (Europe).iso",
        "SCUS-97481.iso",
        "SCES-54206.iso"
    };

    /**
     * Busca a ISO do God of War 2 em locais candidatos estratégicos.
     * Retorna o caminho absoluto do arquivo ou null se não encontrado.
     */
    public static String findGodOfWar2Iso(Context context) {
        List<File> searchDirs = new ArrayList<>();

        // 1. Armazenamento privado do app (onde o usuário ou instalador pode injetar sem permissões)
        File extFiles = context.getExternalFilesDir(null);
        if (extFiles != null) {
            searchDirs.add(extFiles);
            searchDirs.add(new File(extFiles, "ISO"));
        }
        searchDirs.add(context.getFilesDir());
        searchDirs.add(new File(context.getFilesDir(), "ISO"));

        // 2. Armazenamento compartilhado / primário do Android
        File primaryStorage = Environment.getExternalStorageDirectory();
        if (primaryStorage != null && primaryStorage.exists()) {
            searchDirs.add(new File(primaryStorage, "GodOfWar2"));
            searchDirs.add(new File(primaryStorage, "GodOfWar"));
            searchDirs.add(new File(primaryStorage, "PS2"));
            searchDirs.add(new File(primaryStorage, "Download"));
            searchDirs.add(new File(primaryStorage, "ROMs/PS2"));
            searchDirs.add(primaryStorage);
        }

        // 3. Checa diretórios candidatos
        for (File dir : searchDirs) {
            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }

            for (String filename : GOW2_FILENAMES) {
                File candidate = new File(dir, filename);
                if (candidate.exists() && candidate.isFile() && candidate.length() > 1024 * 1024) { // > 1MB
                    Log.i(TAG, "ISO de God of War 2 encontrada: " + candidate.getAbsolutePath());
                    return candidate.getAbsolutePath();
                }
            }
        }

        Log.w(TAG, "ISO de God of War 2 nao foi encontrada nos caminhos automaticos.");
        return null;
    }
}
