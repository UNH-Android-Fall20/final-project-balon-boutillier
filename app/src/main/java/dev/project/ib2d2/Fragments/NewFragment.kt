package dev.project.ib2d2.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
            checkAppPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, "GALLERY", IMAGE_GALLERY_REQUEST_CODE)

            // open the gallery and let the user select an image
            //val gallery = Intent()
            //gallery.setType("image/*")
            //gallery.setAction(Intent.ACTION_GET_CONTENT)

        }

        /*
        1. build an alert dialog to select camera or gallery
        1. check permissions before each time we use camera
        2. set up postLoginSuccess() to request permissions for app before we open theseparts
        3. if the check fails, log the user out and require them to log back in with that error
        4. if the check succeeds we can open the camera and do what we need here and in the profile sec
        5. store images appropriate, everything goes in the files collection for storage
         */


        return rootView
    }

    // permission management
    // @ref: https://developer.android.com/training/permissions/requesting#kotlin

    /**
     * checkAppPermissions(): check for access to apis
     *  - checks for camera access
     *  - checks for gallery access
     *
     *  @permission String:
     *
     *  returns: true - has permission, false - no permission
     */
    private fun checkAppPermissions(permission: String, permissionText: String, requestCode: Int): Boolean{
        // check if the user has permission
        if(activity?.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Failure: User does not have permission to the ${permissionText}")
            requestPermissions(arrayOf(permission), requestCode)

            // check recursively if we have permission or not
            checkAppPermissions(permission, permissionText, requestCode)
            return true
        } else {
            Log.d(TAG, "Success: User has permission to the ${permissionText}")
            return true
        }
    }
}