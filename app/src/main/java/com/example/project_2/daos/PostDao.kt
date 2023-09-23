package com.example.project_2.daos

import com.example.project_2.CreatePostActivity
import com.example.project_2.models.Post
import com.example.project_2.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("posts")
    val auth = Firebase.auth

    private lateinit var createPost: CreatePostActivity





    fun addPost(text: String) {
        GlobalScope.launch {

            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!






            val currentTime = System.currentTimeMillis()
            val post = Post(text, user, currentTime)
            postCollections.document().set(post)



        }
    }


    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }



    fun updateImage(postId: String){

        createPost = CreatePostActivity()
        val img = createPost.plantImageUrl1

        GlobalScope.launch {
            val post = getPostById(postId).await().toObject(Post::class.java)!!

            postCollections.document(postId).set(post.plantImageUrl)
        }

    }







    fun updateLikes(postId: String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if(isLiked) {
                post.likedBy.remove(currentUserId)
            } else {
                post.likedBy.add(currentUserId)
            }
            postCollections.document(postId).set(post)
        }

    }



}