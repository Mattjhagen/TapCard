package com.tapcard.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
                                    // In a real app, this would show a dialog. For now, create a hardcoded one.
                                    val count = profilesList.size + 1
                                    viewModel.createNewProfile("Identity $count")
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
                        Text("⚙️")
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

            // Card Preview
            BusinessCardPreview(profile = profile)

            Spacer(modifier = Modifier.height(16.dp))

            // Sync Status
            val statusText = when(syncStatus) {
                SyncStatus.SAVED_LOCALLY -> "Offline: Saved Locally"
                SyncStatus.UPLOADING -> "Uploading image..."
                SyncStatus.SYNCING -> "Syncing with Supabase..."
                SyncStatus.SYNCED -> "Synced to Cloud"
                SyncStatus.SYNC_FAILED -> "Cloud Sync Failed"
                SyncStatus.SIGN_IN_TO_SYNC -> "Sign in to Sync to Cloud"
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = if (syncStatus == SyncStatus.SYNC_FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (syncStatus == SyncStatus.SYNC_FAILED && syncError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Sync Error",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = syncError ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // QR Code
            Text(
                text = "Share via QR Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            qrCodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )
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
                                Toast.makeText(context, "Ready to program! Hold NFC tag near.", Toast.LENGTH_SHORT).show()
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
}
