package com.tapcard.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import com.tapcard.app.ui.viewmodel.UsernameValidationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: ProfileViewModel,
    onComplete: () -> Unit
) {
    val profile by viewModel.profileState.collectAsState()

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
            if (uri != null) profilePhotoUri = uri.toString()
        }
    )

    val companyLogoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) companyLogoUri = uri.toString()
        }
    )

    val validationState by viewModel.usernameValidationState.collectAsState()

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
                title = { Text("Create Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            Text(
                text = "Let's set up your digital business card.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = { 
                    profilePhotoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(if (profilePhotoUri != null) "Photo Selected" else "Add Photo")
                }
                
                OutlinedButton(onClick = { 
                    companyLogoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(if (companyLogoUri != null) "Logo Selected" else "Add Logo")
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
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
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )
            
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
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = isUsernameValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Create Card", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
