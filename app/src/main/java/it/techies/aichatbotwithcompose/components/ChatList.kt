package it.techies.aichatbotwithcompose.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.techies.aichatbotwithcompose.ChatData
import it.techies.aichatbotwithcompose.ChatRoleEnum
import it.techies.aichatbotwithcompose.MainActivity
import it.techies.aichatbotwithcompose.R
import kotlinx.coroutines.launch

@Composable
fun ChatList(list: MutableList<ChatData>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var previousText by remember { mutableStateOf("") }
    val context = LocalActivity.current as MainActivity

    LaunchedEffect(list.size) {
        coroutineScope.launch {
            listState.animateScrollToItem(list.size - 1)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
        items(items = list) { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 2.dp, horizontal = 2.dp),
            ) {
                if (item.role == ChatRoleEnum.USER.role) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1F)
                                .padding(end = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = item.message.trim(),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                                    .padding(start = 4.dp)
                                    .padding(10.dp),
                                textAlign = TextAlign.Justify,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = {
                                if (item.message != context.currentPlayingItem?.message) {
                                    list.forEach { item ->
                                        item.isPlaying.value = false
                                    }
                                    context.isPlaying = false
                                    context.currentTextPosition = 0
                                    context.currentPlayingItem = null
                                }
                                context.speakOut(item.message, item)
                            },
                            modifier = Modifier
                                .weight(0.1F)
                        ) {
                            Icon(
                                painter = if (item.isPlaying.value) {
                                    painterResource(id = R.drawable.ic_pause)
                                } else {
                                    painterResource(id = R.drawable.ic_play)
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(Color.LightGray)
                                    .padding(4.dp),
                                tint = Color.DarkGray
                            )
                        }
                    }

                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val annotatedText = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                append(item.message.trim().replace("*",""))
                            }
                        }
                        Text(
                            text = annotatedText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(16.dp),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )

                        IconButton(
                            onClick = {
                                if (item.message != context.currentPlayingItem?.message) {
                                    list.forEach { item ->
                                        item.isPlaying.value = false
                                    }
                                    context.isPlaying = false
                                    context.currentTextPosition = 0
                                    context.currentPlayingItem = null
                                }
                                context.speakOut(item.message, item)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = if (item.isPlaying.value) {
                                    painterResource(id = R.drawable.ic_pause)
                                } else {
                                    painterResource(id = R.drawable.ic_play)
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(Color.LightGray)
                                    .padding(4.dp),
                                tint = Color.DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


/*if (item.message != previousText) {
    context.isPlaying = false
    context.currentTextPosition = 0
    previousText = item.message
}
context.speakOut(item.message)*/