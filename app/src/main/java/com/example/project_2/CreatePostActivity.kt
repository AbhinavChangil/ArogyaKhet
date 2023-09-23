package com.example.project_2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.project_2.daos.PostDao
import com.example.project_2.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_post.*
import java.text.SimpleDateFormat
import java.util.*


class CreatePostActivity : AppCompatActivity() {

    private lateinit var postDao: PostDao
    private lateinit var createPost: CreatePostActivity

    private lateinit var adapter: PostAdapter

    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    var plantImageUrl1 = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)


        progressBar.visibility = View.GONE

        initVars()
        registerClickEvents()


        postDao = PostDao()
        createPost = CreatePostActivity()

        // creating a storage reference




        postBtn()


    }


    private fun registerClickEvents() {
        uploadBtn.setOnClickListener {
            uploadImage()
        }


        camera.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        img2.setImageURI(it)
    }


    private fun initVars() {

        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }



    fun uploadImage() {
        progressBar.visibility = View.VISIBLE
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()
                        var plantImageUrl1 = uri.toString()
//                        fun updateVariable(){
//                            plantImageUrl1 = uri.toString()
//                        }





                        firebaseFirestore.collection("images").add(map).addOnCompleteListener { firestoreTask ->

                            if (firestoreTask.isSuccessful){
                                Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
//                                postInput.setText("${uri.toString()}")





//                                val intent = Intent(this, PostAdapter::class.java)
//                                intent.putExtra("plantImageUrl", plantImageUrl1)
//                                startActivity(intent)

                            }else{
                                Toast.makeText(this, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()

                            }
                            progressBar.visibility = View.GONE
                            img2.setImageResource(R.drawable.baseline_cloud_upload_24)

                        }
                    }
                    progressBar.visibility = View.GONE
                    img2.setImageResource(R.drawable.baseline_cloud_upload_24)

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }



    fun postBtn() {
        postButton.setOnClickListener {
            val input = postInput.text.toString().trim()
            if (input.isNotEmpty()) {
                postDao.addPost(input)
                finish()
            }
        }
    }


//    private fun getImageDownloadUrl(storageRef: StorageReference) {
//        storageRef.downloadUrl.addOnSuccessListener { uri ->
//            val imageUrl = uri.toString()
//            // Now, you can use the 'imageUrl' variable as the URL of the uploaded image.
//            // You can store it in a variable, display it, or use it as needed.
////            Log.d("Image URL", imageUrl)
//            postInput.setText("$imageUrl")
//        }.addOnFailureListener { exception ->
//            // Handle any errors that occurred while retrieving the URL.
//            Log.e("Image URL", "Error getting image URL: ${exception.message}", exception)
//        }
//    }





    override fun onBackPressed(){
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}