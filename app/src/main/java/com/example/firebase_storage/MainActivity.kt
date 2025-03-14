package com.example.firebase_storage

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.firebase_storage.ui.theme.FirebasestorageTheme
import com.google.firebase.storage.FirebaseStorage
import java.net.URL
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebasestorageTheme {
                FirebaseStorage(modifier = Modifier.padding(top = 30.dp))
            }
        }
    }

    @Composable
    fun FirebaseStorage(modifier: Modifier = Modifier) {
        Button(onClick = { openGallery() }, modifier = modifier) {
            Text("Sélectionner une image")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun FirebaseStoragePreview() {
        FirebasestorageTheme {
            FirebaseStorage()
        }
    }

    val PICK_IMAGE_REQUEST = 1
    var imageUri: Uri? = null
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference


    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Récupérer l'image sélectionnée
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            uploadImage()
        }
    }

    fun uploadImage() {
        if (imageUri != null) {
            val fileRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("Firebase", "Image uploaded. Download URL: $uri")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Upload failed", e)
                }
        } else {
            Log.e("Firebase", "No image selected")
        }
    }
}
