package com.example.firebasestorage

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


lateinit var imgbtn:Button
lateinit var uploadbtn:Button
lateinit var downloadbtn:Button
lateinit var imgview:ImageView
lateinit var selectedImageUri:Uri
val storage = Firebase.storage
val storageRef = storage.reference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgbtn=findViewById(R.id.button)
        imgview=findViewById(R.id.imageView)
        imgbtn.setOnClickListener {
            selectImage()
        }
        uploadbtn=findViewById(R.id.button2)
        uploadbtn.setOnClickListener {
            uploadImage()
        }
        downloadbtn=findViewById(R.id.button3)
        downloadbtn.setOnClickListener {
//            val intent=Intent(this,downloadactivity::class.java)
//            startActivity(intent)
           downloadImage("1708411518885.jpg")
        }
    }

    private fun uploadImage() {
        selectedImageUri?.let { uri ->
                val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                val uploadTask = imageRef.putFile(uri)
            }

    }

    private fun downloadImage(imageName: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val maxDownloadSize = 3L * 1024 * 1024
            val imageRef = storageRef.child("images/$imageName")


            if (imageRef.metadata.await()!=null) {
                val bytes = imageRef.getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                withContext(Dispatchers.Main) {
                    imgview.setImageBitmap(bmp)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Image not found", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Error downloading image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && data!=null){
             selectedImageUri = data.data!!
            imgview.setImageURI(selectedImageUri)
        }
    }
}