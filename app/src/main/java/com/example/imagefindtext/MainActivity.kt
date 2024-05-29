package com.example.imagefindtext

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.imagefindtext.ui.theme.ImageFindTextTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageFindTextTheme {
                ImageTextRecognition()
            }
        }
    }
}

@Composable
fun ImageTextRecognition() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        val context = LocalContext.current
        var result by remember { mutableStateOf("") }
        val photoSelect =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    imageUri = uri
                    val image: InputImage
                    try {
                        image = InputImage.fromFilePath(context, uri)
                        recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                result = visionText.text
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        Image(
            modifier = Modifier
                .size(200.dp),
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null
        )

        Button(onClick = {
            photoSelect.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "이미지 선택")
        }

        Text(text = result)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageFindTextTheme {
        ImageTextRecognition()
    }
}
