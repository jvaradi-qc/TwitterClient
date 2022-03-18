package com.codepath.apps.restclienttemplate.models

import androidx.room.Embedded

class TweetWithUser {

    @Embedded
    lateinit var user : User

    @Embedded(prefix = "tweet_")
    lateinit var tweet : Tweet

    companion object {

        fun getTweetList(tweetWithUserList: List<TweetWithUser>?): List<Tweet>? {
            val tweets: MutableList<Tweet> = ArrayList()
            if (tweetWithUserList != null) {
                for (i in tweetWithUserList.indices) {
                    val tweetWithUser = tweetWithUserList?.get(i)
                    val tweet = tweetWithUser.tweet
                    tweet.user = tweetWithUser.user
                    tweets.add(tweet)
                }
            }
            return tweets
        }


    }
}