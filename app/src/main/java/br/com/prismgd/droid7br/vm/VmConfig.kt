package br.com.prismgd.droid7br.vm

import java.util.UUID

data class VmConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Android Nougat",
    val androidVersion: String = "7.1.2",
    val apiLevel: Int = 25,
    val ramMb: Int = 1536,
    val storageGb: Int = 8,
    val cpuCores: Int = 2
)
