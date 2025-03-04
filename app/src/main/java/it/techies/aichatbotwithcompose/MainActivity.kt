package it.techies.aichatbotwithcompose

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import it.techies.aichatbotwithcompose.ui.theme.AIChatbotWithComposeTheme
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    var isPlaying by mutableStateOf(false)
    var currentTextPosition by mutableStateOf(0)
    var currentPlayingItem: ChatData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 31) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_AIChatbotWithCompose) //else use old approach
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // Ensures the status bar icons are light-colored
        }

        tts = TextToSpeech(this, this)
        tts.setPitch(1.1F)
        tts.setSpeechRate(0.8F)
        setContent {
            AIChatbotWithComposeTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.DarkGray)
                        .padding(top = 50.dp, bottom = 30.dp)
                ) {
                    ChatBot()
                }
            }
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Do nothing
            }

            override fun onDone(utteranceId: String?) {
                runOnUiThread {
                    currentPlayingItem?.isPlaying?.value = false
                    isPlaying = false
                    currentTextPosition = 0
                    currentPlayingItem = null
                }
            }

            override fun onError(utteranceId: String?) {
                // Handle error
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                currentTextPosition = start
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
           val result =  tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle error
                Toast.makeText(
                    this@MainActivity,
                    "$result",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "Initialization failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun speakOut(text: String, item: ChatData) {
        if (isPlaying && currentPlayingItem == item) {
            tts.stop()
            item.isPlaying.value = false
        } else {
            if (currentPlayingItem != null) {
                currentPlayingItem?.isPlaying?.value = false
            }
            currentPlayingItem = item
            item.isPlaying.value = true

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID")

            if (currentTextPosition > 0 && currentPlayingItem == item) {
                tts.speak(text.substring(currentTextPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            } else {
                currentTextPosition = 0
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
        }
        isPlaying = !isPlaying
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech to release resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

/**
 * 1. https://ai.google.dev/gemini-api/docs/downloads
 * 2. https://aistudio.google.com/u/5/prompts/new_chat?gad_source=1&gclid=Cj0KCQiAq-u9BhCjARIsANLj-s2H-CKrNJu0CLsYxrWWPR7uaCVHOykVcFFk_pNKZnm8Syy3KLI7KMQaAig0EALw_wcB&pli=1
 * 3. https://developer.android.com/develop/ui/compose/libraries
 */

/*if (isPlaying && currentPlayingItem == item) {
            tts.stop()
            item.isPlaying.value = false
            currentPlayingItem = null
        } else {
            currentPlayingItem?.isPlaying?.value = false
            currentPlayingItem = item
            item.isPlaying.value = true

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID")
            }

            if (currentTextPosition > 0 && currentPlayingItem == item) {
                tts.speak(text.substring(currentTextPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            } else {
                currentTextPosition = 0
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
        }
        isPlaying = item.isPlaying.value*/

/*if (isPlaying) {
    tts.stop()
    // Approximate current position, you can adjust logic to more accurately track position
    currentTextPosition += text.length / 10 // For example, update every 10% of text
} else {
    if (currentTextPosition > 0) {
        tts.speak(text.substring(currentTextPosition), TextToSpeech.QUEUE_FLUSH, null, "")
    } else {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}
isPlaying = !isPlaying*/