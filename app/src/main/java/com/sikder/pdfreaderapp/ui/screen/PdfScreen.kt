package com.example.pdfreader.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdfreader.ui.PdfViewModel
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnTapListener

@Composable
fun PdfScreen(
    pdfViewModel: PdfViewModel = viewModel()
) {
    val pdfUri by pdfViewModel.pdfUri
    val translationState by pdfViewModel.translationState

    // Launcher for the file picker.
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                pdfViewModel.onPdfSelected(it)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Reader with Translation") },
                actions = {
                    Button(onClick = { filePickerLauncher.launch("application/pdf") }) {
                        Text("Open PDF")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (pdfUri != null) {
                // When a PDF is selected, show the PDF viewer.
                PdfViewer(
                    uri = pdfUri!!,
                    onTextSelected = { text ->
                        // This is where the text selection logic will be triggered.
                        // For now, we will simulate it with a tap.
                        pdfViewModel.onTextSelected(text)
                    }
                )

                // Show the translation overlay if needed.
                if (translationState.showOverlay) {
                    TranslationOverlay(
                        selectedText = translationState.selectedText,
                        translatedText = translationState.translatedText,
                        onDismiss = { pdfViewModel.dismissTranslation() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            } else {
                // Show a message asking the user to select a PDF.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Please select a PDF file to get started.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun PdfViewer(uri: Uri, onTextSelected: (String) -> Unit) {
    val context = LocalContext.current

    // AndroidView is a composable that can host a classic Android View.
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PDFView(context, null).apply {
                // --- IMPORTANT ---
                // The actual text selection logic would be extremely complex.
                // We would need to intercept touch events, map coordinates to text blocks
                // in the PDF, and extract the text. This is a major engineering task.
                //
                // FOR THIS EXAMPLE: We simulate text selection by just tapping the screen.
                // We will pass back a hardcoded string. In a real app, this would be the
                // extracted text from the PDF.
                val onTapListener = OnTapListener {
                    // SIMULATION: In a real app, you would extract text here.
                    onTextSelected("Hello world")
                    true // Return true to indicate the tap was handled
                }

                // Load the PDF from the URI
                fromUri(uri)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onTap(onTapListener) // Set our simulated tap listener
                    .load()
            }
        }
    )
}

@Composable
fun TranslationOverlay(
    selectedText: String,
    translatedText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selected: \"$selectedText\"", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(translatedText, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}
