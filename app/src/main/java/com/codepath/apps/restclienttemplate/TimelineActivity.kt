package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.TweetDao
import com.codepath.apps.restclienttemplate.models.TweetWithUser
import com.codepath.apps.restclienttemplate.models.User
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var  client: TwitterClient

    lateinit var  rvTweets: RecyclerView

    lateinit var  adapter: TweetsAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()

    var tweetDao: TweetDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.full_logo)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        client = TwitterApplication.getRestClient(this)
        tweetDao = (applicationContext as TwitterApplication).myDatabase?.TweetDao()

        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            //Log.i(TAG,"Refreshing timeline")
            //populateHomeTimeline()

          AsyncTask.execute {
                Log.i(TAG, "Showing data from database")
                val tweetWithUsers : List<TweetWithUser>? = tweetDao?.recentItems()
                val tweetsFromDB : List<Tweet>? = TweetWithUser.getTweetList(tweetWithUsers)
                if (tweetsFromDB != null) {
                    var currentDBTweetAge : String
                    for(i in 0 until tweetsFromDB.size){
                        currentDBTweetAge = TimeFormatter.getTimeDifference(tweetsFromDB.get(i).createdAt)
                        tweetsFromDB.get(i).tweetAge = currentDBTweetAge
                    }
                    adapter.clear()
                    adapter.addAll(0,tweetsFromDB)
                }

            }

            updateHomeTimeline()



        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )


    rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        AsyncTask.execute {
            Log.i(TAG, "Showing data from database")
            val tweetWithUsers : List<TweetWithUser>? = tweetDao?.recentItems()
            val tweetsFromDB : List<Tweet>? = TweetWithUser.getTweetList(tweetWithUsers)
            if (tweetsFromDB != null) {
                var currentDBTweetAge : String
                for(i in 0 until tweetsFromDB.size){
                    currentDBTweetAge = TimeFormatter.getTimeDifference(tweetsFromDB.get(i).createdAt)
                    tweetsFromDB.get(i).tweetAge = currentDBTweetAge
                }
                adapter.clear()
                adapter.addAll(0,tweetsFromDB)
            }

        }
        populateHomeTimeline()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handles clicks on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.compose){
            //navigate to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    // This method is called when we come back from ComposeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            //get data from our intent (our tweet)
            val tweet = data?.getParcelableExtra<Tweet>("tweet")

            // update timeline
            // modifying the data source of tweets
            if (tweet != null) {
                tweets.add(0,tweet)
            }

            // update adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateHomeTimeline() {
        client.getHomeTimeline(object: JsonHttpResponseHandler(){

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG,"onSuccess reached: $json")


                val jsonArray = json.jsonArray

                try {
                    val tweetsFromNetwork : List<Tweet> = Tweet.fromJsonArray(jsonArray)
                    // clear out our currently fetched tweets so we can get most recent tweets
                    adapter.clear()
                    adapter.addAll(0,tweetsFromNetwork)
                    adapter.notifyDataSetChanged()

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)




                    AsyncTask.execute {
                        Log.i(TAG, "Saving data into database")

                        val usersFromNetwork : List<User> = User.fromJsonTweetArray(tweetsFromNetwork)

                        //val array : Array = tweetsFromNetwork.toTypedArray()
                        tweetDao?.insertAllUsers(usersFromNetwork)
                        tweetDao?.insertAllTweets(tweetsFromNetwork)

                    }

                } catch (e: JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG,"onFailure reached: $statusCode response: $response")
            }



        })
    }

    fun updateHomeTimeline() {
        client.getUpdatedHomeTimeline(object: JsonHttpResponseHandler(){

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG,"onSuccess reached:")

                val jsonArray = json.jsonArray

                try {
                    // clear out our currently fetched tweets so we can get most recent tweets
                    Log.i(TAG, "tweets list size before refresh: ${tweets.size}")

                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    //adapter.clear()
                    adapter.addAll(0,listOfNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)

                    AsyncTask.execute {
                        Log.i(TAG, "Saving data into database")

                        val usersFromNetwork : List<User> = User.fromJsonTweetArray(listOfNewTweetsRetrieved)

                        //val array : Array = tweetsFromNetwork.toTypedArray()
                        tweetDao?.insertAllUsers(usersFromNetwork)
                        tweetDao?.insertAllTweets(listOfNewTweetsRetrieved)

                    }
                    Log.i(TAG, "tweets list size after refresh: ${tweets.size}")

                } catch (e: JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG,"onFailure reached: $statusCode response: $response")
            }



        }, tweets)
    }


    companion object {
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 10
    }
}