package dev.project.ib2d2

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.project.ib2d2.Classes.BackBlaze
import dev.project.ib2d2.Classes.CloudStorage
import dev.project.ib2d2.Models.Backup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewBackupActivity : AppCompatActivity() {
    private val TAG = javaClass.name

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    // buttons, images, and lateinits
    private lateinit var fbImage: ImageView
    private lateinit var b2Image: ImageView
    private lateinit var titleText: TextView
    private lateinit var descText: TextView
    private lateinit var createdText: TextView
    private lateinit var hashText: TextView
    private lateinit var timeText: TextView
    private lateinit var model: Backup
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewbackup_layout)

        // show and configure tool bar
        val mTopToolbar = findViewById(R.id.viewToolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle("Manage your Backup")

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);


        // get the image and show it to the user
        val extras = getIntent().getExtras()
        if( extras != null ){
            model = extras.getSerializable("DATAMODEL") as Backup
        }

        // populate the page for the user
        populateBackup()

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }


    /**
     * populateBackup(): populates the view backup page
     *
     */
    private fun populateBackup(){
        // get the image views and text views
        fbImage = findViewById(R.id.fbImage)
        b2Image = findViewById(R.id.b2Image)
        titleText = findViewById(R.id.titleText)
        descText = findViewById(R.id.descText)
        createdText = findViewById(R.id.createdText)
        hashText = findViewById(R.id.shaText)
        timeText = findViewById(R.id.timeText)

        // fill the text views first
        titleText.text = model.title
        descText.text = model.desc
        createdText.text = model.createdBy
        hashText.text = model.shaHash
        timeText.text = model.timeStamp

        // spawn a spinner to fill the area (as demonstrated in class)
        val circularProgressDrawable = CircularProgressDrawable(applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        // Reference to an image file in Cloud Storage
        val storageRef = Firebase.storage.getReferenceFromUrl("gs://final-project-9c2ed.appspot.com/${model.fileName}")

        // get the imageUrl and set it to our Glide
        storageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide
                .with(fbImage)
                .load(imageURL)
                .placeholder(circularProgressDrawable)
                .into(fbImage)
        }).addOnFailureListener(OnFailureListener {
            // Handle any errors
        })


        // get the image from b2 via coroutine
        CoroutineScope(Dispatchers.IO).launch{
                // upload to backblaze
                val b2 = BackBlaze()
                val b2Img = b2.download(model.fileName)

            launch(Dispatchers.Main){
                Glide
                    .with(b2Image)
                    .load(b2Img)
                    .placeholder(circularProgressDrawable)
                    .into(b2Image)
            }
        }
    }
}