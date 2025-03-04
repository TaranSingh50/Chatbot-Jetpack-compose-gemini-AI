package it.techies.aichatbotwithcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.techies.aichatbotwithcompose.components.ChatFooter
import it.techies.aichatbotwithcompose.components.ChatHeader
import it.techies.aichatbotwithcompose.components.ChatList

@Composable
fun ChatBot(viewModel: ChatBotViewModel = viewModel()) {
    val uiState by viewModel.uiState.observeAsState(ChatBotUiState.Ideal)
    var chatDataList by remember { mutableStateOf(viewModel.chatList) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChatHeader()

        Box(
            modifier = Modifier
                .weight(1F)
                .padding(14.dp)
        ) {
            when (uiState) {
                is ChatBotUiState.Ideal -> {
                    // Display the initial state UI
                    Text(
                        "Start your chat!",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ChatBotUiState.Loading -> {
                    // Display a loading indicator
                    //  CircularProgressIndicator(color = Color.White)
                    Column {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                        ChatList(list = chatDataList)
                    }

                }

                is ChatBotUiState.Success -> {
                    // Display the chat response
                    ChatList(list = chatDataList)
                }

                is ChatBotUiState.Error -> {
                    // Display the error message
                    val errorMessage = (uiState as ChatBotUiState.Error).chatError
                    Text(
                        "Error: $errorMessage",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        ChatFooter {
            if (it.isNotEmpty()) {
                viewModel.sendPrompt(it)
            }
        }
    }
}