package com.codepath.apps.restclienttemplate.models

import android.util.Log
import com.codepath.apps.restclienttemplate.TimeFormatter
import org.json.JSONArray
import org.json.JSONObject

class Tweet {

    var body: String = ""
    var createdAt: String = ""
    var tweetAge: String = ""
    var user: User? = null

    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.tweetAge = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray) : List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for(i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
                //Log.i("TweetPos", "$i")
            }

            return tweets
        }
    }
}