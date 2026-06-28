package com.tapcard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.tapcard.app.ui.components.BusinessCardPreview
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import com.tapcard.app.ui.viewmodel.UsernameValidationState
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profileState.collectAsState()
    
    var username by remember { mutableStateOf(profile.username) }
    val validationState by viewModel.usernameValidationState.collectAsState()
    
    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.updateProfile(profile.copy(profilePhotoLocalUri = uri.toString()))
            }
        }
    )

    val companyLogoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.updateProfile(profile.copy(companyLogoLocalUri = uri.toString()))
            }
        }
    )

    // Initialize validation
    LaunchedEffect(Unit) {
        viewModel.onUsernameChanged(username)
    }

    val isUsernameValid = validationState == UsernameValidationState.AVAILABLE ||
                          validationState == UsernameValidationState.SIGN_IN_TO_VALIDATE ||
                          validationState == UsernameValidationState.SUPABASE_NOT_CONFIGURED ||
                          validationState == UsernameValidationState.IDLE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card Editor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview
            BusinessCardPreview(profile = profile)

            Spacer(modifier = Modifier.height(32.dp))

            // Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Theme", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = profile.isDarkTheme,
                    onCheckedChange = { isDark ->
                        viewModel.updateProfile(profile.copy(isDarkTheme = isDark))
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = { 
                    profilePhotoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Change Photo")
                }
                
                OutlinedButton(onClick = { 
                    companyLogoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Change Logo")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Username Editor
            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    viewModel.onUsernameChanged(it)
                },
                label = { Text("Username (for share link)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = validationState == UsernameValidationState.TAKEN || validationState == UsernameValidationState.INVALID_FORMAT
            )
            
            val helperText = when (validationState) {
                UsernameValidationState.IDLE -> ""
                UsernameValidationState.CHECKING -> "Checking availability..."
                UsernameValidationState.AVAILABLE -> "Username available"
                UsernameValidationState.TAKEN -> "Username taken"
                UsernameValidationState.INVALID_FORMAT -> "Invalid format (3-30 chars, lowercase, numbers, hyphens)"
                UsernameValidationState.SIGN_IN_TO_VALIDATE -> "Local only / username not reserved (sign in to sync)"
                UsernameValidationState.SUPABASE_NOT_CONFIGURED -> "Local only (Supabase not configured)"
            }
            val helperColor = when (validationState) {
                UsernameValidationState.AVAILABLE -> Color(0xFF388E3C) // Green
                UsernameValidationState.TAKEN, UsernameValidationState.INVALID_FORMAT -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            }
            if (helperText.isNotEmpty()) {
                Text(
                    text = helperText,
                    color = helperColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp, bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Button(
                onClick = {
                    viewModel.updateProfile(profile.copy(username = username))
                    viewModel.saveProfile()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = isUsernameValid
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}
