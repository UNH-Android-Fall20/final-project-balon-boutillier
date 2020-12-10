package dev.project.ib2d2.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
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

class ProfileEditFragment : Fragment() {
    private val TAG = javaClass.name

    private lateinit var rootView: View
    private lateinit var saveEditProfileButton: Button

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.profile_edit_layout, container, false)

        // Load stored preferences (data) on device
        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        //saveEditProfileButton = rootView.findViewById(R.id.save_edit_profile)

        // TODO save the edits of the profile page
        saveEditProfileButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileFragment())?.commit()
        }

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