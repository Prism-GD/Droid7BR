package br.com.prismgd.droid7br.vm

/**
 * Contract for the Android guest runtime.
 *
 * The QEMU implementation will be added after the guest image format,
 * CPU architecture and Android-host execution strategy are validated.
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
