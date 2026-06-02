package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.WakafViewModel
import com.example.utils.DocumentGenerator
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    viewModel: WakafViewModel,
    landId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    
    // Dynamic lookups
    val lands by viewModel.wakafLands.collectAsState()
    val land = lands.find { it.id == landId }

    var nazhirsList by remember { mutableStateOf<List<Nazhir>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(1) } // 1 to 9
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Fetch custom Nazhirs for this specific Wakaf land
    LaunchedEffect(land) {
        land?.let { l ->
            val ids = listOf(l.nazhir1Id, l.nazhir2Id, l.nazhir3Id)
            viewModel.fetchNazhirsByIds(ids) { result ->
                nazhirsList = result
            }
        }
    }

    if (land == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dokumen Tidak Ditemukan") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Kembali")
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Arsip Wakaf dengan ID $landId tidak ditemukan.")
            }
        }
        return
    }

    // List of tabs corresponding to the 9 documents
    val documentTabs = listOf(
        Pair(1, "SPW (Ikrar)"),
        Pair(2, "Permohonan KUA"),
        Pair(3, "Program Kerja"),
        Pair(4, "Kekayaan Nazhir"),
        Pair(5, "Pernyataan Audit"),
        Pair(6, "Kuasa Balik Nama"),
        Pair(7, "Bebas Sengketa"),
        Pair(8, "Suami/Istri"),
        Pair(9, "Piagam Wakif")
    )

    // Current formatted HTML
    val currentHtml = remember(selectedTab, land, nazhirsList) {
        DocumentGenerator.generateHtml(selectedTab, land, nazhirsList)
    }

    // Web view state resets when html updates
    LaunchedEffect(currentHtml) {
        webViewInstance?.loadDataWithBaseURL(null, currentHtml, "text/html", "UTF-8", null)
    }

    // Function to handle printing (PDF Generation)
    fun printDocument() {
        webViewInstance?.let { webView ->
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager != null) {
                val jobName = "Wakaf_${selectedTab}_${land.wakifName.replace(" ", "_")}"
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id1", "wakaf_print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print(jobName, printAdapter, printAttributes)
            } else {
                Toast.makeText(context, "Sistem cetak tidak didukung di perangkat ini", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Dokumen belum siap untuk dicetak", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle Microsoft Word export (Shared as .doc formatted text)
    fun exportToWord() {
        val rawTextContents = DocumentGenerator.generateRawText(selectedTab, land, nazhirsList)
        try {
            val fileName = "Wakaf_${selectedTab}_${land.wakifName.replace(" ", "_")}.doc"
            val tempFile = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(tempFile)
            outputStream.write(rawTextContents.toByteArray())
            outputStream.flush()
            outputStream.close()

            val authority = "${context.packageName}.fileprovider"
            // Simple robust Intent share
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/msword"
                putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile))
                putExtra(Intent.EXTRA_SUBJECT, "Dokumen Wakaf: ${DocumentGenerator.getDocumentTitle(selectedTab)}")
                putExtra(Intent.EXTRA_TEXT, "Terlampir draf dokumen Word hasil pengarsipan otomatis E-Wakaf Mandiri.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Ekspor Word via..."))
        } catch (e: Exception) {
            // If Uri.fromFile fails under fileprovider system, we fallback safely to simple text share
            val textShareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, rawTextContents)
                putExtra(Intent.EXTRA_SUBJECT, "Dokumen Wakaf")
            }
            context.startActivity(Intent.createChooser(textShareIntent, "Kirim Teks Dokumen via..."))
            Toast.makeText(context, "Mengekspor teks dokumen ke Sharesheet", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(land.wakifName, fontWeight = FontWeight.Bold, color = Color(0xFF21005D), fontSize = 16.sp)
                        Text("Pratinjau & Cetak Dokumen", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF21005D))
                    }
                },
                actions = {
                    // COPY TEXT BUTTON
                    IconButton(onClick = {
                        val rawText = DocumentGenerator.generateRawText(selectedTab, land, nazhirsList)
                        clipboardManager.setText(AnnotatedString(rawText))
                        Toast.makeText(context, "Teks disalin ke Clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Salin Teks", tint = Color(0xFF21005D))
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
                .testTag("document_viewer_screen")
        ) {
            // TOP TABS CAROUSEL (Switch which document of the single-entry set to load)
            ScrollableTabRow(
                selectedTabIndex = selectedTab - 1,
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = Color(0xFF6750A4),
                modifier = Modifier.fillMaxWidth().testTag("document_tabs")
            ) {
                documentTabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab.first,
                        onClick = { selectedTab = tab.first },
                        selectedContentColor = Color(0xFF6750A4),
                        unselectedContentColor = Color(0xFF49454F),
                        text = {
                            Text(
                                tab.second,
                                fontWeight = if (selectedTab == tab.first) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // CORE PRATINJAU AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                // RENDER RAW WEBVIEW FOR PERFECT CORRESPONDENCE FORM PRINTING ACCORDANCE STYLE
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                }
                            }
                            settings.apply {
                                defaultTextEncodingName = "UTF-8"
                                textZoom = 100
                                supportZoom()
                                displayZoomControls = false
                            }
                            loadDataWithBaseURL(null, currentHtml, "text/html", "UTF-8", null)
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize().testTag("doc_webview")
                )
            }

            // BOTTOM CONTROL PALETTE (CETAK PDF & EKSPOR WORD)
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // EXPORT WORD BUTTON
                    OutlinedButton(
                        onClick = { exportToWord() },
                        modifier = Modifier.weight(1f).testTag("export_word_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = "Word Icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ekspor Word", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // CETAK / SAVE PDF BUTTON
                    Button(
                        onClick = { printDocument() },
                        modifier = Modifier.weight(1.2f).testTag("print_pdf_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 9) Color(0xFFE6A100) else Color(0xFF6750A4)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print PDF", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedTab == 9) "Cetak Piagam" else "Cetak & PDF",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
