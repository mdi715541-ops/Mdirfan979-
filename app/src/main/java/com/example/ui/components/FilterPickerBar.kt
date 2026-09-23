package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoFilter
import com.example.ui.theme.ReelCyan
import com.example.ui.theme.ReelGold
import com.example.ui.theme.ReelPink
import com.example.ui.theme.ReelPurple

@Composable
fun FilterPickerBar(
    selectedFilter: VideoFilter,
    onFilterSelected: (VideoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ReelGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Video Filters • रील्स वीडियो फिल्टर",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(VideoFilter.values()) { filter ->
                val isSelected = filter == selectedFilter
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) ReelPink else Color.Transparent,
                    label = "border"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onFilterSelected(filter) }
                        .padding(vertical = 4.dp)
                        .testTag("filter_${filter.name}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(width = 2.5.dp, color = borderColor, shape = CircleShape)
                            .background(getFilterSwatchBrush(filter)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = filter.displayName,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ReelPink else Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun getFilterSwatchBrush(filter: VideoFilter): Brush {
    return when (filter) {
        VideoFilter.NORMAL -> Brush.linearGradient(listOf(Color(0xFF4A4E69), Color(0xFF22223B)))
        VideoFilter.VINTAGE -> Brush.linearGradient(listOf(Color(0xFFC9A227), Color(0xFF6B4226)))
        VideoFilter.NOIR -> Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFF000000)))
        VideoFilter.VIBRANT -> Brush.linearGradient(listOf(Color(0xFFFF5252), Color(0xFFFF7A00)))
        VideoFilter.NEON -> Brush.linearGradient(listOf(ReelPink, ReelCyan))
        VideoFilter.CINEMATIC -> Brush.linearGradient(listOf(Color(0xFF00B4D8), Color(0xFFFF9E00)))
        VideoFilter.GOLDEN_HOUR -> Brush.linearGradient(listOf(Color(0xFFFFB703), Color(0xFFFB8500)))
        VideoFilter.BOLLYWOOD_GLAM -> Brush.linearGradient(listOf(Color(0xFFFF758C), Color(0xFFFF7EB3)))
        VideoFilter.RETRO_VHS -> Brush.linearGradient(listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B)))
    }
}
