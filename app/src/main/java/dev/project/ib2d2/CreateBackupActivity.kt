package dev.project.ib2d2

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.project.ib2d2.Classes.BackBlaze
import kotlinx.android.synthetic.main.createbackup_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CreateBackupActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var bitmap: Bitmap
    private lateinit var b_createBackup: Button

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

        /* todo: implement as follows
         *  (1) check the fields, receive the data [done]
         *  (2) create sha hash, timestamp [done]
         *  (3) upload to backblaze
         *      - create an upload() func [done]
         *      - create a download() func
         *  (4) upload to firestore cloud storage
         *  (5) take all data and submit to the files collection
         *  (6) show dialog while working
         *  (7) pop out to the main screen again
         *
         * (?) encryption: have user make local pw
         * (?) do not upload to firestore
         * (?) enter in settings
         * (?) AES data
         */
        // handle the createBackup button
        b_createBackup.setOnClickListener{
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
                        .ofPattern("yyyy_MM_dd_hh_mm_ss")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now())

                    // create sha1 hash of image
                    val shaHash = sha1Hash(bitmap)
                    Log.d(TAG, timeStamp)
                    Log.d(TAG, shaHash)

                    // create Backblaze object
                    val b2 = BackBlaze()
                    var userID = prefs?.getString("USERID", "NULL")


                    // run the backup code necessary
                    val doBackup = GlobalScope.launch {
                        if (userID != null) {
                            b2.upload(userID, bitmap, title, desc, shaHash, timeStamp)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArray)
        return MessageDigest.getInstance("SHA-1")
            .digest(byteArray.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
