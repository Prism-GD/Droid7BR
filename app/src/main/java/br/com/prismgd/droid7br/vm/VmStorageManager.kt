package br.com.prismgd.droid7br.vm

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Manages private, persistent raw disk files for each VM.
 *
 * The file is sparse where supported by the filesystem. Its logical size is
 * storageGb, but allocated physical space grows as the guest writes data.
 * This creates storage only; it does not format or install an operating system.
 */
class VmStorageManager(context: Context) {
    private val root = File(context.filesDir, "virtual-machines")

    init {
        if (!root.exists() && !root.mkdirs()) {
            throw IOException("Não foi possível criar o diretório das máquinas virtuais.")
        }
    }

    fun diskFile(config: VmConfig): File {
        require(config.id.matches(Regex("[a-fA-F0-9-]{36}"))) {
            "Identificador de máquina inválido."
        }
        return File(File(root, config.id), "system.raw")
    }

    @Synchronized
    @Throws(IOException::class)
    fun prepareDisk(config: VmConfig): File {
        require(config.storageGb in 2..128) {
            "O disco virtual deve ter entre 2 e 128 GB."
        }

        val directory = File(root, config.id)
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Não foi possível criar o diretório do disco virtual.")
        }

        val disk = File(directory, "system.raw")
        val expectedLength = config.storageGb.toLong() * 1024L * 1024L * 1024L

        if (!disk.exists()) {
            RandomAccessFile(disk, "rw").use { it.setLength(expectedLength) }
        } else if (disk.length() != expectedLength) {
            throw IOException(
                "O disco existente tem tamanho diferente da configuração. " +
                    "Não será redimensionado automaticamente para evitar perda de dados."
            )
        }

        return disk
    }

    fun hasDisk(config: VmConfig): Boolean = diskFile(config).let {
        it.isFile && it.length() > 0L
    }

    @Synchronized
    fun deleteMachineStorage(config: VmConfig): Boolean {
        val directory = File(root, config.id)
        return !directory.exists() || directory.deleteRecursively()
    }
}
