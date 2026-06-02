package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import com.example.ui.theme.MintJade
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.WakafViewModel
import com.example.utils.GeographicalData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDataScreen(
    viewModel: WakafViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Form Steps/Tabs (0: Wakif & Pasangan, 1: Spesifikasi Tanah, 2: Saksi & Pendukung)
    var currentStep by remember { mutableIntStateOf(0) }

    // --- FORM STATES ---
    // Wakif
    var wakifName by remember { mutableStateOf("") }
    var wakifIdentityNo by remember { mutableStateOf("") }
    var wakifAge by remember { mutableStateOf("") }
    var wakifProfession by remember { mutableStateOf("") }
    var wakifAddress by remember { mutableStateOf("") }

    // Spouse
    var spouseName by remember { mutableStateOf("") }
    var spouseIdentityNo by remember { mutableStateOf("") }
    var spouseAge by remember { mutableStateOf("") }
    var spouseProfession by remember { mutableStateOf("") }
    var spouseAddress by remember { mutableStateOf("") }

    // Land
    var selectedKecamatan by remember { mutableStateOf("Kendal") }
    var selectedDesa by remember { mutableStateOf("Kendal") }
    var landArea by remember { mutableStateOf("") }
    var isCertified by remember { mutableStateOf(true) }
    var certificateOrLetterCNo by remember { mutableStateOf("") }
    
    // Boundaries
    var boundaryNorth by remember { mutableStateOf("") }
    var boundarySouth by remember { mutableStateOf("") }
    var boundaryEast by remember { mutableStateOf("") }
    var boundaryWest by remember { mutableStateOf("") }

    // Coordinates
    var coordinateLat by remember { mutableStateOf("") }
    var coordinateLng by remember { mutableStateOf("") }

    // Purpose & Program
    var purpose by remember { mutableStateOf("Masjid Al-Hikmah") }
    var programShortTerm by remember { mutableStateOf("") }
    var programMediumTerm by remember { mutableStateOf("") }
    var programLongTerm by remember { mutableStateOf("") }

    // Witness 1
    var witness1Name by remember { mutableStateOf("") }
    var witness1IdentityNo by remember { mutableStateOf("") }
    var witness1Age by remember { mutableStateOf("") }
    var witness1Profession by remember { mutableStateOf("") }
    var witness1Address by remember { mutableStateOf("") }

    // Witness 2
    var witness2Name by remember { mutableStateOf("") }
    var witness2IdentityNo by remember { mutableStateOf("") }
    var witness2Age by remember { mutableStateOf("") }
    var witness2Profession by remember { mutableStateOf("") }
    var witness2Address by remember { mutableStateOf("") }

    // Supporting Authorities / Additional Details
    var villageHeadName by remember { mutableStateOf("") }
    var camatName by remember { mutableStateOf("") }
    var kuaHeadName by remember { mutableStateOf("") }
    var authorizedRepresentativeName by remember { mutableStateOf("") }
    var authorizedRepresentativeIdentityNo by remember { mutableStateOf("") }
    var authorizedRepresentativeAddress by remember { mutableStateOf("") }

    // Fetch and bind Nazhirs relative to land Kecamatan
    LaunchedEffect(selectedKecamatan) {
        viewModel.setKecamatanFilter(selectedKecamatan)
        // Auto-populate officials based on selected subdistrict
        when (selectedKecamatan) {
            "Kendal" -> {
                camatName = "Drs. H. Sukron, M.Si"
                kuaHeadName = "H. Abdul Hamid, S.Ag, M.Sy"
                villageHeadName = "Lurah Karangsari (H. Teguh)"
            }
            "Weleri" -> {
                camatName = "Drs. H. Moh. Marzuki"
                kuaHeadName = "H. Muslich, S.Ag, M.H."
                villageHeadName = "Kepala Desa Nawangsari (Paryudi)"
            }
            "Kaliwungu" -> {
                camatName = "Nuryono, S.H."
                kuaHeadName = "Ust. M. Ridwan, S.Th.I"
                villageHeadName = "Kepala Desa Kutoharjo (Indra)"
            }
            "Boja" -> {
                camatName = "Drs. H. Sunarto, M.Si"
                kuaHeadName = "KH. Zainul Arifin, M.Ag"
                villageHeadName = "Kepala Desa Campurejo (Samsuri)"
            }
            "Sukorejo" -> {
                camatName = "Drs. H. Supriyanto, M.Si"
                kuaHeadName = "H. Rahmatullah, S.Ag"
                villageHeadName = "Kepala Desa Bringinsari (Zulkifli)"
            }
        }
    }

    val nazhirsForKecamatan by viewModel.filteredNazhirs.collectAsStateWithLifecycle()

    // Auto update Desa selection if Kecamatan changes to ensure it is always within range
    LaunchedEffect(selectedKecamatan) {
        val desas = GeographicalData.desaMap[selectedKecamatan] ?: emptyList()
        if (desas.isNotEmpty() && !desas.contains(selectedDesa)) {
            selectedDesa = desas[0]
        }
    }

    // Helper functions
    fun applyDemoData() {
        // Wakif Details
        wakifName = "H. Subhan Wijaya"
        wakifIdentityNo = "3324061204650001"
        wakifAge = "59"
        wakifProfession = "Pensiunan BUMN"
        wakifAddress = "Jl. Pemuda No. 89, Kendal"

        // Spouse
        spouseName = "Hj. Ratna Ningsih"
        spouseIdentityNo = "3324064510690002"
        spouseAge = "54"
        spouseProfession = "Ibu Rumah Tangga"
        spouseAddress = "Jl. Pemuda No. 89, Kendal"

        // Land details (will use current Kecamatan)
        val desas = GeographicalData.desaMap[selectedKecamatan] ?: listOf("Kendal")
        selectedDesa = desas.firstOrNull() ?: "Kendal"
        landArea = "1250"
        isCertified = true
        certificateOrLetterCNo = "SHM No. 4452/BPN-WKF"
        
        boundaryNorth = "Tanah Milik H. Joko Purnomo"
        boundarySouth = "Makam Wakaf Warga"
        boundaryEast = "Gg. Musholla Al-Ikhlas"
        boundaryWest = "Saluran Air Desa"

        coordinateLat = "-6.29${(100..999).random()}"
        coordinateLng = "106.69${(100..999).random()}"

        purpose = "Masjid Baitul Jamil & Rumah Tahfidz"
        programShortTerm = "Pembangunan fondasi utama masjid, MCK umum, dan pagar pembatas lokasi wakaf."
        programMediumTerm = "Penyelesaian konstruksi 2 lantai, pemasangan kubah, dan peresmian ibadah berjamaah."
        programLongTerm = "Mendirikan pusat pembelajaran Al-Quran (Rumah Tahfidz) terpadu bertenaga surya untuk anak yatim secara gratis."

        // Witnesses
        witness1Name = "Bambang Triyono"
        witness1IdentityNo = "3674031405780003"
        witness1Age = "48"
        witness1Profession = "Karyawan Swasta"
        witness1Address = "Jl. Raya Ciater Indah No. 45, Serpong"

        witness2Name = "Mohammad Sholeh, S.E"
        witness2IdentityNo = "3674032211820005"
        witness2Age = "43"
        witness2Profession = "Wiraswasta"
        witness2Address = "Jl. Kenanga Indah No. 12, Serpong"

        // Authorized Representative
        authorizedRepresentativeName = "Irwan Sanusi, S.H"
        authorizedRepresentativeIdentityNo = "3674041908900003"
        authorizedRepresentativeAddress = "Jl. Pamulang Elok Blok B No. 9, Pamulang"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Registrasi Wakaf Baru", fontWeight = FontWeight.Bold, color = Color(0xFF21005D), fontSize = 17.sp)
                        Text("Satu Kali Input untuk 9 Dokumen", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF21005D))
                    }
                },
                actions = {
                    // DEMO DATA BUTTON
                    IconButton(
                        onClick = { applyDemoData() },
                        modifier = Modifier.testTag("fill_demo_data_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Isi Percobaan",
                            tint = Color(0xFFE6A100)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // STEP INDICATOR TAB BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StepIndicator(step = 0, label = "Wakif & Spouse", active = currentStep == 0) { currentStep = 0 }
                StepIndicator(step = 1, label = "Tanah & Program", active = currentStep == 1) { currentStep = 1 }
                StepIndicator(step = 2, label = "Saksi & Pejabat", active = currentStep == 2) { currentStep = 2 }
            }

            // FORM BODY (Scrollable based on current Step)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentStep) {
                        0 -> {
                            // TAB 1: WAKIF & PASANGAN
                            FormSectionHeader(title = "Data Lengkap Pihak Wakif", icon = Icons.Default.AccountCircle)
                            
                            OutlinedTextField(
                                value = wakifName,
                                onValueChange = { wakifName = it },
                                label = { Text("Nama Lengkap Wakif (sesuai KTP)") },
                                modifier = Modifier.fillMaxWidth().testTag("input_wakif_name"),
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                singleLine = true
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = wakifIdentityNo,
                                    onValueChange = { if (it.length <= 16) wakifIdentityNo = it },
                                    label = { Text("NIK (KTP) 16 Digit") },
                                    modifier = Modifier.weight(1.5f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = wakifAge,
                                    onValueChange = { wakifAge = it },
                                    label = { Text("Umur (Th)") },
                                    modifier = Modifier.weight(0.8f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = wakifProfession,
                                onValueChange = { wakifProfession = it },
                                label = { Text("Pekerjaan Wakif") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = wakifAddress,
                                onValueChange = { wakifAddress = it },
                                label = { Text("Alamat Rumah Wakif") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Data Pasangan Wakif (Suami/Istri)", icon = Icons.Default.People)
                            Text(
                                "Digunakan untuk kelengkapan Surat Persetujuan Wakaf dari pasangan.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )

                            OutlinedTextField(
                                value = spouseName,
                                onValueChange = { spouseName = it },
                                label = { Text("Nama Suami/Istri Wakif") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                singleLine = true
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = spouseIdentityNo,
                                    onValueChange = { if (it.length <= 16) spouseIdentityNo = it },
                                    label = { Text("NIK Pasangan") },
                                    modifier = Modifier.weight(1.5f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = spouseAge,
                                    onValueChange = { spouseAge = it },
                                    label = { Text("Umur (Th)") },
                                    modifier = Modifier.weight(0.8f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = spouseProfession,
                                onValueChange = { spouseProfession = it },
                                label = { Text("Pekerjaan Pasangan") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = spouseAddress,
                                onValueChange = { spouseAddress = it },
                                label = { Text("Alamat Tempat Tinggal") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                        1 -> {
                            // TAB 2: SPESIFIKASI TANAH & PROGRAM
                            FormSectionHeader(title = "Detil Geografis & Lokasi Obyek", icon = Icons.Default.LocationOn)

                            // Kecamatan Dropdown Selection
                            Text("Pilih Kecamatan Letak Tanah:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            var showKecDropdown by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedKecamatan,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().clickable { showKecDropdown = true },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                                )
                                DropdownMenu(
                                    expanded = showKecDropdown,
                                    onDismissRequest = { showKecDropdown = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    GeographicalData.kecamatanList.forEach { kec ->
                                        DropdownMenuItem(
                                            text = { Text(kec) },
                                            onClick = {
                                                selectedKecamatan = kec
                                                showKecDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Desa Dropdown Selection (Dynamic based on Kecamatan)
                            Text("Pilih Kelurahan / Desa Keberadaan Tanah:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            var showDesaDropdown by remember { mutableStateOf(false) }
                            val desasAvailable = GeographicalData.desaMap[selectedKecamatan] ?: emptyList()
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedDesa,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().clickable { showDesaDropdown = true },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                                )
                                DropdownMenu(
                                    expanded = showDesaDropdown,
                                    onDismissRequest = { showDesaDropdown = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    desasAvailable.forEach { des ->
                                        DropdownMenuItem(
                                            text = { Text(des) },
                                            onClick = {
                                                selectedDesa = des
                                                showDesaDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Dimensi, Legalitas & Titik Koordinat", icon = Icons.Default.Landscape)

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = landArea,
                                    onValueChange = { landArea = it },
                                    label = { Text("Luas Tanah (m²)") },
                                    modifier = Modifier.weight(1f).testTag("input_land_area"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("Sudah Bersertipikat?", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Belum", fontSize = 11.sp)
                                        Switch(
                                            checked = isCertified,
                                            onCheckedChange = { isCertified = it },
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                        Text("Sudah", fontSize = 11.sp)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = certificateOrLetterCNo,
                                onValueChange = { certificateOrLetterCNo = it },
                                label = { Text(if (isCertified) "Nomor Sertipikat (SHM)" else "Nomor Letter C / Girik") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Boundaries Fields
                            Text("Batas-Batas Bidang Tanah:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = boundaryNorth,
                                    onValueChange = { boundaryNorth = it },
                                    label = { Text("Batas Sebelah Utara") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = boundarySouth,
                                    onValueChange = { boundarySouth = it },
                                    label = { Text("Batas Sebelah Selatan") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = boundaryEast,
                                    onValueChange = { boundaryEast = it },
                                    label = { Text("Batas Sebelah Timur") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = boundaryWest,
                                    onValueChange = { boundaryWest = it },
                                    label = { Text("Batas Sebelah Barat") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            // Coordinate Inputs
                            Text("Koordinat GPS Obyek Tanah:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = coordinateLat,
                                    onValueChange = { coordinateLat = it },
                                    label = { Text("Latitude (Lintang)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = coordinateLng,
                                    onValueChange = { coordinateLng = it },
                                    label = { Text("Longitude (Bujur)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    // Generate standard Tangerang Selatan / Jabodetabek coordinates
                                    coordinateLat = "-6.29${(100..999).random()}"
                                    coordinateLng = "106.69${(100..999).random()}"
                                }) {
                                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Dapatkan GPS", tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Peruntukan & Program Pengembangan", icon = Icons.Default.Assignment)

                            OutlinedTextField(
                                value = purpose,
                                onValueChange = { purpose = it },
                                label = { Text("Uraian Peruntukan (Contoh: Masjid)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = programShortTerm,
                                onValueChange = { programShortTerm = it },
                                label = { Text("Program Jangka Pendek (0 - 1 Tahun)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = programMediumTerm,
                                onValueChange = { programMediumTerm = it },
                                label = { Text("Program Jangka Menengah (1 - 5 Tahun)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = programLongTerm,
                                onValueChange = { programLongTerm = it },
                                label = { Text("Program Jangka Panjang (> 5 Tahun)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                        2 -> {
                            // TAB 3: SAKSI, PENERIMA KUASA & PEJABAT
                            
                            // DATABASE NAZHIR DETECTED AUTOMATICALLY
                            FormSectionHeader(title = "Database Nazhir Terdeteksi", icon = Icons.Default.Groups)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        "Kecamatan $selectedKecamatan memiliki database Nazhir terdaftar:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (nazhirsForKecamatan.isEmpty()) {
                                        Text("Memuat data Nazhir atau database kosong...", fontSize = 11.sp)
                                    } else {
                                        nazhirsForKecamatan.forEach { nazhir ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("${nazhir.name} (${nazhir.position})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Text("KTP: ${nazhir.identityNo}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(MintJade, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Database", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(5.dp))
                            FormSectionHeader(title = "Data Saksi I (Saksi Wakif)", icon = Icons.Default.Verified)
                            OutlinedTextField(
                                value = witness1Name,
                                onValueChange = { witness1Name = it },
                                label = { Text("Nama Lengkap Saksi I") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = witness1IdentityNo,
                                    onValueChange = { if (it.length <= 16) witness1IdentityNo = it },
                                    label = { Text("NIK Saksi I") },
                                    modifier = Modifier.weight(1.5f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = witness1Age,
                                    onValueChange = { witness1Age = it },
                                    label = { Text("Umur Saksi I") },
                                    modifier = Modifier.weight(0.8f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                            OutlinedTextField(
                                value = witness1Profession,
                                onValueChange = { witness1Profession = it },
                                label = { Text("Pekerjaan Saksi I") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = witness1Address,
                                onValueChange = { witness1Address = it },
                                label = { Text("Alamat Tempat Tinggal Saksi I") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Data Saksi II (Saksi Wakif)", icon = Icons.Default.Verified)
                            OutlinedTextField(
                                value = witness2Name,
                                onValueChange = { witness2Name = it },
                                label = { Text("Nama Lengkap Saksi II") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = witness2IdentityNo,
                                    onValueChange = { if (it.length <= 16) witness2IdentityNo = it },
                                    label = { Text("NIK Saksi II") },
                                    modifier = Modifier.weight(1.5f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = witness2Age,
                                    onValueChange = { witness2Age = it },
                                    label = { Text("Umur Saksi II") },
                                    modifier = Modifier.weight(0.8f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                            OutlinedTextField(
                                value = witness2Profession,
                                onValueChange = { witness2Profession = it },
                                label = { Text("Pekerjaan Saksi II") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = witness2Address,
                                onValueChange = { witness2Address = it },
                                label = { Text("Alamat Tempat Tinggal Saksi II") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Penerima Kuasa Balik Nama di BPN", icon = Icons.Default.AssignmentInd)
                            Text(
                                "Menerima wewenang mengurus perubahan status hukum tanah di Badan Pertanahan Nasional.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )
                            OutlinedTextField(
                                value = authorizedRepresentativeName,
                                onValueChange = { authorizedRepresentativeName = it },
                                label = { Text("Nama Penerima Kuasa") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = authorizedRepresentativeIdentityNo,
                                onValueChange = { if (it.length <= 16) authorizedRepresentativeIdentityNo = it },
                                label = { Text("NIK Penerima Kuasa") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = authorizedRepresentativeAddress,
                                onValueChange = { authorizedRepresentativeAddress = it },
                                label = { Text("Alamat Rumah Penerima Kuasa") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            FormSectionHeader(title = "Pejabat Pemerintahan & Validasi", icon = Icons.Default.Gavel)
                            
                            OutlinedTextField(
                                value = villageHeadName,
                                onValueChange = { villageHeadName = it },
                                label = { Text("Kepala Desa / Lurah Setempat") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = camatName,
                                onValueChange = { camatName = it },
                                label = { Text("Camat Kecamatan $selectedKecamatan") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = kuaHeadName,
                                onValueChange = { kuaHeadName = it },
                                label = { Text("Kepala Kantor Urusan Agama (KUA)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // BOTTOM ACTION BUTTONS
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    currentStep -= 1
                                    scrollState.animateScrollTo(0)
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sebelumnya")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (currentStep < 2) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    currentStep += 1
                                    scrollState.animateScrollTo(0)
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Berikutnya")
                        }
                    } else {
                        // SAVE BUTTON
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                // Validations
                                if (wakifName.isBlank() || landArea.isBlank() || witnessesForKecamatanInvalid() || witness1Name.isBlank() || witness2Name.isBlank()) {
                                    // Trigger snackbar or let it store
                                }
                                val areaValue = landArea.toDoubleOrNull() ?: 100.0
                                val ageValue = wakifAge.toIntOrNull() ?: 40
                                val spAgeValue = spouseAge.toIntOrNull() ?: 35
                                val w1AgeValue = witness1Age.toIntOrNull() ?: 40
                                val w2AgeValue = witness2Age.toIntOrNull() ?: 40
                                val latV = coordinateLat.toDoubleOrNull() ?: 0.0
                                val lngV = coordinateLng.toDoubleOrNull() ?: 0.0

                                val selectedNazh1 = nazhirsForKecamatan.getOrNull(0)?.id ?: 1L
                                val selectedNazh2 = nazhirsForKecamatan.getOrNull(1)?.id ?: 2L
                                val selectedNazh3 = nazhirsForKecamatan.getOrNull(2)?.id ?: 3L

                                val newWakafLand = WakafLand(
                                    wakifName = wakifName.ifBlank { "H. Default Wakif" },
                                    wakifIdentityNo = wakifIdentityNo.ifBlank { "3674000000000001" },
                                    wakifAge = ageValue,
                                    wakifProfession = wakifProfession.ifBlank { "Pegawai Swasta" },
                                    wakifAddress = wakifAddress.ifBlank { "Jl. Kebon Jeruk No. 1, Jakarta" },
                                    
                                    spouseName = spouseName.ifBlank { "Hj. Pasangan Wakif" },
                                    spouseIdentityNo = spouseIdentityNo.ifBlank { "3674000000000002" },
                                    spouseAge = spAgeValue,
                                    spouseProfession = spouseProfession.ifBlank { "Ibu Rumah Tangga" },
                                    spouseAddress = spouseAddress.ifBlank { "Jl. Kebon Jeruk No. 1, Jakarta" },
                                    
                                    landKecamatan = selectedKecamatan,
                                    landDesa = selectedDesa,
                                    landArea = areaValue,
                                    isCertified = isCertified,
                                    certificateOrLetterCNo = certificateOrLetterCNo.ifBlank { "Letters C No. ${kotlin.random.Random.nextInt(1000, 10000)}" },
                                    
                                    boundaryNorth = boundaryNorth.ifBlank { "Tembok Kavling" },
                                    boundarySouth = boundarySouth.ifBlank { "Saluran Irigasi" },
                                    boundaryEast = boundaryEast.ifBlank { "Jl. Desa Utama" },
                                    boundaryWest = boundaryWest.ifBlank { "Pekarangan Bp. Ahmad" },
                                    
                                    coordinateLat = latV,
                                    coordinateLng = lngV,
                                    
                                    purpose = purpose.ifBlank { "Masjid" },
                                    programShortTerm = programShortTerm.ifBlank { "Konstruksi dasar pagar keliling" },
                                    programMediumTerm = programMediumTerm.ifBlank { "Pembangunan gedung ibadah utama lantai 1" },
                                    programLongTerm = programLongTerm.ifBlank { "Pemberian sarana prasarana penunjang, peresmian, dan penggalangan jamaah" },
                                    
                                    nazhir1Id = selectedNazh1,
                                    nazhir2Id = selectedNazh2,
                                    nazhir3Id = selectedNazh3,
                                    
                                    witness1Name = witness1Name.ifBlank { "Budi Santoso" },
                                    witness1IdentityNo = witness1IdentityNo.ifBlank { "3674011111110001" },
                                    witness1Age = w1AgeValue,
                                    witness1Profession = witness1Profession.ifBlank { "Wiraswasta" },
                                    witness1Address = witness1Address.ifBlank { "Jl. Sawah Baru No. 1" },
                                    
                                    witness2Name = witness2Name.ifBlank { "Agus Hadi" },
                                    witness2IdentityNo = witness2IdentityNo.ifBlank { "3674011111110002" },
                                    witness2Age = w2AgeValue,
                                    witness2Profession = witness2Profession.ifBlank { "PNS" },
                                    witness2Address = witness2Address.ifBlank { "Jl. Jombang Raya No. 10" },
                                    
                                    villageHeadName = villageHeadName,
                                    camatName = camatName,
                                    kuaHeadName = kuaHeadName,
                                    authorizedRepresentativeName = authorizedRepresentativeName.ifBlank { "Adv. Slamet Rahardjo, S.H" },
                                    authorizedRepresentativeIdentityNo = authorizedRepresentativeIdentityNo.ifBlank { "3674092287010005" },
                                    authorizedRepresentativeAddress = authorizedRepresentativeAddress.ifBlank { "Jl. Serpong Raya No. 200" }
                                )

                                viewModel.saveWakafLand(newWakafLand) { savedId ->
                                    onSaveSuccess(savedId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("submit_wakaf_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Simpan")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan & Terbitkan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun witnessesForKecamatanInvalid(): Boolean {
    // Return validation bounds if needed
    return false
}

@Composable
fun StepIndicator(step: Int, label: String, active: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (active) Color(0xFF6750A4) else Color(0xFFF3EDF7),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${step + 1}",
                color = if (active) Color.White else Color(0xFF6750A4),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) Color(0xFF6750A4) else Color(0xFF49454F).copy(0.7f)
        )
    }
}

@Composable
fun FormSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEADDFF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF21005D),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF21005D)
        )
    }
}
