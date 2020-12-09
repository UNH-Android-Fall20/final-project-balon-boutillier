package dev.project.ib2d2.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.project.ib2d2.R
import java.util.jar.Manifest

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
                    setPositiveButton("Camera",
                        DialogInterface.OnClickListener { dialog, id ->
                            // check if there are permissions for the camera
                            checkAppPermissions(
                                android.Manifest.permission.CAMERA,
                                "CAMERA",
                                IMAGE_CAMERA_REQUEST_CODE, rootView)
                        })
                    setNegativeButton("Gallery",
                        DialogInterface.OnClickListener { dialog, id ->
                            // check if there are permissions for the gallery
                            checkAppPermissions(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                "GALLERY",
                                IMAGE_GALLERY_REQUEST_CODE, rootView)
                        })
                }

                // Create the AlertDialog
                builder.create()
                builder.show()
            }



            // open the gallery and let the user select an image
            //val gallery = Intent()
            //gallery.setType("image/*")
            //gallery.setAction(Intent.ACTION_GET_CONTENT)

        }

        /*
        1. (X) build an alert dialog to select camera or gallery
        1. (X) check permissions before each time we use camera
        4. if the check succeeds we can open the camera and do what we need here and in the profile sec
        5. store images appropriate, everything goes in the files collection for storage
         */


        return rootView
    }

    // permission management
    // @ref: https://developer.android.com/training/permissions/requesting#kotlin

    /**
     * checkAppPermissions(): check for access to apis
     *
     *  @permission String: the permission we are requesting (android defined)
     *  @permissionText String: pretty name to describe the permission
     *  @requestCode Int: arbitrary code to describe asset requested
     *
     */
    private fun checkAppPermissions(permission: String, permissionText: String, requestCode: Int, currentView: View){
        when{
            activity?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED -> {
                // if there is permission... proceed
                Log.d(TAG, "Success: User has permission to the ${permissionText}")
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // if there is no permission, but we have been here before.. let them know
                informAppPermissions(permissionText)
            }
            else -> {
                // if no permission... inform user and prompt them
                Log.d(TAG, "Failure: User does not have permission to the ${permissionText}")
                Toast.makeText(currentView.context, "This app requires camera/gallery access to function", Toast.LENGTH_SHORT).show()
                requestPermissions(arrayOf(permission), requestCode)
            }
        }
    }

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