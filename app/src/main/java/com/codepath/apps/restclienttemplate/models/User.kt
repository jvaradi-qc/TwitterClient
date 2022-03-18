package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
@Entity
class User(

    @ColumnInfo
    @PrimaryKey
    var id: Long? = null,

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var screenName: String = "",

    @ColumnInfo
    var publicImageUrl : String = ""
): Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject): User {
            val user = User()
            user.id = jsonObject.getLong("id")
            user.name = jsonObject.getString("name")
            user.screenName = jsonObject.getString("screen_name")
            user.publicImageUrl = jsonObject.getString("profile_image_url_https")

            return user
        }

        fun fromJsonTweetArray(tweetsFromNetwork: List<Tweet>): List<User> {
            val users = ArrayList<User>()
            for (i in 0 until tweetsFromNetwork.size){
                tweetsFromNetwork.get(i).user?.let { users.add(it) }
            }
            return users
        }
    }
}