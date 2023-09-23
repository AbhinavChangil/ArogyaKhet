package com.example.project_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_2.daos.PostDao
import com.example.project_2.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.tasks.await
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postDao = PostDao()

        fab.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
            }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        val postCollections = postDao.postCollections
        val query = postCollections.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()


        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart(){
        super.onStart()
        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume(){
        super.onResume()
        adapter.notifyDataSetChanged()
    }



    override fun onLikeClicked(postId: String){
        postDao.updateLikes((postId))
    }




    override fun onBackPressed(){
        super.onBackPressed()
        exitProcess(0)
    }


    }


