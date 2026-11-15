package br.com.prismgd.droid7br.vm

import android.content.Context
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Runs qemu-system-aarch64 as an Android app process using software TCG.
 * The guest kernel and initrd must target QEMU's generic "virt" machine.
 */
class QemuEngine(context: Context) : VmEngine {
    private val appContext = context.applicationContext
    private val runtime = QemuRuntime(appContext)
    private val storage = VmStorageManager(appContext)
    private val guestDirectory = File(appContext.filesDir, "guest-images/android-7.1.2")
    private val kernel = File(guestDirectory, "Image")
    private val initrd = File(guestDirectory, "initrd.img")
    private val logFile = File(guestDirectory, "qemu.log")

    @Volatile
    private var process: Process? = null

    override fun start(config: VmConfig, onStateChanged: (VmState) -> Unit) {
        synchronized(this) {
            if (process?.isAlive == true) {
                onStateChanged(VmState.RUNNING)
                return
            }
            onStateChanged(VmState.STARTING)
            if (!runtime.isAvailable() || !kernel.isFile || !initrd.isFile) {
                onStateChanged(VmState.ERROR)
                return
            }

            try {
                if (!guestDirectory.exists() && !guestDirectory.mkdirs()) {
                    throw IllegalStateException("Não foi possível criar a pasta da imagem convidada.")
                }
                val disk = storage.prepareDisk(config)
                val command = listOf(
                    runtime.executable.absolutePath,
                    "-machine", "virt",
                    "-cpu", "max",
                    "-accel", "tcg,thread=multi",
                    "-m", config.ramMb.toString(),
                    "-smp", config.cpuCores.toString(),
                    "-kernel", kernel.absolutePath,
                    "-initrd", initrd.absolutePath,
                    "-append", "console=ttyAMA0,115200",
                    "-drive", "if=none,id=system,format=raw,file=${disk.absolutePath}",
                    "-device", "virtio-blk-device,drive=system",
                    "-netdev", "user,id=net0",
                    "-device", "virtio-net-device,netdev=net0",
                    "-display", "none",
                    "-serial", "stdio",
                    "-monitor", "none"
                )
                val builder = ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .redirectOutput(logFile)
                builder.environment()["LD_LIBRARY_PATH"] = appContext.applicationInfo.nativeLibraryDir
                val started = builder.start()
                process = started
                onStateChanged(VmState.RUNNING)

                Thread({
                    val exitCode = try {
                        started.waitFor()
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        -1
                    }
                    synchronized(this) { if (process === started) process = null }
                    onStateChanged(if (exitCode == 0) VmState.STOPPED else VmState.ERROR)
                }, "Droid7BR-QEMU-monitor").apply {
                    isDaemon = true
                    start()
                }
            } catch (_: Exception) {
                process = null
                onStateChanged(VmState.ERROR)
            }
        }
    }

    override fun pause() {
        throw UnsupportedOperationException("Pausa via QMP ainda não foi implementada.")
    }

    override fun resume() {
        throw UnsupportedOperationException("Retomada via QMP ainda não foi implementada.")
    }

    override fun shutdown() {
        val running = process ?: return
        running.destroy()
        if (!running.waitFor(3, TimeUnit.SECONDS)) running.destroyForcibly()
        synchronized(this) { process = null }
    }
}
