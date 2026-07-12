package com.tapcard.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import com.tapcard.app.ui.viewmodel.UsernameValidationState
import com.tapcard.app.ui.components.BusinessCardPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current

    var currentStep by remember { mutableIntStateOf(1) }

    var fullName by remember { mutableStateOf(profile.fullName) }
    var jobTitle by remember { mutableStateOf(profile.jobTitle) }
    var company by remember { mutableStateOf(profile.company) }
    var phone by remember { mutableStateOf(profile.phone) }
    var email by remember { mutableStateOf(profile.email) }
    var website by remember { mutableStateOf(profile.website) }
    var username by remember { mutableStateOf(profile.username) }
    
    var profilePhotoUri by remember { mutableStateOf(profile.profilePhotoLocalUri) }
    var companyLogoUri by remember { mutableStateOf(profile.companyLogoLocalUri) }

    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                profilePhotoUri = uri.toString()
                viewModel.updateProfile(profile.copy(profilePhotoLocalUri = uri.toString()))
            }
        }
    )

    val companyLogoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                companyLogoUri = uri.toString()
                viewModel.updateProfile(profile.copy(companyLogoLocalUri = uri.toString()))
            }
        }
    )

    val validationState by viewModel.usernameValidationState.collectAsStateWithLifecycle()

    // Sync input values to viewmodel for preview to update in real-time
    LaunchedEffect(fullName, jobTitle, company, phone, email, website, username, profilePhotoUri, companyLogoUri) {
        viewModel.updateProfile(
            profile.copy(
                fullName = fullName,
                jobTitle = jobTitle,
                company = company,
                phone = phone,
                email = email,
                website = website,
                username = username,
                profilePhotoLocalUri = profilePhotoUri,
                companyLogoLocalUri = companyLogoUri
            )
        )
    }

    LaunchedEffect(username) {
        viewModel.onUsernameChanged(username)
    }

    val isUsernameValid = validationState == UsernameValidationState.AVAILABLE ||
                          validationState == UsernameValidationState.SIGN_IN_TO_VALIDATE ||
                          validationState == UsernameValidationState.SUPABASE_NOT_CONFIGURED ||
                          validationState == UsernameValidationState.IDLE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Card - Step $currentStep of 3", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Progress Indicator
                LinearProgressIndicator(
                    progress = currentStep / 3f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                // Live card preview is always helpful to see edits in real-time
                BusinessCardPreview(profile = profile)
                Spacer(modifier = Modifier.height(24.dp))

                // Step content
                when (currentStep) {
                    1 -> {
                        // Step 1: Identity Info
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = { 
                                    profilePhotoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(if (profilePhotoUri != null) "Change Photo" else "Add Photo")
                            }
                            
                            OutlinedButton(
                                onClick = { 
                                    companyLogoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(if (companyLogoUri != null) "Change Logo" else "Add Logo")
                            }
                        }

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = jobTitle,
                            onValueChange = { jobTitle = it },
                            label = { Text("Job Title") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = company,
                            onValueChange = { company = it },
                            label = { Text("Company") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                currentStep = 2
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = fullName.isNotBlank()
                        ) {
                            Text("Next Step", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    2 -> {
                        // Step 2: Contact Info
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                currentStep = 3
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = phone.isNotBlank() || email.isNotBlank() || website.isNotBlank()
                        ) {
                            Text("Next Step", fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = { currentStep = 1 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    3 -> {
                        // Step 3: Link & Style
                        OutlinedTextField(
                            value = username,
                            onValueChange = { 
                                username = it
                                viewModel.onUsernameChanged(it)
                            },
                            label = { Text("Username (for link)") },
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
                            UsernameValidationState.AVAILABLE -> Color(0xFF388E3C)
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
                                    .padding(start = 8.dp, top = 4.dp, bottom = 24.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateProfile(
                                    profile.copy(
                                        fullName = fullName,
                                        jobTitle = jobTitle,
                                        company = company,
                                        phone = phone,
                                        email = email,
                                        website = website,
                                        username = username,
                                        profilePhotoLocalUri = profilePhotoUri,
                                        companyLogoLocalUri = companyLogoUri
                                    )
                                )
                                viewModel.saveProfile()
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = isUsernameValid && username.isNotBlank()
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
