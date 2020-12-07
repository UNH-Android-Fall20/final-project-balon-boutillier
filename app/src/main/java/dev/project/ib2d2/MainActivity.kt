package dev.project.ib2d2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var signUpButton: Button
    private lateinit var registerBackButton: Button
    private lateinit var rootLoginButton: Button

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    private var wrongPasswordUsername: String = "Incorrect username or password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load stored preferences (data) on device
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)

        loginScreen()
    }

    /**
     * loginScreen(): Interface to login or create an account
     */
    private fun loginScreen() {
        loginButton = findViewById(R.id.login_button)
        createAccountButton = findViewById(R.id.createAccount_button)
        rootLoginButton = findViewById(R.id.rootLogin_button)

        // Send credentials to login
        loginButton.setOnClickListener {
            val username = (findViewById<EditText>(R.id.editText_username )).text.toString()
            val password = (findViewById<EditText>(R.id.editText_password)).text.toString()

            signIn(username, password)
        }

        // Prompt the user to create a new account
        createAccountButton.setOnClickListener {
            setContentView(R.layout.register_layout)

            // TODO function to initialize the components in each layout
            signUpButton = findViewById(R.id.signUp_button)
            registerBackButton = findViewById(R.id.registerBack_button)

            signUpButton.setOnClickListener {
                val username = (findViewById<EditText>(R.id.editText_username)).text.toString()
                val password = (findViewById<EditText>(R.id.editText_password)).text.toString()
                val confirmPassword = (findViewById<EditText>(R.id.editText_confirmPassword)).text.toString()

                if(confirmPassword == password) {
                    signUpLegacy(username, password, confirmPassword)
                } else {
                    Toast.makeText(this, "Passwords need to match", Toast.LENGTH_SHORT).show()
                }
            }

            // app is crashing because we call loginScreen() without reloading intent
            // TODO : back button crashes app zzzz
            registerBackButton.setOnClickListener {
                loginScreen()
            }
        }

        /* Temporarily add a root login button to override having to sign in each time */
        rootLoginButton.setOnClickListener {
            signIn("test", "test")
        }
    }

    /**
     * signUp(): Creates an account for a new user
     *
     * @email EditText: User's email to login with
     * @password EditText: User's password to login with
     * @confirmPassword EditText: User's password confirmed
     */
    private fun signUp(email: String, password: String, confirmPassword: String){



    }


    /**
     * signUpLegacy(): Creates an account for a new user
     * - old method for creating user accounts
     * @username EditText: User's username to login with
     * @password EditText: User's password to login with
     * @confirmPassword EditText: User's password confirmed
     */
    private fun signUpLegacy(username: String, password: String, confirmPassword: String) {
        // TODO Passwords must always be hashed before being saved, this is for testing atm
        if(username.isNotEmpty() && password.isNotEmpty() ) {
            val testUser = hashMapOf(
                "username" to username,
                "password" to password,
            )

            // TODO check if username is unique
            db.collection("users").document(username).set(testUser)
                .addOnSuccessListener {
                    Log.d(TAG, "Document added")

                    // Automatically sign the user in after successful account creation
                    Toast.makeText(this, "Account Created, Signing you in...", Toast.LENGTH_SHORT).show()
                    signIn(username, password)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * signIn(): Authenticates user to app
     *
     * @username String: User's username to login with
     * @password String: User's password to login with
     */
    private fun signIn(username: String, password: String) {
        db.collection("users").document(username).get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    Log.d(TAG, "${user.data}")
                    if (user.data?.get("password") == password) {

                        // Save the username of the person logging in locally
                        var editor = prefs!!.edit()
                        editor.putString("USERNAME", username)
                        editor.apply()

                        // Spawn the NavController after successful authentication
                        val intent = Intent(this, NavController::class.java)
                        startActivity(intent)

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
}