package com.tapcard.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tapcard.app.domain.model.Profile

@Composable
fun BusinessCardPreview(profile: Profile, modifier: Modifier = Modifier) {
    val backgroundColor = if (profile.isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (profile.isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = textColor.copy(alpha = 0.7f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f), // Standard credit card ratio
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Photo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Profile Photo
                    val photoUri = profile.profilePhotoLocalUri ?: profile.profilePhotoUrl
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.fullName.takeIf { it.isNotBlank() }?.take(1)?.uppercase() ?: "?",
                                style = MaterialTheme.typography.titleLarge,
                                color = textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Column {
                        Text(
                            text = profile.fullName.ifBlank { "Your Name" },
                            color = textColor,
                            fontSize = 20.sp,
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
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                val logoUri = profile.companyLogoLocalUri ?: profile.companyLogoUrl
                if (logoUri != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = logoUri,
                        contentDescription = "Company Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                }
            }

            // Bottom Section: Contact Info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (profile.phone.isNotBlank()) {
                    Text(
                        text = profile.phone,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
                if (profile.email.isNotBlank()) {
                    Text(
                        text = profile.email,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
                if (profile.website.isNotBlank()) {
                    Text(
                        text = profile.website,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
