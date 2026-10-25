#include <jni.h>
#include "gow2_hardware_profile.h"
#include <android/log.h>

#define LOG_TAG "GoW2_NativeBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * Ponto de entrada JNI para forcar as configuracoes de baixo nivel
 * antes ou durante a inicializacao do nucleo do emulador.
 */
JNIEXPORT void JNICALL
Java_com_gow2_launcher_HardwareProfileManager_applyNativeHardwareOverrides(
        JNIEnv* env,
        jclass clazz) {
    LOGI("[GoW2_Bridge] Solicitada injecao nativa de hardware para God of War 2");
    auto profile = GoW2::GetDefaultGoW2Profile();
    GoW2::ApplyHardwareProfile(profile);
}

/**
 * Chamado automaticamente quando a biblioteca nativa e carregada.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    LOGI("[GoW2_Bridge] JNI_OnLoad inicializado. Perfil GoW2 pronto para aplicacao.");
    return JNI_VERSION_1_6;
}

} // extern "C"
