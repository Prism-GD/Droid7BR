#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_br_com_prismgd_droid7br_vm_NativeRuntime_nativeEngineStatus(
    JNIEnv* env,
    jobject /* thiz */
) {
    const std::string status =
        "Ponte JNI nativa carregada. QEMU ainda não foi integrado.";
    return env->NewStringUTF(status.c_str());
}
