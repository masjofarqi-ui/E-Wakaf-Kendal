package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WakafLand
import com.example.ui.theme.MintJade
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.WakafViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarDataScreen(
    viewModel: WakafViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToInput: () -> Unit
) {
    val lands by viewModel.wakafLands.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    // Deletion states
    var landToDelete by remember { mutableStateOf<WakafLand?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val filteredList = remember(lands, searchQuery) {
        if (searchQuery.isBlank()) {
            lands
        } else {
            lands.filter { land ->
                land.wakifName.contains(searchQuery, ignoreCase = true) ||
                        land.purpose.contains(searchQuery, ignoreCase = true) ||
                        land.landDesa.contains(searchQuery, ignoreCase = true) ||
                        land.landKecamatan.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Arsip Registrasi Wakaf", fontWeight = FontWeight.Bold, color = Color(0xFF21005D), fontSize = 17.sp)
                        Text("${filteredList.size} Bidang Terdaftar", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF21005D))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToInput,
                containerColor = Color(0xFF6750A4),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Registrasi")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari Wakif, peruntukan Desa, Kecamatan...", fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("search_wakaf_input"),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color(0xFF6750A4)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF49454F))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color(0xFFCAC4D0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // ARSIP LIST
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Hasil Pencarian Tidak Ditemukan" else "Belum Ada Arsip Wakaf",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Coba kata kunci pencarian yang lain." else "Mulailah mendaftarkan wakaf tanah Anda melalui tombol (+) di bawah ini.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("wakaf_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { land ->
                        ArsipWakafCard(
                            land = land,
                            onClick = { onNavigateToDetail(land.id) },
                            onDeleteClick = {
                                landToDelete = land
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // DOUBLE-CONFIRM DELETION DIALOG
        if (showDeleteDialog && landToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    landToDelete = null
                },
                title = { Text("Konfirmasi Hapus Arsip", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Apakah Anda yakin ingin menghapus seluruh berkas dokumen perwakafan atas nama Wakif: ${landToDelete?.wakifName}? Berkas yang dihapus tidak dapat dipulihkan."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            landToDelete?.let {
                                viewModel.deleteWakafLand(it.id)
                            }
                            showDeleteDialog = false
                            landToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Ya, Hapus Permanen", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        landToDelete = null
                    }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun ArsipWakafCard(
    land: WakafLand,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
    val dateStr = formatter.format(Date(land.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("arsip_card_${land.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (land.isCertified) WarmGold.copy(0.15f) else MintJade.copy(0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (land.isCertified) "SHM" else "Letter C",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (land.isCertified) WarmGold else MintJade
                            )
                        }
                        
                        Text(
                            text = land.certificateOrLetterCNo,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = land.wakifName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // DELETE ARCHIVE ICON BUTTON
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp).testTag("delete_button_${land.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Arsip",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info Grid
                Column {
                    Text(
                        text = "Lokasi: Kel. ${land.landDesa}, Kec. ${land.landKecamatan}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Dimensi / Peruntukan: ${land.landArea} m² • ${land.purpose}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Chevron indicating detail click
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Lihat 9 Dokumen",
                    tint = MintJade,
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registrasi: $dateStr",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Light
                )
                
                Text(
                    text = "9 Berkas Siap",
                    fontSize = 10.sp,
                    color = MintJade,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
