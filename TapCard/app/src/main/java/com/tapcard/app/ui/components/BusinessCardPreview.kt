package com.tapcard.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapcard.app.data.models.Profile

@Composable
fun BusinessCardPreview(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = if (profile.isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF232526), Color(0xFF414345))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3))
        )
    }

    val textColor = if (profile.isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = textColor.copy(alpha = 0.7f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f), // Standard business card aspect ratio
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Section: Name and Title
                Column {
                    Text(
                        text = profile.fullName.ifBlank { "Your Name" },
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = profile.jobTitle.ifBlank { "Job Title" },
                        color = secondaryTextColor,
                        fontSize = 14.sp
                    )
                    Text(
                        text = profile.company.ifBlank { "Company" },
                        color = secondaryTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Bottom Section: Contact Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    if (profile.phone.isNotBlank()) {
                        Text(
                            text = profile.phone,
                            color = textColor,
                            fontSize = 12.sp
                        )
                    }
                    if (profile.email.isNotBlank()) {
                        Text(
                            text = profile.email,
                            color = textColor,
                            fontSize = 12.sp
                        )
                    }
                    if (profile.website.isNotBlank()) {
                        Text(
                            text = profile.website,
                            color = textColor,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
