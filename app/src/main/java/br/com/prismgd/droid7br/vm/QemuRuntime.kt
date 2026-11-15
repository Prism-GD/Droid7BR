package br.com.prismgd.droid7br.vm

import android.content.Context
import java.io.File
import java.util.concurrent.TimeUnit

/** Locates and probes the ARM64 QEMU executable packaged in jniLibs. */
class QemuRuntime(context: Context) {
    private val nativeLibraryDir = context.applicationInfo.nativeLibraryDir
    val executable = File(nativeLibraryDir, "libqemu-system-aarch64.so")
    private val slirpLibrary = File(nativeLibraryDir, "libslirp.so")

    fun isAvailable(): Boolean =
        executable.isFile && executable.canExecute() && slirpLibrary.isFile

    fun version(): String {
        if (!isAvailable()) return "QEMU ARM64 não está incluído neste APK."
        return try {
            val process = ProcessBuilder(executable.absolutePath, "--version")
                .redirectErrorStream(true)
                .apply { environment()["LD_LIBRARY_PATH"] = nativeLibraryDir }
                .start()
            if (!process.waitFor(8, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return "O teste do QEMU excedeu o tempo limite."
            }
            val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
            if (process.exitValue() == 0) output.lineSequence().firstOrNull()
                ?: "QEMU respondeu sem informar a versão."
            else "Falha ao executar QEMU (código ${process.exitValue()})."
        } catch (error: Exception) {
            "Não foi possível executar QEMU: ${error.message ?: "erro desconhecido"}"
        }
    }
}
