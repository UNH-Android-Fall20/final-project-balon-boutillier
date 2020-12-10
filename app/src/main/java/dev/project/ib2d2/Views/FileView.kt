package dev.project.ib2d2.Views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.project.ib2d2.R

/* FileView class */
class FileView(view: View) : RecyclerView.ViewHolder(view) {
    var backupImage: ImageView = view.findViewById(R.id.listBackupImage)
    var backupTitle: TextView = view.findViewById(R.id.listBackupTitle)
    var backupDesc: TextView = view.findViewById(R.id.listBackupDesc)
}