package dev.project.ib2d2

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.createbackup_layout.*

class CreateBackupActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.createbackup_layout)

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
        }






        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }
}
