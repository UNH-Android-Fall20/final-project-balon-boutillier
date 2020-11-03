package dev.project.ib2d2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var signUpButton: Button
    private lateinit var confirmPasswordText: EditText
    private lateinit var registerBackButton: Button

    private var wrongPasswordUsername: String = "Incorrect username or password"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.page_1 -> {
                bottomScreenChange(R.layout.files_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_2 -> {
                bottomScreenChange(R.layout.profile_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_3 -> {
                bottomScreenChange(R.layout.home_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_4 -> {
                bottomScreenChange(R.layout.settings_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_5 -> {
                bottomScreenChange(R.layout.about_layout)
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            loginScreen()
        }

    private fun loginScreen() {
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.login_button)
        usernameText = findViewById(R.id.editText_username)
        passwordText = findViewById(R.id.editText_password)
        createAccountButton = findViewById(R.id.createAccount_button)

        loginButton.setOnClickListener {
            signIn(usernameText, passwordText)
        }

        createAccountButton.setOnClickListener {
            setContentView(R.layout.register_layout)

            // TODO function to initialize the components in each layout
            signUpButton = findViewById(R.id.signUp_button)
            confirmPasswordText = findViewById(R.id.editText_confirmPassword)
            usernameText = findViewById(R.id.editText_username)
            passwordText = findViewById(R.id.editText_password)
            registerBackButton = findViewById(R.id.registerBack_button)

            signUpButton.setOnClickListener {
                if(confirmPasswordText.text.toString() == passwordText.text.toString()) {
                    signUp(usernameText, passwordText, confirmPasswordText)
                } else {
                    Toast.makeText(this, "Passwords need to match", Toast.LENGTH_SHORT).show()
                }
            }

            registerBackButton.setOnClickListener {
                loginScreen()
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

            // TODO check if username is unique
            db.collection("users").document(username.text.toString()).set(testUser)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Document added with hash code ${documentReference.hashCode()}")
                    username.setText("")
                    password.setText("")
                    confirmPassword.setText("")
                    bottomScreenChange(R.layout.home_layout)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun signIn(username: EditText, password: EditText) {
        db.collection("users").document(username.text.toString()).get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    Log.d(TAG, "${user.data}")
                    if (user.data?.get("password") == password.text.toString()) {
                        bottomScreenChange(R.layout.home_layout)
                    } else {
                        Toast.makeText(this, wrongPasswordUsername, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "Document not found")
                    Toast.makeText(this, wrongPasswordUsername, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "$exception")
                Toast.makeText(this, "Database is down sorry", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bottomScreenChange(layout: Int) {
        setContentView(layout)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}