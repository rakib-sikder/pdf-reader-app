package com.sikder.pdfreaderapp.service

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationService {

    private val options: TranslatorOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.SPANISH) // Translating to Spanish
        .build()

    private val translator: Translator = Translation.getClient(options)

    init {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                println("Translation model downloaded successfully.")
            }
            .addOnFailureListener { exception ->
                println("Error downloading translation model: ${exception.message}")
            }
    }

    suspend fun translateText(text: String): String {
        return try {
            translator.translate(text).await()
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }

    fun close() {
        translator.close()
    }
}
