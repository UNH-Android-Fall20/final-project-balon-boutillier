package dev.project.ib2d2.Classes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class BackBlaze {
    private val TAG = javaClass.name

    // account details TODO: move to a proper file
    private val accountID: String = "0016174650cbcda0000000008"
    private val appKey: String = "K001JQR6IDafd/zEippnmJsSJZkDljU"
    private val bucketName: String = "ib2d2-dev"

    // returns from b2 api
    private lateinit var accAuthToken: String
    private lateinit var apiUrl: String
    private lateinit var downloadUrl: String
    private lateinit var bucketID: String
    private lateinit var uploadUrl: String
    private lateinit var accUploadToken: String
    private lateinit var fileID: String

    // data for upload
    private lateinit var bitmap: Bitmap
    private lateinit var shaHash: String
    private lateinit var timeStamp: String
    private lateinit var userID: String

    /**
     * authorize(): authorize account information to B2
     *
     *  @ref: https://www.backblaze.com/b2/docs/b2_authorize_account.html
     */
     fun authorize(){
        // create connection, headers
        var url = URL("https://api.backblazeb2.com/b2api/v2/b2_authorize_account")
        var authHeader = ("Basic " + Base64.encodeToString("$accountID:$appKey".toByteArray(), Base64.DEFAULT))
        lateinit var json: JSONObject

        try {
            // can we authorize with b2?
            (url.openConnection() as? HttpURLConnection)?.run{
                requestMethod = "GET"
                setRequestProperty("Authorization", authHeader)
                json = JSONObject(jsonDecode(inputStream))

                // retrieve data from b2 json
                accAuthToken = json["authorizationToken"] as String
                downloadUrl = json["downloadUrl"] as String
                apiUrl = json["apiUrl"] as String
                bucketID = json.getJSONObject("allowed")["bucketId"] as String

                // log what we got from b2
                Log.d(TAG, json.toString())
                Log.d(TAG, "accAuthToken: " + accAuthToken)
                Log.d(TAG, "downloadUrl: " + downloadUrl)
                Log.d(TAG, "apiUrl: " + apiUrl)
                Log.d(TAG, "bucketID: " + bucketID)
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
        // create connection, headers
        var url = URL("$apiUrl/b2api/v2/b2_get_upload_url")
        var postParams = "{\"bucketId\":\"" + bucketID + "\"}"
        var postData = postParams.toByteArray()
        lateinit var json: JSONObject

        try {
            // can we authorize with b2?
            (url.openConnection() as? HttpURLConnection)?.run{
                requestMethod = "POST"
                setRequestProperty("Authorization", accAuthToken)
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("charset", "utf-8")
                setRequestProperty("Content-Length", postData.size.toString())
                doOutput = true
                outputStream.write(postData)
                json = JSONObject(jsonDecode(inputStream))

                // retrieve data from b2 json
                uploadUrl = json["uploadUrl"] as String
                accUploadToken = json["authorizationToken"] as String

                // log what we got from b2
                Log.d(TAG, json.toString())
                Log.d(TAG, "uploadUrl: " + uploadUrl)
                Log.d(TAG, "accUploadToken: " + accUploadToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * uploadFile(): upload the file to b2
     *
     *  @ref: https://www.backblaze.com/b2/docs/b2_upload_file.html
     */
    private fun uploadFile(){
        // create connection, headers
        var url = URL(uploadUrl)
        val fileName = (userID + "_" + timeStamp)
        val contentType = "image/jpeg"
        lateinit var json: JSONObject

        // convert image data
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteStream)
        val imageData = byteStream.toByteArray()

        try {
            // can we authorize with b2?
            (url.openConnection() as? HttpURLConnection)?.run{
                requestMethod = "POST"
                setRequestProperty("Authorization", accUploadToken)
                setRequestProperty("Content-Type", contentType)
                setRequestProperty("X-Bz-File-Name", fileName)
                setRequestProperty("X-Bz-Content-Sha1", shaHash)
                doOutput = true
                outputStream.write(imageData)
                Log.d(TAG, responseCode.toString())

                json = JSONObject(jsonDecode(inputStream))

                // retrieve data from b2 json
                fileID = json["fileId"] as String


                // log what we got from b2
                Log.d(TAG, json.toString())
                Log.d(TAG, fileID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * downloadFile(): download a file from b2
     *
     *  @ref: https://www.backblaze.com/b2/docs/b2_upload_file.html
     */
    private fun downloadFile(){
        // create connection, headers
        var url = URL("$downloadUrl/b2api/v2/b2_download_file_by_id?fileId=$fileID")
        lateinit var imageData: ByteArray

        try {
            // can we authorize with b2?
            (url.openConnection() as? HttpURLConnection)?.run{
                requestMethod = "GET"
                setRequestProperty("Authorization", accAuthToken)
                imageData = inputStream.readBytes()

                // convert imageData back to bitmap
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    /**
     * upload(): perform upload() to b2 using co-routines
     *
     * @usr String: UserID
     * @bt Bitmap: imageData
     * @hash String: sha1 hash of data
     * @time String: timeStamp of selection
     */
    suspend fun upload(usr: String, bt: Bitmap, hash: String, time: String): String{
        // initialize our variables
        userID = usr
        bitmap = bt

        shaHash = hash
        timeStamp = time

        Log.d(TAG, "uploading file...")
        withContext(Dispatchers.IO){
            authorize()
            getUploadUrl()
            uploadFile()
        }
        return fileID
    }

    /**
     * download(): perform download to b2 using co-routines
     *
     * @fid String: fileId to download from B2
     */
    suspend fun download(fid: String): Bitmap{
        fileID = fid

        Log.d(TAG, "downloading file...")
        withContext(Dispatchers.IO){
            authorize()
            downloadFile()
        }
        return bitmap
    }
}
