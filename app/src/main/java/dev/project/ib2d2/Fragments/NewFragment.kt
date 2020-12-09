package dev.project.ib2d2.Fragments

import android.app.Activity.RESULT_OK
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.project.ib2d2.CreateBackupActivity
import dev.project.ib2d2.R

private val IMAGE_GALLERY_REQUEST_CODE = 20
private val IMAGE_CAMERA_REQUEST_CODE = 21

class NewFragment : Fragment() {
    private val TAG = javaClass.name
    private lateinit var b_selectImage: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.new_tab, container, false)

        b_selectImage = rootView.findViewById(R.id.selectImage)
        b_selectImage.setOnClickListener {
            // ask user camera or gallery as input
            val alertDialog: AlertDialog? = activity?.let {
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
                                IMAGE_CAMERA_REQUEST_CODE, rootView)

                            if(cameraPermissions){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, IMAGE_CAMERA_REQUEST_CODE)
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
                                IMAGE_GALLERY_REQUEST_CODE, rootView)

                            // did they have permission?
                            if(galleryPermissions){
                                // spawn an image picker
                                val intent = Intent()
                                intent.setType("image/*")
                                intent.setAction(Intent.ACTION_GET_CONTENT)
                                startActivityForResult(Intent.createChooser(intent, "Select Photo"), IMAGE_GALLERY_REQUEST_CODE)
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
        return rootView
    }

    /**
     * postImageSelectionSuccess(): handle transition to CreateBackup
     *
     *  @bitmap Bitmap: bitmap data of image
     */
    private fun postImageSelectionSuccess(imageUri: Uri){
        // create intent and add the imageUri to it
        val intent = Intent(context!!, CreateBackupActivity::class.java)
        intent.putExtra("IMAGE_URI", imageUri.toString())

        // customize animation then send us over
        val options = ActivityOptions.makeCustomAnimation(context!!, R.anim.right_in, R.anim.left_out)
        startActivity(intent, options.toBundle())
    }

    /**
     * OnActivityResultListener: callback for image picker(s)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the RESULT_OK and if the data exists
        if(resultCode == RESULT_OK && data != null){
            when(requestCode){
                IMAGE_CAMERA_REQUEST_CODE -> {
                    // get the bitmap
                    val bitmap = data.extras?.get("data") as Bitmap

                    // store the image the camera took then send the imageUri to CreateBackup
                    val imageUri = Uri.parse(MediaStore.Images.Media
                            .insertImage(context!!.contentResolver, bitmap, "ib2d2 Image Capture", null))

                    Log.d(TAG, "Image created: ${imageUri}")
                    postImageSelectionSuccess(imageUri)
                }
                IMAGE_GALLERY_REQUEST_CODE -> {
                    // get selected imageUri and send to CreateBackup
                    val imageUri = data.data!!
                    Log.d(TAG, "Image selected: ${imageUri}")
                    postImageSelectionSuccess(imageUri)
                }
                else -> {
                    Log.d(TAG, "GOT HERE X")
                }
            }

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
    private fun checkAppPermissions(permission: String, permissionText: String, requestCode: Int, currentView: View): Boolean{
        when{
            activity?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED -> {
                // if there is permission... proceed
                Log.d(TAG, "Success: User has permission to the ${permissionText}")
                return true
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // if there is no permission, but we have been here before.. let them know
                informAppPermissions(permissionText)
                Toast.makeText(currentView.context, "App requires camera/gallery access", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                // if no permission... inform user and prompt them
                Log.d(TAG, "Failure: User does not have permission to the ${permissionText}")
                Toast.makeText(currentView.context, "App requires camera/gallery access", Toast.LENGTH_SHORT).show()
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
        val alertDialog: AlertDialog? = activity?.let {
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