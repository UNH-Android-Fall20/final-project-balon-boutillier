package dev.project.ib2d2

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.project.ib2d2.Classes.BackBlaze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewer : AppCompatActivity() {
    private val TAG = javaClass.name

    // late inits
    private lateinit var imgView: ImageView
    private lateinit var title: String
    private lateinit var fileName: String
    private lateinit var method: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imageview_layout)

        // get the image and show it to the user
        val extras = getIntent().getExtras()
        if( extras != null ){
            title = extras.getString("IMAGE_TITLE").toString()
            fileName = extras.getString("IMAGE_NAME").toString()
            method = extras.getString("METHOD").toString()
        }

        // show and configure tool bar
        val mTopToolbar = findViewById(R.id.imageViewerToolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle(title)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        fillImage(method)

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    private fun fillImage(method: String){
        // get imageView
        imgView = findViewById(R.id.bigImage)


        // spawn a spinner to fill the area (as demonstrated in class)
        val circularProgressDrawable = CircularProgressDrawable(applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()


        when(method){
            "backblaze" -> {
                // get the image from b2 via coroutine
                CoroutineScope(Dispatchers.IO).launch{
                    // upload to backblaze
                    val b2 = BackBlaze()
                    val b2Img = b2.download(fileName)

                    launch(Dispatchers.Main){
                        Glide
                            .with(imgView)
                            .load(b2Img)
                            .placeholder(circularProgressDrawable)
                            .into(imgView)
                    }
                }
            }
            "firebase" -> {
                // Reference to an image file in Cloud Storage
                val storageRef = Firebase.storage.getReferenceFromUrl("gs://final-project-9c2ed.appspot.com/${fileName}")

                // get the imageUrl and set it to our Glide
                storageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                    val imageURL = uri.toString()
                    Glide
                        .with(imgView)
                        .load(imageURL)
                        .placeholder(circularProgressDrawable)
                        .into(imgView)
                }).addOnFailureListener(OnFailureListener {
                    // Handle any errors
                })
            }
            else -> Log.d(TAG, "Error: Invalid image sent")
        }
    }
}