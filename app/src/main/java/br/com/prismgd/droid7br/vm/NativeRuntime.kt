package br.com.prismgd.droid7br.vm

/**
 * JNI entry point for the native runtime.
 *
 * This JNI library reports native bridge status. QEMU is packaged separately as an Android ARM64 executable.
 */
object NativeRuntime {
    private val loaded: Boolean = try {
        System.loadLibrary("droid7br_native")
        true
    } catch (_: UnsatisfiedLinkError) {
        false
    } catch (_: SecurityException) {
        false
    }

    fun isLoaded(): Boolean = loaded

    fun engineStatus(): String {
        if (!loaded) return "Biblioteca nativa indisponível neste build."
        return try {
            nativeEngineStatus()
        } catch (_: UnsatisfiedLinkError) {
            "Função JNI não encontrada."
        }
    }

    private external fun nativeEngineStatus(): String
}
