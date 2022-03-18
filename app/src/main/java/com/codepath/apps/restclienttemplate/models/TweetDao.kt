package com.codepath.apps.restclienttemplate.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.tweetAge AS tweet_tweetAge, Tweet.tweetId AS tweet_id, User.*" +
            "FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY tweet.tweetId DESC LIMIT 5")
    fun recentItems() : List<TweetWithUser>

   // @Insert(onConflict = OnConflictStrategy.REPLACE)
   // fun insertModel(tweet: Tweet, tweets: List<Tweet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTweets(tweets: List<Tweet>)

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //fun insertModel(user: User, users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(users: List<User>)
}