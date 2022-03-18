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
    var user: User? = null
) :
    Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val tweet = Tweet()
            tweet.tweetId = jsonObject.getLong("id")
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.tweetAge = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
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