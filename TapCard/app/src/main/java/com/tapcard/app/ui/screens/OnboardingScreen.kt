package com.tapcard.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun OnboardingScreen(
    viewModel: ProfileViewModel,
    onComplete: () -> Unit
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current

    var currentStep by remember { mutableIntStateOf(0) }

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
                title = { 
                    Text(
                        text = if (currentStep == 0) "Welcome" else "Step $currentStep of 3", 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
                if (currentStep > 0) {
                    LinearProgressIndicator(
                        progress = currentStep / 3f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Step content
                when (currentStep) {
                    0 -> {
                        // Welcome Screen
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "TapCard",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your identity, instantly shareable.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Immersive Card Mockup
                        BusinessCardPreview(profile = profile)

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Input your details once to build your digital business card. Share it with anyone using NFC tags or a quick QR scan.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                currentStep = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Get Started", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    1 -> {
                        // Step 1: Identity Info
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Who are you?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Enter your primary details and select your photo and logo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
                        )

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
                                Text(if (profilePhotoUri != null) "✓ Photo Added" else "Add Profile Photo")
                            }
                            
                            OutlinedButton(
                                onClick = { 
                                    companyLogoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(if (companyLogoUri != null) "✓ Logo Added" else "Add Company Logo")
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

                        TextButton(
                            onClick = { currentStep = 0 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back to Welcome")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    2 -> {
                        // Step 2: Contact Info
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "How can people reach you?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Provide your contact numbers and profiles for your digital card.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
                        )

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
                            label = { Text("Website / URL") },
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Finalize your card link",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Claim your username and set your visual theme preferences.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { 
                                username = it
                                viewModel.onUsernameChanged(it)
                            },
                            label = { Text("Username") },
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
                                onComplete()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = isUsernameValid && username.isNotBlank()
                        ) {
                            Text("Complete Setup", fontWeight = FontWeight.Bold)
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
