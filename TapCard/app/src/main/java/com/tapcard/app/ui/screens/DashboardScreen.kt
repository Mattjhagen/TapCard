package com.tapcard.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    onEditCard: () -> Unit
) {
    val profile by viewModel.profileState.collectAsState()
    val shareUrl = viewModel.getShareableUrl()
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val nfcState by viewModel.nfcState.collectAsState()
    var isSharingActive by remember { mutableStateOf(false) }
    
    val syncStatus by viewModel.syncStatus.collectAsState()

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
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onEditCard) {
                        Text("Edit", fontWeight = FontWeight.Bold)
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
                    qrCodeBitmap?.let { bitmap ->
                        viewModel.shareQrCode(bitmap, "Here is my digital business card: $shareUrl")?.let { intent ->
                            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
                        }
                    }
                }) {
                    Text("Share QR")
                }

                OutlinedButton(onClick = {
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
                    if (activity != null) {
                        if (isSharingActive) {
                            viewModel.stopNfcProgramming(activity)
                            isSharingActive = false
                            Toast.makeText(context, "NFC programming stopped", Toast.LENGTH_SHORT).show()
                        } else {
                            val success = viewModel.startNfcProgramming(activity)
                            if (success) {
                                isSharingActive = true
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
                    if (WalletConfig.isGoogleWalletEnabled) {
                        // Real Wallet implementation goes here when backend is ready
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
