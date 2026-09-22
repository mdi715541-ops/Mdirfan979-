package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple
import com.example.ui.theme.ReelSurface
import com.example.ui.theme.ReelSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<CommentEntity>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onLikeComment: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ReelSurface,
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comments.size} Comments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 0.8.dp)

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Be the first to vibe! ✨",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onLike = { onLikeComment(comment.id) }
                        )
                    }
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 0.8.dp)

            // Add Comment Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ReelPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = {
                        Text(
                            text = "Add a comment...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ReelSurfaceVariant,
                        unfocusedContainerColor = ReelSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = ReelPink,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("comment_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            onAddComment(commentInput.trim())
                            commentInput = ""
                        }
                    },
                    enabled = commentInput.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (commentInput.isNotBlank()) ReelPink else Color.White.copy(alpha = 0.1f))
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (commentInput.isNotBlank()) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentEntity,
    onLike: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ReelPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.userName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.userName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comment.userHandle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = comment.text,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onLike() }
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (comment.isLiked) ReelPink else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.likesCount.toString(),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
