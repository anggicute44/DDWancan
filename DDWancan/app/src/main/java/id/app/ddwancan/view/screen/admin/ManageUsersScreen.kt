package id.app.ddwancan.view.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.data.model.UserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    userList: List<UserItem>,
    onBackClick: () -> Unit,
    onDeleteClick: (UserItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Pengguna") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (userList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data user.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userList) { user ->
                    UserItemCard(user = user, onDelete = { onDeleteClick(user) })
                }
            }
        }
    }
}

@Composable
fun UserItemCard(user: UserItem, onDelete: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
                    Text(text = "UID: ${user.uid.take(5)}...", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            // Tombol Hapus
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus User", tint = Color.Red)
            }
        }
    }
}