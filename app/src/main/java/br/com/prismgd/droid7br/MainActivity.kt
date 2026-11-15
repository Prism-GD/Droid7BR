package br.com.prismgd.droid7br

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Droid7BRTheme {
                Droid7BRHome()
            }
        }
    }
}

@Composable
private fun Droid7BRTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

@Composable
private fun Droid7BRHome() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Droid7BR",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Seu Android virtual.",
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
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Android 7.1.2 Nougat",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "API 25 • GApps planejado • Magisk planejado"
                    )
                    Text(
                        text = "A imagem do sistema ainda não foi integrada.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Estado da máquina virtual",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "QEMU ainda não configurado",
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Nesta versão inicial, a tela do aplicativo já está preparada. A inicialização da VM será adicionada nas próximas etapas."
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Android virtual — em desenvolvimento")
            }

            Text(
                text = "Droid7BR • Versão 0.1.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
