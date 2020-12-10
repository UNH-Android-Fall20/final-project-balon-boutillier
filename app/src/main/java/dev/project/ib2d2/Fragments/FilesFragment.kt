package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.project.ib2d2.Adapters.FileAdapter
import dev.project.ib2d2.Models.Backup
import dev.project.ib2d2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class FilesFragment() : Fragment() {
    private val TAG = javaClass.name
    private lateinit var fileAdapter: FileAdapter
    private val db = FirebaseFirestore.getInstance()

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.files_tab, container, false)

        // load prefs and get userID
        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)
        var userID = prefs?.getString("USERID", "NULL")

        val query: Query = db
            .collection("files")
            .whereEqualTo("createdBy", userID)
            .orderBy("timeStamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Backup>()
                .setQuery(query, Backup::class.java)
                .build()

        fileAdapter = FileAdapter(options)

        val fileList = rootView.findViewById(R.id.files_list) as RecyclerView
        fileList.adapter = fileAdapter
        fileList.layoutManager = LinearLayoutManager(rootView.context)

        return rootView
    }

    override fun onStart() {
        super.onStart()
        fileAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        fileAdapter.stopListening()
    }
}