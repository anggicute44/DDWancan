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

    var apiKey by remember { mutableStateOf("") }

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
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key NewsAPI.org") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.updateNews(apiKey)
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Updating..." else "Update Berita dari API")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = status)
        }
    }
}
