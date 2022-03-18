package com.codepath.apps.restclienttemplate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

class TweetsAdapter(val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //inflate our layout
        val view =  inflater.inflate(R.layout.item_tweet, parent, false)

        return ViewHolder(view)
    }

    // populates data into the item through viewholder
    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        // get the data model based upon position
        val tweet: Tweet = tweets.get(position)

        // set item views based on views and data model

        holder.tvUserName.text = tweet.user?.name
        holder.tvTweetBody.text = tweet.body
        holder.tvTweetAge.text = tweet.tweetAge
        Log.i(TAG,"Tweet body variable value: ${tweet.body}")
        Log.i(TAG,"Tweet Age variable value: ${tweet.tweetAge}")

        //itemview is a view which is a context that can be used
        Glide.with(holder.itemView).load(tweet.user?.publicImageUrl).into(holder.ivProfileImage)

    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(pos: Int, tweetList: List<Tweet>) {
        tweets.addAll(pos,tweetList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUserName = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
        val tvTweetAge = itemView.findViewById<TextView>(R.id.tvTweetAge)

    }

    companion object {
        val TAG = "TweetsAdapter"
    }
}