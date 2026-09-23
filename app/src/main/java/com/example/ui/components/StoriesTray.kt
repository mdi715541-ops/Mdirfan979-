package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryEntity
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple

@Composable
fun StoriesTray(
    stories: List<StoryEntity>,
    currentUserHandle: String,
    onStoryClick: (StoryEntity) -> Unit,
    onCreateStoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userStory = stories.firstOrNull { it.isUserStory }
    val otherStories = stories.filter { !it.isUserStory }

    val infiniteTransition = rememberInfiniteTransition(label = "story_ring")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stories_tray"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Current User Story Item (Add or View)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        if (userStory != null) {
                            onStoryClick(userStory)
                        } else {
                            onCreateStoryClick()
                        }
                    }
                    .testTag("user_story_item")
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ring
                    if (userStory != null) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .rotate(ringRotation)
                                .border(
                                    width = 2.5.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(ReelPink, ReelPurple, ReelGold, ReelCyan, ReelPink)
                                    ),
                                    shape = CircleShape
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = CircleShape
                                )
                        )
                    }

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(ReelPurple, ReelPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Plus icon badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ReelPink)
                            .border(1.5.dp, Color.Black, CircleShape)
                            .clickable { onCreateStoryClick() }
                            .testTag("create_story_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Story",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (userStory != null) "Your Story" else "Add Status",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }

        // 2. Stories from Other Creators
        items(otherStories, key = { it.id }) { story ->
            val isUnviewed = !story.isViewed
            val ringBrush = if (isUnviewed) {
                Brush.sweepGradient(
                    listOf(ReelPink, ReelGold, ReelCyan, ReelPurple, ReelPink)
                )
            } else {
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.15f))
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStoryClick(story) }
                    .testTag("story_item_${story.id}")
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Instagram-style Ring
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(
                                width = if (isUnviewed) 2.5.dp else 1.5.dp,
                                brush = ringBrush,
                                shape = CircleShape
                            )
                    )

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    when (story.id.toInt() % 4) {
                                        0 -> listOf(ReelPink, ReelPurple)
                                        1 -> listOf(ReelCyan, Color(0xFF1E88E5))
                                        2 -> listOf(ReelGold, Color(0xFFFF7043))
                                        else -> listOf(ReelPurple, ReelCyan)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.creatorName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "ST" },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Music badge or Video indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.8f))
                                .border(1.dp, ReelGold.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (story.mediaType == "VIDEO") Icons.Default.PlayArrow else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ReelGold,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.creatorName.split(" ").firstOrNull() ?: story.creatorHandle,
                    fontSize = 11.sp,
                    color = if (isUnviewed) Color.White else Color.White.copy(alpha = 0.6f),
                    fontWeight = if (isUnviewed) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
