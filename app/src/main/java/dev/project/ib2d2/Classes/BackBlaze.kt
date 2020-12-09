package dev.project.ib2d2.Classes

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class BackBlaze {
    private val TAG = javaClass.name

    // account details TODO: move to a proper file
    private val accountID: String = "0016174650cbcda0000000008"
    private val appKey: String = "K001JQR6IDafd/zEippnmJsSJZkDljU"
    private val bucketName: String = "ib2d2-dev"

    // authorize returns
    private lateinit var accAuthToken: String
    private lateinit var apiUrl: String
    private lateinit var downloadUrl: String
    private lateinit var bucketID: String

    // data for upload
    private lateinit var bitmap: Bitmap
    private lateinit var title: String
    private lateinit var desc: String
    private lateinit var shaHash: String
    private lateinit var timeStamp: String


    /**
     * authorize(): authorize account information to B2
     *
     *  @ref: https://www.backblaze.com/b2/docs/b2_authorize_account.html
     */
     fun authorize(){
        // create connection, headers, and
        var url = URL("https://api.backblazeb2.com/b2api/v2/b2_authorize_account")
        var authHeader = ("Basic " + Base64.encodeToString("$accountID:$appKey".toByteArray(), Base64.DEFAULT))
        lateinit var json: JSONObject

        try {
            // can we authorize with b2?
            (url.openConnection() as? HttpURLConnection)?.run{
                requestMethod = "GET"
                setRequestProperty("Authorization", authHeader)
                json = JSONObject(jsonDecode(inputStream))

                // set auth token, apiUrl, downloadUrl
                accAuthToken = json["authorizationToken"] as String
                downloadUrl = json["downloadUrl"] as String
                apiUrl = json["apiUrl"] as String
                bucketID = json.getJSONObject("allowed")["bucketId"] as String

                // log what we got from b2
                Log.d(TAG, json.toString())
                Log.d(TAG, "accAuthToken:" + accAuthToken)
                Log.d(TAG, "downloadUrl:" + downloadUrl)
                Log.d(TAG, "apiUrl:" + apiUrl)
                Log.d(TAG, "bucketID:" + bucketID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * getUploadUrl(): get the uploadURL for B2
     *
     *  @ref: https://www.backblaze.com/b2/docs/b2_get_upload_url.html
     */
    private fun getUploadUrl(){

    }


    /**
     * jsonDecode(): decode the json from web requests
     *
     *  @input InputStream: input stream from web
     */
    fun jsonDecode(input: InputStream?): String? {
        val json = input?.reader().use { it?.readText() }
        if (json != null) {
            return json
        }
        return "Error: Could not decode json data"
    }

    suspend fun upload(){
        withContext(Dispatchers.IO){
            authorize()
            Log.d(TAG, "out of the thread")
        }
    }
}
