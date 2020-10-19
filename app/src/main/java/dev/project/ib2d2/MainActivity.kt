package dev.project.ib2d2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var signUpButton: Button
    private lateinit var confirmPasswordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.login_button)
        usernameText = findViewById(R.id.editText_username)
        passwordText = findViewById(R.id.editText_password)
        createAccountButton = findViewById(R.id.createAccount_button)

        createAccountButton.setOnClickListener {
            setContentView(R.layout.register_layout)

            // TODO function to initialize the components in each layout
            signUpButton = findViewById(R.id.signUp_button)
            confirmPasswordText = findViewById(R.id.editText_confirmPassword)
            usernameText = findViewById(R.id.editText_username)
            passwordText = findViewById(R.id.editText_password)

            signUpButton.setOnClickListener {
                if(confirmPasswordText.text.toString() == passwordText.text.toString()) {
                    signUp(usernameText, passwordText, confirmPasswordText)
                }
            }
        }
    }

    private fun signUp(username: EditText, password: EditText, confirmPassword: EditText) {

        // TODO Passwords must always be hashed before being saved, this is for testing atm
        if(username.text.toString() != "" && password.text.toString() != "" ) {
            val testUser = hashMapOf(
                "username" to username.text.toString(),
                "password" to password.text.toString(),
            )
            db.collection("test")
                .add(testUser)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Document added with ID ${documentReference.id}")
                    username.setText("")
                    password.setText("")
                    confirmPassword.setText("")
                    setContentView(R.layout.home_layout)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}