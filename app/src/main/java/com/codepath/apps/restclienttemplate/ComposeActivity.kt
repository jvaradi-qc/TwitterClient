package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharCount = findViewById(R.id.tvCharCount)

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(etCompose.text.toString().trim().length < MAX_TWEET_LENGTH){
                    Log.i(TAG,"in onTextChanged if")
                    tvCharCount.text = (MAX_TWEET_LENGTH - etCompose.text.toString().trim().length).toString()
                    tvCharCount.setTextColor(Color.BLACK)
                }
                else{
                    Log.i(TAG,"in onTextChanged else")
                    tvCharCount.text = "0"
                    tvCharCount.setTextColor(Color.RED)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
                //tvCharCount.setText(s.toString())
            }
        })

        btnTweet.setOnClickListener {

            // Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
                // Look into displaying SnackBar message instead of Toast (more elegant)
            } else
            // 2. Make sure the tweet is under character count
            if (tweetContent.length > MAX_TWEET_LENGTH) {
                Toast.makeText(this,"Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT).show()
            } else {
                // Make an api call to Twitter api to publish tweet
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()


                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }



                })
            }



        }
    }

    companion object {
        val TAG = "ComposeActivity"
        val MAX_TWEET_LENGTH = 280
    }
}