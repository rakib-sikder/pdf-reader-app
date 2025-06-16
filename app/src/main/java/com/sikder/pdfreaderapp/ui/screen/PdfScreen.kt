package com.sikder.pdfreaderapp.ui.screen

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
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnTapListener
import com.sikder.pdfreaderapp.ui.PdfViewModel
import com.sikder.pdfreaderapp.ui.TranslationState

@Composable
fun PdfScreen(
    pdfViewModel: PdfViewModel = viewModel()
) {
    val pdfUri by pdfViewModel.pdfUri
    val translationState by pdfViewModel.translationState

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
                title = { Text("PDF Reader") },
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
                PdfViewer(
                    uri = pdfUri!!,
                    onTextSelected = { text ->
                        pdfViewModel.onTextSelected(text)
                    }
                )

                if (translationState.showOverlay) {
                    TranslationOverlay(
                        state = translationState,
                        onDismiss = { pdfViewModel.dismissTranslation() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Please select a PDF file.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun PdfViewer(uri: Uri, onTextSelected: (String) -> Unit) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PDFView(context, null).apply {
                // THIS IS THE DUMMY CODE WE DISCUSSED.
                // A tap will simulate selecting the text "Hello world".
                // Making real text selection work is the next big step after the app builds.
                val onTapListener = OnTapListener {
                    onTextSelected("Hello world")
                    true
                }

                fromUri(uri)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onTap(onTapListener)
                    .load()
            }
        }
    )
}

@Composable
fun TranslationOverlay(
    state: TranslationState,
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
            Text("Selected: \"${state.selectedText}\"", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.translatedText, style = MaterialTheme.typography.bodyLarge)
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
