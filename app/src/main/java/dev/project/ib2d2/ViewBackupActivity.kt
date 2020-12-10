package dev.project.ib2d2

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.project.ib2d2.Classes.BackBlaze
import dev.project.ib2d2.Models.Backup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewBackupActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

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

        // get the b2 image and put it in the view
        b2Image = findViewById(R.id.b2Image)
        b2Image.setOnClickListener{
            // create intent and add the fileName
            val intent = Intent(applicationContext, ImageViewer::class.java)
            intent.putExtra("IMAGE_TITLE", model.title)
            intent.putExtra("IMAGE_NAME", model.fileName)
            intent.putExtra("METHOD", "backblaze")

            // customize animation then send us over
            val options = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.right_in, R.anim.left_out)
            startActivity(intent, options.toBundle())
        }

        // get the fb image and put it in the view
        fbImage = findViewById(R.id.fbImage)
        fbImage.setOnClickListener{
            // create intent and add the fileName
            val intent = Intent(applicationContext, ImageViewer::class.java)
            intent.putExtra("IMAGE_TITLE", model.title)
            intent.putExtra("IMAGE_NAME", model.fileName)
            intent.putExtra("METHOD", "firebase")

            // customize animation then send us over
            val options = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.right_in, R.anim.left_out)
            startActivity(intent, options.toBundle())
        }

        // populate the page for the user
        populateBackup()

        // have a button to remove backups per request
        deleteButton = findViewById(R.id.deleteBackup_button)
        deleteButton.setOnClickListener {
            Log.d(TAG, "got here")
            db.collection("files")
                .whereEqualTo("fileName", model.fileName)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) {
                        db.collection("files").document(doc.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Backup Deleted", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener{ e-> Log.d(TAG, "Error: ", e)}
                    }
                }
                .addOnFailureListener{e ->
                    Log.d(TAG, "Error getting document: ", e)
                }

        }

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    /**
     * populateBackup(): populates the view backup page
     */
    private fun populateBackup(){
        // get the text views
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