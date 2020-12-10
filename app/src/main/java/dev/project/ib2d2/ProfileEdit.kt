package dev.project.ib2d2

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
private val IMAGE_GALLERY_REQUEST_CODE = 20
private val IMAGE_CAMERA_REQUEST_CODE = 21


class ProfileEdit : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var selectAvatar: Button
    private lateinit var saveProfile: Button
    private lateinit var bitmap: Bitmap
    private var imageSelected = false

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_layout)

        // Load stored preferences (data) on device
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)

        // show and configure tool bar
        val mTopToolbar = findViewById(R.id.profileToolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle("Edit Profile")

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        selectAvatar = findViewById(R.id.selectAvatar)
        selectAvatar.setOnClickListener {
            // ask user camera or gallery as input
            val alertDialog: AlertDialog? = this?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setTitle("Choose image source")
                    // the camera button
                    setPositiveButton("Camera",
                        DialogInterface.OnClickListener { dialog, id ->
                            // check if there are permissions for the camera
                            val cameraPermissions = checkAppPermissions(
                                android.Manifest.permission.CAMERA,
                                "CAMERA",
                                IMAGE_CAMERA_REQUEST_CODE)

                            if(cameraPermissions){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent,
                                    IMAGE_CAMERA_REQUEST_CODE
                                )
                            } else {
                                Log.w(TAG, "app does not have permission to: camera")

                            }
                        })

                    // the gallery button
                    setNegativeButton("Gallery",
                        DialogInterface.OnClickListener { dialog, id ->
                            // check if there are permissions for the gallery
                            val galleryPermissions = checkAppPermissions(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                "GALLERY",
                                IMAGE_GALLERY_REQUEST_CODE)

                            // did they have permission?
                            if(galleryPermissions){
                                // spawn an image picker
                                val intent = Intent()
                                intent.setType("image/*")
                                intent.setAction(Intent.ACTION_GET_CONTENT)
                                startActivityForResult(Intent.createChooser(intent, "Select Photo"),
                                    IMAGE_GALLERY_REQUEST_CODE)
                            } else {
                                Log.w(TAG, "app does not have permission to: gallery")

                            }
                        })
                }

                // create and show the dialog
                builder.create()
                builder.show()
            }
        }

        saveProfile = findViewById(R.id.saveProfile)
        if(!imageSelected) {
            // if there was no image selected, try setting just the display name
            saveProfile.setOnClickListener {
                Log.d(TAG, "GOT HIT")
            }
        }

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    /**
     * OnActivityResultListener: callback for image picker(s)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the RESULT_OK and if the data exists
        if(resultCode == Activity.RESULT_OK && data != null){
            when(requestCode){
                IMAGE_CAMERA_REQUEST_CODE -> {
                    selectAvatar.text = "camera IMAGE SELECTED"
                    Log.d(TAG, "USER SELECTED CAMERA")

                    // get the bitmap
                    bitmap = data.extras?.get("data") as Bitmap
                    imageSelected = true

                }
                IMAGE_GALLERY_REQUEST_CODE -> {
                    selectAvatar.text = "GALLERY IMAGE SELECTED"
                    Log.d(TAG, "USER SELECTED GALLERY")

                    // get selected imageUri and create a bitmap
                    val imageUri = data.data!!
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageSelected = true
                    postImageSelectionSuccess()
                }
                else -> {
                    Log.d(TAG, "Could not retrieve image")
                }
            }

        }
    }

    /**
     * postImageSelectionSuccess(): handle the image
     *
     *  @bitmap Bitmap: bitmap data of image
     */
    private fun postImageSelectionSuccess(){
        saveProfile.setOnClickListener {
            Log.d(TAG, bitmap.toString())

        }

    }


    // permission management
    // @ref: https://developer.android.com/training/permissions/requesting#kotlin

    /**
     * checkAppPermissions(): check for access to apis
     *
     *  @permission String: the permission we are requesting (android defined)
     *  @permissionText String: pretty name to describe the permission
     *  @requestCode Int: arbitrary code to describe asset requested
     */
    private fun checkAppPermissions(permission: String, permissionText: String, requestCode: Int): Boolean{
        when{
            applicationContext?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED -> {
                // if there is permission... proceed
                Log.d(TAG, "Success: User has permission to the ${permissionText}")
                return true
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // if there is no permission, but we have been here before.. let them know
                informAppPermissions(permissionText)
                Toast.makeText(this, "App requires camera/gallery access", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                // if no permission... inform user and prompt them
                Log.d(TAG, "Failure: User does not have permission to the ${permissionText}")
                Toast.makeText(this, "App requires camera/gallery access", Toast.LENGTH_SHORT).show()
                requestPermissions(arrayOf(permission), requestCode)
                return false
            }
        }
    }



    /**
     * informAppPermissions(): inform user of access required
     *
     *  @permissionText String: pretty name to describe the permission
     */
    private fun informAppPermissions(permissionText: String){
        // provide info on why we need access
        val alertDialog: AlertDialog? = applicationContext?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Additional Access Required")
                setMessage(getString(R.string.badAccess))
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }

            // Create the AlertDialog
            builder.create()
            builder.show()
        }
    }


}