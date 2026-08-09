package com.example.ui.screens

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.camera.BarcodeAnalyzer
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

@Composable
fun ScanScreen(
    onMenuClick: () -> Unit,
    onBarcodeDetected: (Barcode) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var hasFlash by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableStateOf(0f) }
    var maxZoom by remember { mutableStateOf(1f) }
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    
    val analyzer = remember { BarcodeAnalyzer { barcode -> onBarcodeDetected(barcode) } }

    LaunchedEffect(lensFacing) {
        analyzer.reset()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                previewView
            },
            update = { previewView ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                        }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        
                        hasFlash = camera?.cameraInfo?.hasFlashUnit() == true
                        maxZoom = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f
                        
                    } catch (e: Exception) {
                        Log.e("ScanScreen", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Overlay drawing (darkened background with transparent cutout)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val boxSize = canvasWidth * 0.7f
            val left = (canvasWidth - boxSize) / 2
            val top = (canvasHeight - boxSize) / 2
            
            // Draw darkened background
            drawRect(color = Color.Black.copy(alpha = 0.5f))
            
            // Draw transparent cutout
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                blendMode = BlendMode.Clear
            )
            
            // Draw red scanning line
            drawLine(
                color = Color.Red,
                start = Offset(left, top + boxSize / 2),
                end = Offset(left + boxSize, top + boxSize / 2),
                strokeWidth = 2.dp.toPx()
            )
            
            // Draw corner markers
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val blueColor = Color.White
            // Top Left
            drawLine(blueColor, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
            drawLine(blueColor, Offset(left, top), Offset(left, top + cornerLength), strokeWidth)
            // Top Right
            drawLine(blueColor, Offset(left + boxSize, top), Offset(left + boxSize - cornerLength, top), strokeWidth)
            drawLine(blueColor, Offset(left + boxSize, top), Offset(left + boxSize, top + cornerLength), strokeWidth)
            // Bottom Left
            drawLine(blueColor, Offset(left, top + boxSize), Offset(left + cornerLength, top + boxSize), strokeWidth)
            drawLine(blueColor, Offset(left, top + boxSize), Offset(left, top + boxSize - cornerLength), strokeWidth)
            // Bottom Right
            drawLine(blueColor, Offset(left + boxSize, top + boxSize), Offset(left + boxSize - cornerLength, top + boxSize), strokeWidth)
            drawLine(blueColor, Offset(left + boxSize, top + boxSize), Offset(left + boxSize, top + boxSize - cornerLength), strokeWidth)
        }

        // Zoom Slider at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = zoomRatio,
                onValueChange = { 
                    zoomRatio = it
                    camera?.cameraControl?.setLinearZoom(it)
                },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}
