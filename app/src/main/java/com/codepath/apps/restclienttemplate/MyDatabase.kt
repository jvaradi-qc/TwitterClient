package com.codepath.apps.restclienttemplate

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codepath.apps.restclienttemplate.models.*

@Database(entities = [SampleModel::class, Tweet::class, User::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun sampleModelDao(): SampleModelDao?
    abstract fun TweetDao(): TweetDao?

    companion object {
        // Database name to be used
        const val NAME = "MyDatabase"
    }
}