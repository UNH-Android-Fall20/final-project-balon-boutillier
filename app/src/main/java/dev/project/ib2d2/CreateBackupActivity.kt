package dev.project.ib2d2

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Contacts
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dev.project.ib2d2.Classes.BackBlaze
import dev.project.ib2d2.Classes.CloudStorage
import kotlinx.android.synthetic.main.createbackup_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CreateBackupActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()
    private lateinit var bitmap: Bitmap
    private lateinit var b_createBackup: Button
    private lateinit var uploadProgress: ProgressBar

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createbackup_layout)

        // Load stored preferences (data) on device
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)

        // show and configure tool bar
        val mTopToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle("Create New Backup")

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        // get the image and show it to the user
        val extras = getIntent().getExtras()
        if( extras != null ){
            val imageUri = Uri.parse(extras.getString("IMAGE_URI"))
            Log.d(TAG, imageUri.toString())
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            backupImage.setImageBitmap(bitmap)
        } else {
            // provide toast, and go back
        }

        backupHandler(bitmap)

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    /**
     * backupHandler(): handles the backup routine
     *  - watches for createBackup to be called
     *  - creates necessary information to make backup
     *  - uploads to b2 and firestore
     *  - creates collection entry for backup
     *
     *  @bitmap Bitmap: data of image selection
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun backupHandler(bitmap: Bitmap){
        b_createBackup = findViewById(R.id.createBackup_button)
        uploadProgress = findViewById(R.id.uploadProgress)

        uploadProgress.visibility = View.INVISIBLE

        /* ideas --
         * (?) encryption: have user make local pw
         * (?) do not upload to firestore
         * (?) enter in settings
         * (?) AES data
         */

        // handle the createBackup button
        b_createBackup.setOnClickListener{
            uploadProgress.visibility = View.VISIBLE
            Log.d(TAG, "Running backup routine...")
            val title = titleBackup.editText?.text.toString()
            val desc = descBackup.editText?.text.toString()

            // validate user input
            when {
                (title.isBlank()) -> Toast.makeText(this, "Error: Select a title for your backup", Toast.LENGTH_SHORT).show()
                (desc.isBlank()) -> Toast.makeText(this, "Error: Select a description for your backup", Toast.LENGTH_SHORT).show()
                else -> {
                    // create timestamp
                    val timeStamp = DateTimeFormatter
                        .ofPattern("yyyy_MM_dd_hh_mm_ss_SSS")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now())

                    // create sha1 hash of image
                    val shaHash = sha1Hash(bitmap)
                    Log.d(TAG, timeStamp)
                    Log.d(TAG, shaHash)

                    // get userID
                    var userID = prefs?.getString("USERID", "NULL")

                    // create the fileName
                    val fileName = userID + "_" + timeStamp

                    // call coroutine to thread fileUpload
                    CoroutineScope(IO).launch{
                        if (userID != null) {
                            // upload to backblaze
                            val b2 = BackBlaze()
                            b2.upload(fileName, bitmap, shaHash, timeStamp)

                            // upload to firebase cloud storage
                            val firebase = CloudStorage()
                            firebase.upload(fileName, bitmap, shaHash, timeStamp)
                        }
                        launch(Main){
                            val fileData = mutableMapOf(
                                "createdBy" to userID,
                                "timeStamp" to timeStamp,
                                "fileName" to fileName,
                                "shaHash" to shaHash,
                                "title" to title,
                                "desc" to desc
                            )

                            // create a new document in files collection
                            db.collection("files").document(fileName)
                                .set(fileData)

                            Toast.makeText(applicationContext, "Backup Created Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    /**
     * sha1Hash(): create sha1 hash of bitmap
     *
     *  @bitmap: user selected image
     *
     *  @ref: https://developer.android.com/reference/kotlin/java/security/MessageDigest
     */
    private fun sha1Hash(bitmap: Bitmap): String    {
        // convert bitmap to ByeArray
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        return MessageDigest.getInstance("SHA-1")
            .digest(byteArray.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
