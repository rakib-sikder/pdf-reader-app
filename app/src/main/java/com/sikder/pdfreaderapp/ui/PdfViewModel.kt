package com.example.pdfreader.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sikder.pdfreaderapp.service.TranslationService
import kotlinx.coroutines.launch

// Data class to hold the state of the translation overlay
data class TranslationState(
    val translatedText: String = "",
    val showOverlay: Boolean = false,
    val selectedText: String = ""
)

class PdfViewModel : ViewModel() {

    // Holds the URI of the PDF file selected by the user.
    var pdfUri by mutableStateOf<Uri?>(null)
        private set

    // Holds the state related to text translation.
    var translationState by mutableStateOf(TranslationState())
        private set

    private val translationService = TranslationService()

    // Called when the user selects a new PDF file.
    fun onPdfSelected(uri: Uri) {
        pdfUri = uri
    }

    /**
     * This is the function we will call when text is selected on the PDF.
     * The text extraction logic itself will be complex and will be handled in the View.
     * For now, this function simulates receiving selected text and translating it.
     */
    fun onTextSelected(text: String) {
        if (text.isBlank()) {
            // If text is blank or selection is cleared, hide the overlay.
            translationState = TranslationState(showOverlay = false)
            return
        }

        // Update the state with the selected text immediately.
        translationState = translationState.copy(selectedText = text, showOverlay = true, translatedText = "Translating...")

        // Launch a coroutine to perform the translation without blocking the UI.
        viewModelScope.launch {
            val result = translationService.translateText(text)
            translationState = translationState.copy(translatedText = result)
        }
    }

    // Called to dismiss the translation overlay.
    fun dismissTranslation() {
        translationState = TranslationState(showOverlay = false)
    }

    override fun onCleared() {
        // Clean up the translation service when the ViewModel is destroyed.
        translationService.close()
        super.onCleared()
    }
}
