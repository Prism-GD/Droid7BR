#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_br_com_prismgd_droid7br_vm_NativeRuntime_nativeEngineStatus(
    JNIEnv* env,
    jobject /* thiz */
) {
    const std::string status =
        "Ponte JNI nativa carregada. O QEMU ARM64 é executado como processo separado.";
    return env->NewStringUTF(status.c_str());
}
