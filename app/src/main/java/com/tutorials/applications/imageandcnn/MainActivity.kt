package com.tutorials.applications.imageandcnn

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tutorials.applications.imageandcnn.ml.Modelly
import com.tutorials.applications.imageandcnn.ui.theme.ImageandCNNTheme
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category


class MainActivity : ComponentActivity() {

    private lateinit var model: Modelly

    companion object {
        init {
            System.loadLibrary("imageandcnn")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model= Modelly.newInstance(this)
        setContent {
            ImageandCNNTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageLoaderAndCNN()
                }
            }
        }
    }


    @SuppressLint("RememberReturnType")
    @Composable
    fun ImageLoaderAndCNN(modifier: Modifier = Modifier) {
        var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
        var classificationResults by remember { mutableStateOf<Map<Uri, Category?>>(emptyMap()) }
        val context = LocalContext.current


        val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                selectedImageUris = uris
                val results = mutableMapOf<Uri, Category?>()
                uris.forEach { uri ->
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.setTargetColorSpace(android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.SRGB))
                    }
                    Log.d("BitmapConfig", "Bitmap configuration: ${bitmap.config}") // Add this line
                    val result = classify(bitmap, model)

                    results[uri] = Category(result, 1.0f)
                }
                classificationResults = results
            }
        )

        LazyColumn(modifier = modifier
            .fillMaxSize()
            .padding(16.dp)) {
            item {
                Column(
                    modifier = modifier.fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally  // Align the column in the center
                ) {
                    Text("Birds Image Classifier", fontWeight = FontWeight.Bold, fontSize = 36.sp, modifier = Modifier.padding(16.dp), textAlign= TextAlign.Center, lineHeight = 48.sp)
                    HorizontalDivider()

                    Row(
                        modifier = modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Text("Select multiple photos")
                        }
                    }
                }
            }

            items(selectedImageUris) { uri ->
                AsyncImage(
                    model = uri, contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentScale = ContentScale.Crop
                )
                Text(capitalizeClassificationResult( classificationResults[uri]?.label ?: "Unknown"))
            }
        }






    }
    private fun classify(bitmap: Bitmap, model: Modelly): String {
        val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Create a TensorImage from the ARGB_8888 bitmap
        val tfImage = TensorImage.fromBitmap(argbBitmap)
        Log.d("TensorImage", "TensorImage configuration: ${tfImage.buffer}")
        // Process the image with the model and get the classification results
        try {
            // Perform image classification using the model
            val outputs = model.process(tfImage).probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }

            // Return the label of the highest probability category
            return outputs[0].label
        } catch (e: Exception) {
            // Handle any exceptions that might occur during classification
            Log.e("lll", "Error classifying image: ${e.message}")
        }

        // If classification fails or encounters an error, return an appropriate message
        return "Unknown"
    }

}external fun capitalizeClassificationResult(input: String): String


