package it.techies.aichatbotwithcompose

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatBotViewModel:ViewModel() {

    val chatList by lazy {
        mutableStateListOf<ChatData>()
    }

    private val _uiState = MutableLiveData<ChatBotUiState>(ChatBotUiState.Ideal)
    val uiState : LiveData<ChatBotUiState> = _uiState

    private val generativeAI by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY
        )
    }

    fun sendPrompt(message: String)=viewModelScope.launch {
        _uiState.value = ChatBotUiState.Loading
        try {
            val chat = generativeAI.startChat()
            chatList.add(ChatData(message=message, role = ChatRoleEnum.USER.role))
            val response = chat.sendMessage(
                content(ChatRoleEnum.USER.role) { text(message) }
            ).text

            response?.let { response ->
                chatList.add(ChatData(message = response, role = ChatRoleEnum.MODEL.role))
                Log.d("TAGsendPrompt", response)
                _uiState.value = ChatBotUiState.Success("Success")
            }?: kotlin.run {
                _uiState.value = ChatBotUiState.Error("No response from generativeAI")
            }
        }catch (e: Exception) {
            _uiState.value = ChatBotUiState.Error(e.message ?: "Unknown error")
            chatList.removeAt(chatList.size.minus(1))
        }

    }
}

/*
* 1. https://stackoverflow.com/questions/79175287/what-steps-can-i-use-to-debug-a-googlegenerativeai-error-resource-has-been-exha
* 2. https://console.cloud.google.com/iam-admin/quotas?referrer=search&pageState=(%22allQuotasTable%22:(%22f%22:%22%255B%257B_22k_22_3A_22_22_2C_22t_22_3A10_2C_22v_22_3A_22_5C_22gemini_5C_22_22%257D%255D%22))&authuser=5&project=gen-lang-client-0249157477
* */