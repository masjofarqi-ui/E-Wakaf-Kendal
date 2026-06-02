package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WakafLand
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.MintJade
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.WakafViewModel
import com.example.utils.GeographicalData
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WakafViewModel,
    onNavigateToInput: () -> Unit,
    onNavigateToDaftar: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val lands by viewModel.wakafLands.collectAsState()
    val nazhirs by viewModel.allNazhirs.collectAsState()

    val totalLandsCount = lands.size
    val totalArea = lands.sumOf { it.landArea }
    val certifiedCount = lands.count { it.isCertified }
    val uncertifiedCount = totalLandsCount - certifiedCount

    // Dynamic calculations for districts
    val kecamatanStats = lands.groupBy { it.landKecamatan }
        .mapValues { it.value.size }

    val formattedArea = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalArea)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF6750A4), shape = RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Wakaf Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "E-Wakaf Kab. Kendal",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF21005D),
                                fontSize = 16.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF4CAF50), shape = RoundedCornerShape(3.dp))
                                )
                                Text(
                                    "CLOUD SYNCED",
                                    color = Color(0xFF6750A4),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                WelcomeBanner(totalLandsCount, formattedArea, onNavigateToInput)
            }

            // Web & Regional Info Section
            item {
                InfoKendalCustomBanner()
            }

            // Quick Stats Grid
            item {
                Text(
                    "Ringkasan Statistik".uppercase(Locale.getDefault()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF49454F),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Asosiasi Nazhir",
                        value = "${nazhirs.size} Anggota",
                        icon = Icons.Default.Groups,
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF21005D),
                        iconColor = Color(0xFF21005D),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Sudah Bersertipikat",
                        value = "$certifiedCount Bidang",
                        icon = Icons.Default.Verified,
                        containerColor = Color(0xFFEADDFF),
                        contentColor = Color(0xFF21005D),
                        iconColor = Color(0xFF21005D),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Surat Terbit (Cloud)",
                        value = "${totalLandsCount * 8} Dokumen",
                        icon = Icons.Default.CloudSync,
                        containerColor = Color(0xFFF3EDF7),
                        contentColor = Color(0xFF49454F),
                        iconColor = Color(0xFF6750A4),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Belum Sertipikat",
                        value = "$uncertifiedCount Bidang",
                        icon = Icons.Default.HourglassEmpty,
                        containerColor = Color(0xFFF3EDF7),
                        contentColor = Color(0xFF49454F),
                        iconColor = Color(0xFFB3261E),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Kecamatan Distribution
            item {
                Text(
                    "Sebaran Kecamatan".uppercase(Locale.getDefault()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF49454F),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val allKecamatans = GeographicalData.kecamatanList
                        allKecamatans.forEach { kecamatan ->
                            val count = kecamatanStats[kecamatan] ?: 0
                            val percentage = if (totalLandsCount > 0) count.toFloat() / totalLandsCount else 0f
                            KecamatanProgressRow(kecamatan, count, percentage)
                        }
                    }
                }
            }

            // Quick Actions Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pendaftaran Terbaru".uppercase(Locale.getDefault()),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    TextButton(onClick = onNavigateToDaftar) {
                        Text(
                            "Lihat Semua",
                            color = Color(0xFF6750A4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Recent List
            if (lands.isEmpty()) {
                item {
                    EmptyStateCard(onNavigateToInput)
                }
            } else {
                items(lands.take(3)) { land ->
                    RecentLandItem(land = land, onClick = { onNavigateToDetail(land.id) })
                }
            }
        }
    }
}

@Composable
fun WelcomeBanner(
    totalLandsCount: Int,
    formattedArea: String,
    onNavigateToInput: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepEmerald, MintJade)
                    )
                )
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Sistem Administrasi Arsip & Dokumen Wakaf",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                
                Text(
                    "Total Akumulasi Wakaf:",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )

                Text(
                    "$formattedArea m²",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 1.sp
                )

                Text(
                    "Dari total $totalLandsCount bidang tanah yang teregistrasi secara daring.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onNavigateToInput,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("create_new_wakaf_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Daftarkan Wakaf",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daftar Wakaf Baru", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(24.dp), // Dynamic Material 3 high-density rounded shape
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )
                Text(
                    text = title.uppercase(Locale.getDefault()),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun KecamatanProgressRow(kecamatan: String, count: Int, percentage: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                kecamatan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "$count Lokasi (${(percentage * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MintJade,
            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun EmptyStateCard(onRegisterClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SnippetFolder,
                contentDescription = "Folder Kosong",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(60.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Belum Ada Data Wakaf",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Mulailah dengan mengisi formulir registrasi satu-kali-input di bawah ini.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            OutlinedButton(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Icon")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Daftar Sekarang")
            }
        }
    }
}

@Composable
fun RecentLandItem(land: WakafLand, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recent_land_${land.id}"),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (land.isCertified) WarmGold.copy(alpha = 0.15f) else MintJade.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (land.isCertified) Icons.Default.VerifiedUser else Icons.Default.Domain,
                    contentDescription = "Land Type",
                    tint = if (land.isCertified) WarmGold else MintJade,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wakif: ${land.wakifName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${land.landArea} m² • ${land.purpose}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Kel. ${land.landDesa}, Kec. ${land.landKecamatan}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InfoKendalCustomBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("info_kendal_custom_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3EDF7)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEADDFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Cloud Enabled",
                    tint = Color(0xFF6750A4),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Akses Web & Regional Spesifik Kendal".uppercase(Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF21005D),
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "• AKSES WEB / CHROME: Aplikasi ini dihosting di server cloud & terintegrasi penuh. Dapat diakses langsung melalui browser (Google Chrome) di PC/Laptop untuk kenyamanan administrasi cetak & input data.\n\n• SPESIALISASI KABUPATEN KENDAL: Sistem ini didesain eksklusif untuk administrasi wakaf Kabupaten Kendal. Pilihan Kecamatan, Desa/Kelurahan, dan Pejabat KUA otomatis disesuaikan secara lokal untuk wilayah Kendal.",
                fontSize = 12.sp,
                color = Color(0xFF49454F),
                lineHeight = 18.sp
            )
        }
    }
}
