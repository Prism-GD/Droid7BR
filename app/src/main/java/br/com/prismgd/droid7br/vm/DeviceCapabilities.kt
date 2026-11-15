package br.com.prismgd.droid7br.vm

import android.os.Build
import java.io.File

/**
 * Read-only information about the Android device hosting Droid7BR.
 * KVM availability is reported conservatively: the device node must exist
 * and be accessible to the app. This does not guarantee that QEMU can use it.
 */
data class DeviceCapabilities(
    val supportedAbis: List<String>,
    val primaryAbi: String,
    val isArm64: Boolean,
    val kvmDevicePresent: Boolean,
    val kvmDeviceReadable: Boolean,
    val kvmDeviceWritable: Boolean
) {
    val kvmLikelyUsableByApp: Boolean
        get() = kvmDevicePresent && kvmDeviceReadable && kvmDeviceWritable

    companion object {
        fun detect(): DeviceCapabilities {
            val abis = Build.SUPPORTED_ABIS.toList()
            val kvm = File("/dev/kvm")
            return DeviceCapabilities(
                supportedAbis = abis,
                primaryAbi = abis.firstOrNull() ?: "unknown",
                isArm64 = abis.any { it.equals("arm64-v8a", ignoreCase = true) },
                kvmDevicePresent = kvm.exists(),
                kvmDeviceReadable = kvm.canRead(),
                kvmDeviceWritable = kvm.canWrite()
            )
        }
    }
}
