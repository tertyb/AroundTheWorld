package com.harelshaigal.aroundtw.ui.userDispaly.userProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.harelshaigal.aroundtw.R
import com.squareup.picasso.Picasso

class UserPostsAdapter(private var postImages: List<String>) :
    RecyclerView.Adapter<UserPostsAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.postImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_image, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val imageUrl = postImages[position]

        // Clear any previous image (prevents flickering)
        holder.imageView.setImageDrawable(null)

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .fit() // Adjust the image size to match the ImageView
            .centerCrop()
            .into(holder.imageView)

    }

    override fun getItemCount() = postImages.size

    fun updatePosts(newImages: List<String>) {
        postImages = newImages
        notifyDataSetChanged() // Refresh the RecyclerView
    }
}
