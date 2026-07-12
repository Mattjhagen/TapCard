package com.tapcard.app.ui.screens

import androidx.compose.ui.text.style.TextAlign
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import android.content.Intent
import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.tapcard.app.ui.components.BusinessCardPreview
import com.tapcard.app.utils.QRCodeGenerator
import com.tapcard.app.utils.NfcState
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import com.tapcard.app.domain.wallet.WalletConfig
import com.tapcard.app.domain.model.SyncStatus

// Custom contactless / NFC symbol Vector graphic
val WifiNfcIcon: ImageVector = ImageVector.Builder(
    name = "WifiNfc",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        stroke = SolidColor(Color.White),
        strokeLineWidth = 2.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        // Wave 1 (inner)
        moveTo(10f, 7f)
        curveTo(12.2f, 7f, 14f, 8.8f, 14f, 11f)
        curveTo(14f, 13.2f, 12.2f, 15f, 10f, 15f)

        // Wave 2 (middle)
        moveTo(13f, 4f)
        curveTo(16.8f, 4f, 20f, 7.2f, 20f, 11f)
        curveTo(20f, 14.8f, 16.8f, 18f, 13f, 18f)

        // Wave 3 (outer)
        moveTo(7f, 9f)
        curveTo(8.1f, 9f, 9f, 9.9f, 9f, 11f)
        curveTo(9f, 12.1f, 8.1f, 13f, 7f, 13f)
    }
}.build()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ProfileViewModel,
    onEditCard: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val profilesList by viewModel.profilesList.collectAsStateWithLifecycle()
    val shareUrl = viewModel.getShareableUrl()
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val hapticFeedback = LocalHapticFeedback.current

    val nfcState by viewModel.nfcState.collectAsStateWithLifecycle()
    var isSharingActive by remember { mutableStateOf(false) }
    
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val syncError by viewModel.syncError.collectAsStateWithLifecycle()

    var showProfileMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.nfcProgrammingResult.collect { (success, message) ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (success && isSharingActive && activity != null) {
                viewModel.stopNfcProgramming(activity)
                isSharingActive = false
            }
        }
    }

    // Stop NFC programming if the composable leaves the composition or goes to background
    DisposableEffect(lifecycleOwner, activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkNfcState()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                if (isSharingActive && activity != null) {
                    viewModel.stopNfcProgramming(activity)
                    isSharingActive = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (isSharingActive && activity != null) {
                viewModel.stopNfcProgramming(activity)
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current

    // Generate high-resolution QR Code bitmap (1024x1024) for both display and export
    val qrCodeBitmap = remember(shareUrl) {
        QRCodeGenerator.generateQRCode(shareUrl, size = 1024)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Box {
                            TextButton(onClick = { showProfileMenu = true }) {
                                Text(profile.profileName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.width(4.dp))
                                Text("▼", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                            DropdownMenu(
                                expanded = showProfileMenu,
                                onDismissRequest = { showProfileMenu = false }
                            ) {
                                profilesList.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.profileName) },
                                        onClick = {
                                            viewModel.switchProfile(p.id)
                                            showProfileMenu = false
                                        }
                                    )
                                }
                                Divider()
                                DropdownMenuItem(
                                    text = { Text("+ Create New Identity") },
                                    onClick = {
                                        viewModel.createNewProfile("Identity ${profilesList.size + 1}")
                                        showProfileMenu = false
                                    }
                                )
                            }
                        }
                    },
                    actions = {
                        TextButton(onClick = onEditCard) {
                            Text("Edit", fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Close, // Placeholder gear placeholder
                                contentDescription = "Settings",
                                modifier = Modifier.graphicsLayer(rotationZ = 45f) // stylized
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Business Card Preview widget
                BusinessCardPreview(profile = profile)

                Spacer(modifier = Modifier.height(12.dp))

                // Sync Status Indicator
                val syncText = when (syncStatus) {
                    SyncStatus.SAVED_LOCALLY -> "Saved locally (Offline)"
                    SyncStatus.SIGN_IN_TO_SYNC -> "Sign in to backup to cloud"
                    SyncStatus.UPLOADING -> "Uploading card assets..."
                    SyncStatus.SYNCING -> "Syncing to cloud..."
                    SyncStatus.SYNCED -> "Cloud backup active ✓"
                    SyncStatus.SYNC_FAILED -> "Backup failed (Will retry)"
                }
                
                Text(
                    text = syncText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (syncStatus == SyncStatus.SYNC_FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                if (syncStatus == SyncStatus.SYNC_FAILED && syncError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = syncError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons for QR and Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        qrCodeBitmap?.let { bitmap ->
                            viewModel.shareQrCode(bitmap, "Here is my digital business card: $shareUrl")?.let { intent ->
                                context.startActivity(Intent.createChooser(intent, "Share QR Code"))
                            }
                        }
                    }) {
                        Text("Share QR")
                    }

                    OutlinedButton(onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        qrCodeBitmap?.let { bitmap ->
                            val saved = viewModel.saveQrToGallery(bitmap)
                            if (saved) {
                                Toast.makeText(context, "Saved to gallery", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Save QR")
                    }

                    OutlinedButton(onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        clipboardManager.setText(AnnotatedString(shareUrl))
                        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Copy Link")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // NFC Button
                val nfcButtonText = when {
                    nfcState == NfcState.UNAVAILABLE -> "NFC Unavailable"
                    nfcState == NfcState.DISABLED -> "NFC Disabled (Enable in Settings)"
                    isSharingActive -> "Ready to Program (Hold Tag Near)"
                    else -> "Program NFC Tag"
                }
                
                val isNfcEnabled = nfcState == NfcState.READY

                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (activity != null) {
                            if (isSharingActive) {
                                viewModel.stopNfcProgramming(activity)
                                isSharingActive = false
                                Toast.makeText(context, "NFC programming stopped", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = viewModel.startNfcProgramming(activity)
                                if (success) {
                                    isSharingActive = true
                                    com.tapcard.app.utils.AnalyticsManager.logNfcProgrammed()
                                } else {
                                    Toast.makeText(context, "NFC Program failed / unsupported", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Cannot start NFC (no activity)", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = isNfcEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSharingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(nfcButtonText, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Wallet Button
                OutlinedButton(
                    onClick = {
                        com.tapcard.app.utils.AnalyticsManager.logWalletButtonTapped()
                        if (WalletConfig.isGoogleWalletEnabled) {
                            val username = profile.username.ifBlank { profile.id.toString() }
                            val slug = profile.profileSlug.ifBlank { ProfileViewModel.computeSlug(profile.profileName) }
                            val walletUrl = "https://tapcard.space/api/wallet/google/$username/$slug"
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(walletUrl))
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Google Wallet requires backend configuration.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = WalletConfig.isGoogleWalletEnabled
                ) {
                    Text(
                        if (WalletConfig.isGoogleWalletEnabled) "Add to Google Wallet" else "Google Wallet coming soon", 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Custom Animated NFC Programming Overlay
        if (isSharingActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Pulse contactless animation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "nfcPulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.85f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "nfcScale"
                        )
                        
                        Icon(
                            imageVector = WifiNfcIcon,
                            contentDescription = "Pulsing Contactless Waves",
                            tint = Color.White,
                            modifier = Modifier
                                .size(96.dp)
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Ready to Program",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hold your phone near the physical NFC tag.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Glassmorphic Card Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.White.copy(alpha = 0.08f), shape = MaterialTheme.shapes.large)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), shape = MaterialTheme.shapes.large)
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TAP CARD WRITER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "WAITING FOR TAG",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Yellow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Column {
                                Text(
                                    text = profile.fullName.ifBlank { "New Identity" },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (profile.jobTitle.isNotBlank()) "${profile.jobTitle} @ ${profile.company}".trim().removePrefix("@").removeSuffix("@").trim() else "Digital Business Card",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Circular Close/Cancel Button
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (activity != null) {
                                viewModel.stopNfcProgramming(activity)
                            }
                            isSharingActive = false
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.15f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel NFC Program",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
