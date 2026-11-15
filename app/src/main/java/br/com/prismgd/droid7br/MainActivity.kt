package br.com.prismgd.droid7br

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.prismgd.droid7br.vm.DeviceCapabilities
import br.com.prismgd.droid7br.vm.NativeRuntime
import br.com.prismgd.droid7br.vm.QemuRuntime
import br.com.prismgd.droid7br.vm.VmStorageManager
import br.com.prismgd.droid7br.vm.VmConfig
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Droid7BRTheme {
                Droid7BRHome(
                    initialMachines = loadMachines(this),
                    onMachinesChanged = { saveMachines(this, it) }
                )
            }
        }
    }
}

@Composable
private fun Droid7BRTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(), content = content)
}

@Composable
private fun Droid7BRHome(
    initialMachines: List<VmConfig>,
    onMachinesChanged: (List<VmConfig>) -> Unit
) {
    val machines = remember {
        mutableStateListOf<VmConfig>().apply { addAll(initialMachines) }
    }
    val deviceCapabilities = remember { DeviceCapabilities.detect() }
    val context = LocalContext.current
    val storageManager = remember(context) { VmStorageManager(context) }
    val qemuRuntime = remember(context) { QemuRuntime(context) }
    var storageMessage by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var qemuProbeMessage by remember { androidx.compose.runtime.mutableStateOf("Teste ainda não executado.") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Droid7BR", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Seus celulares Android virtuais",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Gerenciador de máquinas", style = MaterialTheme.typography.titleLarge)
                    Text("Crie instâncias independentes e mantenha cada configuração salva.")
                    Text("QEMU ARM64: ${if (qemuRuntime.isAvailable()) "Binário instalado" else "Aguardando compilação pelo GitHub Actions"}", color = if (qemuRuntime.isAvailable()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Text("Camada nativa: ${NativeRuntime.engineStatus()}")
                    OutlinedButton(onClick = {
                        Thread({
                            val result = qemuRuntime.version()
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                qemuProbeMessage = result
                            }
                        }, "Droid7BR-QEMU-probe").start()
                    }, enabled = qemuRuntime.isAvailable()) {
                        Text("Testar QEMU")
                    }
                    Text(qemuProbeMessage, style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Compatibilidade do aparelho", style = MaterialTheme.typography.titleMedium)
                    Text("Arquitetura: ${deviceCapabilities.primaryAbi}")
                    Text("ARM64: ${if (deviceCapabilities.isArm64) "Sim" else "Não"}")
                    Text("Dispositivo KVM: ${if (deviceCapabilities.kvmDevicePresent) "Encontrado" else "Não encontrado"}")
                    Text(
                        "Acesso ao KVM pelo app: ${if (deviceCapabilities.kvmLikelyUsableByApp) "Possível (ainda precisa ser validado pelo QEMU)" else "Não disponível"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Se não houver KVM acessível, será necessário avaliar QEMU por emulação de software.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Minhas máquinas", style = MaterialTheme.typography.titleLarge)
                Text("${machines.size} criada(s)", style = MaterialTheme.typography.labelLarge)
            }

            machines.forEach { machine ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(machine.name, style = MaterialTheme.typography.titleMedium)
                        Text("Android ${machine.androidVersion} • API ${machine.apiLevel}")
                        Text("${machine.ramMb} MB RAM • ${machine.cpuCores} CPU • ${machine.storageGb} GB")
                        Text(if (storageManager.hasDisk(machine)) "Disco RAW preparado" else "Disco virtual ainda não criado")
                        OutlinedButton(onClick = {
                            storageMessage = try {
                                val disk = storageManager.prepareDisk(machine)
                                "Disco preparado: ${disk.name} (${machine.storageGb} GB virtuais)."
                            } catch (error: Exception) {
                                "Falha ao preparar disco: ${error.message ?: "erro desconhecido"}"
                            }
                        }) {
                            Text("Preparar armazenamento")
                        }
                        Text(
                            "Desligada • O sistema convidado ainda não foi conectado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { }, enabled = false) {
                                Text("Iniciar — em breve")
                            }
                            OutlinedButton(onClick = {
                                storageManager.deleteMachineStorage(machine)
                                machines.remove(machine)
                                onMachinesChanged(machines.toList())
                            }) {
                                Text("Excluir")
                            }
                        }
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val next = VmConfig(
                        name = if (machines.isEmpty()) "Android Nougat" else "Android Nougat ${machines.size + 1}"
                    )
                    machines.add(next)
                    onMachinesChanged(machines.toList())
                }
            ) {
                Text("＋ Criar celular virtual")
            }

            storageMessage?.let { message ->
                Text(message, style = MaterialTheme.typography.bodySmall)
            }

            Text(
                "Primeira máquina planejada: Android 7.1.2 Nougat com GApps e Magisk.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Droid7BR • Protótipo do gerenciador",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun loadMachines(context: Context): List<VmConfig> {
    val stored = context.getSharedPreferences("droid7br", Context.MODE_PRIVATE)
        .getString("machines", null) ?: return listOf(VmConfig())

    return try {
        val array = JSONArray(stored)
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            VmConfig(
                id = item.optString("id"),
                name = item.optString("name", "Android Nougat"),
                androidVersion = item.optString("androidVersion", "7.1.2"),
                apiLevel = item.optInt("apiLevel", 25),
                ramMb = item.optInt("ramMb", 1536),
                storageGb = item.optInt("storageGb", 8),
                cpuCores = item.optInt("cpuCores", 2)
            )
        }
    } catch (_: Exception) {
        listOf(VmConfig())
    }
}

private fun saveMachines(context: Context, machines: List<VmConfig>) {
    val array = JSONArray()
    machines.forEach { machine ->
        array.put(
            JSONObject().apply {
                put("id", machine.id)
                put("name", machine.name)
                put("androidVersion", machine.androidVersion)
                put("apiLevel", machine.apiLevel)
                put("ramMb", machine.ramMb)
                put("storageGb", machine.storageGb)
                put("cpuCores", machine.cpuCores)
            }
        )
    }
    context.getSharedPreferences("droid7br", Context.MODE_PRIVATE)
        .edit()
        .putString("machines", array.toString())
        .apply()
}
