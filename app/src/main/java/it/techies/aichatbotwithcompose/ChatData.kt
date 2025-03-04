package it.techies.aichatbotwithcompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ChatData(
    val message:String,
    val role:String,
    val isPlaying: MutableState<Boolean> = mutableStateOf(false)
)

enum class ChatRoleEnum(val role:String){
    USER("user"),
    MODEL("model")
}