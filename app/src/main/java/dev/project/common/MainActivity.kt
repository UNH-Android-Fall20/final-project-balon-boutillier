package dev.project.common

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dev.project.ib2d2.R
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        val testUser = hashMapOf(
            "first" to "test",
            "last" to "user",
        )
        db.collection("test")
            .add(testUser)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Document added with ID ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}