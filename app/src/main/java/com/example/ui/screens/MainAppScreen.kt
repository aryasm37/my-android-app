package com.example.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

val BgColor = Color(0xFFFDFBFF)
val TextPrimary = Color(0xFF1B1B1F)
val NavBg = Color(0xFFF3F4F9)

val BentoBlueBg = Color(0xFFDDE1FF)
val BentoBlueText = Color(0xFF001453)
val BentoPurpleBg = Color(0xFFF3E7FF)
val BentoPurpleText = Color(0xFF2C1349)
val BentoOrangeBg = Color(0xFFFFE9E1)
val BentoOrangeText = Color(0xFF4F1D0E)
val SurfaceDark = Color(0xFF1B1B1F)
val HeaderIconBg = Color(0xFFE2E2E6)
val HeaderIconTint = Color(0xFF44474E)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainAppScreen() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        containerColor = BgColor,
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Header()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scanner View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(SurfaceDark)
            ) {
                if (cameraPermissionState.status.isGranted) {
                    ScanScreen(
                        onMenuClick = {},
                        onBarcodeDetected = {}
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Camera permission required", color = Color.White)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bento Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Create Code (Spans 2 rows)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(BentoBlueBg)
                        .clickable { /* TODO */ }
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoBlueText),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                        
                        Column {
                            Text(
                                "Create\nCode",
                                color = BentoBlueText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                lineHeight = 24.sp
                            )
                            Text(
                                "Text, Link, Wi-Fi",
                                color = BentoBlueText.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                
                // Right Column (History, Saved)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // History
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(BentoPurpleBg)
                            .clickable { /* TODO */ }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoPurpleText),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "History",
                            color = BentoPurpleText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                    
                    // Saved
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(BentoOrangeBg)
                            .clickable { /* TODO */ }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoOrangeText),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Saved",
                            color = BentoOrangeText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "UTILITY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderIconTint
            )
            Text(
                "QR Scanner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { /* TODO Toggle Flash */ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(HeaderIconBg)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = HeaderIconTint)
            }
            IconButton(
                onClick = { /* TODO Open Settings */ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(HeaderIconBg)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = HeaderIconTint)
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    Surface(
        color = NavBg,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.QrCodeScanner,
                label = "SCAN",
                isActive = true
            )
            NavItem(
                icon = Icons.Default.QrCode,
                label = "GENERATE",
                isActive = false
            )
            NavItem(
                icon = Icons.Default.History,
                label = "BATCH",
                isActive = false
            )
            NavItem(
                icon = Icons.Default.Person,
                label = "ACCOUNT",
                isActive = false
            )
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isActive) BentoBlueBg else Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) BentoBlueText else HeaderIconTint
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) BentoBlueText else HeaderIconTint
        )
    }
}
