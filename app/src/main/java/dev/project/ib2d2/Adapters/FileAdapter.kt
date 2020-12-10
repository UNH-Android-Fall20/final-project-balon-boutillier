package dev.project.ib2d2.Adapters

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.project.ib2d2.CreateBackupActivity
import dev.project.ib2d2.Models.Backup
import dev.project.ib2d2.R
import dev.project.ib2d2.ViewBackupActivity
import dev.project.ib2d2.Views.FileView


/* class to handle fileAdapter
 *
 * @ref: https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/
 * @ref: https://github.com/chefeleUNH/pizzahub-android-fall20-class/blob/master/app/src/main/java/edu/newhaven/pizzahub/controller/PizzeriaAdapter.kt
 */
class FileAdapter(options: FirestoreRecyclerOptions<Backup>)
    :FirestoreRecyclerAdapter<Backup, FileView>(options){
    private val TAG = javaClass.name

    // create the ViewHolder, inflate rows
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileView {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_entry, parent, false)

        return FileView(view)
    }

    /*
     * onBindViewHolder(): fills the data and handle clicks of each row
     */
    override fun onBindViewHolder(holder: FileView, position: Int, model: Backup) {
        // handle if a user clicks the buttons
        holder.itemView.setOnClickListener {
            // create intent and add the fileName
            val intent = Intent(holder.itemView.context, ViewBackupActivity::class.java)
            intent.putExtra("DATAMODEL", model)

            // customize animation then send us over
            val options = ActivityOptions.makeCustomAnimation(holder.itemView.context, R.anim.right_in, R.anim.left_out)
            holder.itemView.context.startActivity(intent, options.toBundle())
        }

        // spawn a spinner to fill the area (as demonstrated in class)
        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        // Reference to an image file in Cloud Storage
        val storageRef = Firebase.storage.getReferenceFromUrl("gs://final-project-9c2ed.appspot.com/${model.fileName}")

        // get the imageUrl and set it to our Glide
        storageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide
                .with(holder.backupImage)
                .load(imageURL)
                .placeholder(circularProgressDrawable)
                .into(holder.backupImage)
        }).addOnFailureListener(OnFailureListener {
            // Handle any errors
        })

        holder.backupTitle.text = model.title
        holder.backupDesc.text = model.desc
    }

}
