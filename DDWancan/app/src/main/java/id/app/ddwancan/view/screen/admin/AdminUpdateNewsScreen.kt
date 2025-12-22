package id.app.ddwancan.view.screen.admin

import androidx.compose.foundation.background
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
            TopAppBar(
                title = { Text("Admin - Update Berita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Simple radio list for presets with hardcoded names
            val presetNames = listOf(
                "Apple News",
                "Tesla News",
                "Business US News",
                "Techcrunch News",
                "Wall Street News"
            )
            
            // Get current selected preset name for confirmation dialog
            var selectedPresetName by remember { mutableStateOf("Apple News") }
            
            Column(modifier = Modifier.fillMaxWidth()) {
                val allPresets = id.app.ddwancan.viewmodel.AdminNewsViewModel.Preset.values()
                for ((index, preset) in allPresets.withIndex()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = preset == selectedPreset,
                            onClick = { 
                                selectedPreset = preset
                                selectedPresetName = if (index < presetNames.size) presetNames[index] else preset.label
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (index < presetNames.size) presetNames[index] else preset.label,
                            color = MaterialTheme.colorScheme.onBackground
                        )
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
                    text = { Text("Anda akan mengupdate semua berita dari $selectedPresetName, lanjutkan?") },
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

            Text(
                text = status,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
