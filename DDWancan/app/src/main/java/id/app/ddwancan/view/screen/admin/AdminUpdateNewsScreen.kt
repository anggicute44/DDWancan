package id.app.ddwancan.view.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.app.ddwancan.viewmodel.AdminNewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUpdateNewsScreen(
    viewModel: AdminNewsViewModel,
    onBack: () -> Unit
) {
    val isLoading by viewModel.isLoading
    val status by viewModel.status

    // Preset selection state
    var selectedPreset by remember { mutableStateOf(AdminNewsViewModel.Preset.EVERYTHING_APPLE) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin - Update Berita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Pilih jenis feed yang ingin di-update:")
            Spacer(modifier = Modifier.height(8.dp))

            // Simple radio list for presets
            Column(modifier = Modifier.fillMaxWidth()) {
                for (preset in id.app.ddwancan.viewmodel.AdminNewsViewModel.Preset.values()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = preset == selectedPreset,
                            onClick = { selectedPreset = preset }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(preset.label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showConfirm = true },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Updating..." else "Update Selected Feed")
            }

            if (showConfirm) {
                AlertDialog(
                    onDismissRequest = { showConfirm = false },
                    title = { Text("Konfirmasi Update") },
                    text = { Text("Anda akan meng-update feed: ${selectedPreset.label}. Lanjutkan?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirm = false
                            viewModel.updateNewsWithPreset(selectedPreset)
                        }) { Text("Ya") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirm = false }) { Text("Batal") }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = status)
        }
    }
}
