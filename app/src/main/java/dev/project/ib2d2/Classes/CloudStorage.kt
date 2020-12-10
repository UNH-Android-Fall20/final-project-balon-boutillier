package dev.project.ib2d2.Classes

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CloudStorage {
    private val TAG = javaClass.name

    // data for upload
    private lateinit var fileName: String
    private lateinit var bitmap: Bitmap
    private lateinit var shaHash: String
    private lateinit var timeStamp: String

   // init firebase
    var storage = FirebaseStorage.getInstance()

    /**
     * uploadFile(): upload the file to firebase
     *
     *  @ref: https://firebase.google.com/docs/storage/android/upload-files
     */
    private fun uploadFile(){
        // Create a storage reference from our app
        val storageRef = storage.reference

        // create ref to file and to image
        val fileReference = storageRef.child(fileName)
        val fileImgReference = storageRef.child("images/$fileName")

        // While the file names are the same, the references point to different files
        fileReference.name == fileImgReference.name // true
        fileReference.path == fileImgReference.path // false

        // get our imageData from the bitmap
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val imageData = byteArray.toByteArray()

        // create metadata for the upload
        var metaData = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCustomMetadata("File-Hash", shaHash)
            .build()

        // do upload to firebase
        var uploadTask = fileReference.putBytes(imageData, metaData)
        uploadTask.addOnFailureListener {
            Log.d(TAG, "Error: file could not be uploaded to firebase")
        }.addOnSuccessListener { taskSnapshot ->
            // do something here
        }
    }

    /**
     * upload(): perform upload to firebase storage
     *
     */
    suspend fun upload(fn: String, bt: Bitmap, hash: String, time: String){
        // initialize variables
        fileName = fn
        bitmap = bt
        shaHash = hash
        timeStamp = time

        Log.d(TAG, "uploading file to firebase...")
        withContext(Dispatchers.IO) {
            uploadFile()
        }
        Log.d(TAG, "Upload to Firebase complete")
    }



}