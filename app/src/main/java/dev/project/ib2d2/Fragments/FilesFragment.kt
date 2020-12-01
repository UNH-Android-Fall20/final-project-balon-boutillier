package dev.project.ib2d2.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import dev.project.ib2d2.R

class FilesFragment() : Fragment() {
    private lateinit var fileList: ListView
    // This is test data for the fileList
    private var testFiles = arrayOf("File 1", "File 2", "File 3")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.files_tab, container, false)
        fileList = rootView.findViewById(R.id.files_list)
        // TODO need a custom adapter for custom list items
        fileList.adapter = ArrayAdapter(rootView.context, R.layout.file_list_item, testFiles)
        return rootView
    }
}