package com.sikder.pdfreaderapp.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sikder.pdfreaderapp.service.TranslationService
import kotlinx.coroutines.launch

data class TranslationState(
    val translatedText: String = "",
    val showOverlay: Boolean = false,
    val selectedText: String = ""
)

class PdfViewModel : ViewModel() {

    var pdfUri by mutableStateOf<Uri?>(null)
        private set

    var translationState by mutableStateOf(TranslationState())
        private set

    private val translationService = TranslationService()

    fun onPdfSelected(uri: Uri) {
        pdfUri = uri
    }

    fun onTextSelected(text: String) {
        if (text.isBlank()) {
            translationState = TranslationState(showOverlay = false)
            return
        }

        translationState = translationState.copy(selectedText = text, showOverlay = true, translatedText = "Translating...")

        viewModelScope.launch {
            val result = translationService.translateText(text)
            translationState = translationState.copy(translatedText = result)
        }
    }

    fun dismissTranslation() {
        translationState = TranslationState(showOverlay = false)
    }

    override fun onCleared() {
        translationService.close()
        super.onCleared()
    }
}
