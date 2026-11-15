package br.com.prismgd.droid7br.vm

/**
 * Contract for an Android guest runtime. QemuEngine implements the initial
 * ARM64 TCG process backend; boot assets for Android 7.1.2 are still pending.
 */
interface VmEngine {
    fun start(config: VmConfig, onStateChanged: (VmState) -> Unit)
    fun pause()
    fun resume()
    fun shutdown()
}

enum class VmState {
    STOPPED,
    STARTING,
    RUNNING,
    PAUSED,
    STOPPING,
    ERROR
}
