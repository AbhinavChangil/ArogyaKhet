package com.example.project_2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_2.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0


    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }
    abstract fun onDoubleClick(v: View)
    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener: IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {


    private lateinit var createPost: CreatePostActivity

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
        viewHolder.likeButton.setOnClickListener{
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        viewHolder.itemView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
            }
        })





        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {


//        createPost = CreatePostActivity()
//        val img = createPost.plantImageUrl1





//        val intent = Intent.getIntent("plantImageUrl1")
//        val img = intent.getStringArrayListExtra("plantImageUrl")

        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        Glide.with(holder.plantImage.context).load(model.plantImageUrl).circleCrop().into(holder.plantImage)

//        Glide.with(holder.plantImage.context).load(img).circleCrop().into(holder.plantImage)

//        Picasso.get()
//            .load(img)
//            .into(holder.plantImage)

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }

    }
}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
}