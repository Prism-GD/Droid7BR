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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    Text("Motor QEMU: em preparação", color = MaterialTheme.colorScheme.error)
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
                        Text(
                            "Desligada • O sistema convidado ainda não foi conectado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { }) {
                                Text("Iniciar")
                            }
                            OutlinedButton(onClick = {
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
