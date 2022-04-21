package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import android.util.Log
import androidx.room.*
import com.codepath.apps.restclienttemplate.TimeFormatter
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
@Entity(foreignKeys = arrayOf(
        ForeignKey(entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"))))
class Tweet (

    @ColumnInfo
    @PrimaryKey
    var tweetId: Long? = null,

    @ColumnInfo
    var body: String = "",

    @ColumnInfo
    var createdAt: String = "",

    @ColumnInfo
    var tweetAge: String = "",

    @ColumnInfo
    var userId: Long? = null,

    @Ignore
    var user: User? = null,

    @Ignore
    var videoURL: String = ""
) :
    Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val tweet = Tweet()
            tweet.tweetId = jsonObject.getLong("id")
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.tweetAge = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
            if(jsonObject.has("extended_entities")){
                val extEntities = jsonObject.getJSONObject("extended_entities")
                val mediaArray = extEntities.getJSONArray("media")
                val mediaObj = mediaArray.getJSONObject(0)
                val mediaType = mediaObj.getString("type")
                if(mediaType == "video"){
                        Log.i(TAG,"We have a video!")
                        val video_url = mediaObj.getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url")
                        Log.i(TAG,"URL: ${video_url}")
                        tweet.videoURL = video_url
                }


                Log.i(TAG, "extEntities: ${extEntities}")
                Log.i(TAG,"mediaArr: ${mediaArray}")
            }


            var user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.user = user
            tweet.userId = user.id

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray) : List<Tweet> {
            val tweets = ArrayList<Tweet>()
            var tweet : Tweet
            for(i in 0 until jsonArray.length()) {
                //tweet = fromJson(jsonArray.getJSONObject(i))
                //Log.i(TAG," Tweet Age: ${tweet.tweetAge}")
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
                //Log.i("TweetPos", "$i")
            }

            return tweets
        }
        val TAG = "Tweet"
    }
}