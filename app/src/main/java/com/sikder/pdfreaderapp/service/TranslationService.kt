package com.sikder.pdfreaderapp.service

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslationService {

    private val options: TranslatorOptions = TranslatorOptions.Builder()
        // In a real app, you would let the user choose the source and target languages.
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.SPANISH) // Example: translating to Spanish
        .build()

    private val englishSpanishTranslator: Translator = Translation.getClient(options)

    init {
        // Ensure the model is downloaded. This can be done at app startup.
        // It's good practice to show a progress indicator to the user.
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishSpanishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                println("Translation model downloaded.")
            }
            .addOnFailureListener { exception ->
                // Model download failed. Handle the error.
                println("Error downloading translation model: ${exception.message}")
            }
    }

    /**
     * Translates the given text. This is a suspending function to be called from a coroutine.
     * @param text The text to translate.
     * @return The translated text, or an error message if it fails.
     */
    suspend fun translateText(text: String): String {
        return try {
            englishSpanishTranslator.translate(text).await()
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }

    /**
     * It's important to close the translator when it's no longer needed
     * to free up resources.
     */
    fun close() {
        englishSpanishTranslator.close()
    }
}