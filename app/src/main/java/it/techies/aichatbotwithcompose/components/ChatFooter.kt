package it.techies.aichatbotwithcompose.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import it.techies.aichatbotwithcompose.R

@Composable
fun ChatFooter(
    onClick: (message: String) -> Unit
) {
    var inputText by remember {
        mutableStateOf("")
    }
    val view = LocalView.current

    // For speech to text
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var isTextTyped by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("microphone_animation.json")
    )

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.current.language)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isListening = true
            speechRecognizer.startListening(speechRecognizerIntent)
            hideKeyboard(view)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.DarkGray)
            .padding(10.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
                if (it.isNotEmpty()){
                    isTextTyped = true
                }else{
                    isTextTyped = false
                }
            },
            placeholder = {
                Text(
                    text = "Ask anything..",
                    color = Color.LightGray
                )
            },
            singleLine = false,
            modifier = Modifier
                .weight(1F)
                .padding(8.dp)
                .background(Color.Gray, shape = RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default // Show Done button
            )/*,
            keyboardActions = KeyboardActions(
                onSend = {
                    // keyboardController?.hide() // Hide keyboard when Done is clicked
                    onClick(inputText)
                    inputText = ""
                    isTextTyped = false
                    hideKeyboard(view)
                }
            )*/
        )

        if (isTextTyped) {
            IconButton(onClick = {
                onClick(inputText)
                inputText = ""
                isTextTyped = false
                hideKeyboard(view)
            }, modifier = Modifier.size(52.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(
                            CircleShape
                        )
                        .background(Color.LightGray)
                        .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    tint = Color.DarkGray
                )
            }
        } else {
            IconButton(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }, modifier = Modifier.size(52.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.mic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(
                            CircleShape
                        )
                        .background(Color.LightGray)
                        .padding(8.dp),
                    tint = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (isListening) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                // Perform action when speech ends
                if (inputText.isNotEmpty()) {
                    onClick(inputText)
                    inputText = ""
                    isListening = false
                }
            }

            override fun onError(error: Int) {
                Log.e("SpeechRecognizer", "Error code: $error")
                isListening = false
                Toast.makeText(context, "Recognition error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    inputText = it.firstOrNull() ?: ""
                    // Perform action when results are available
                    if (inputText.isNotEmpty()) {
                        onClick(inputText)
                        inputText = ""
                        isListening = false
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose {
            speechRecognizer.destroy()
        }
    }
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}