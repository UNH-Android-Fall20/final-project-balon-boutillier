package dev.project.ib2d2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
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

        // initiate Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        if(currentUser != null) {
            Log.d(TAG, "attempting session based sign-in")
            Log.d(TAG, "${currentUser} : ${currentUser.uid}")

            auth.signOut()
        } else {
            Log.d(TAG, "no user found")
        }

        loginHandler()
    }

    /**
     * loginHandler(): handle the login screen
     *  - handles login, register, root login
     *  - sanitizes input before function calls
     *  - formats data before function calls
     */
    private fun loginHandler() {
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.createAccount_button)
        rootLoginButton = findViewById(R.id.rootLogin_button)

        // handle: loginButton (send to login screen)
        loginButton.setOnClickListener {
            val email = (findViewById<EditText>(R.id.editText_email)).text.toString()
            val password = (findViewById<EditText>(R.id.editText_password)).text.toString()

            // validate the user input
            when {
                (email.isBlank()) -> Toast.makeText(this, "Error: Email cannot be blank", Toast.LENGTH_SHORT).show()
                (password.isBlank()) -> Toast.makeText(this, "Error: Password cannot be blank", Toast.LENGTH_SHORT).show()
                (!email.isEmail()) -> Toast.makeText(this, "Error: Enter a valid email", Toast.LENGTH_SHORT).show()
                else -> {
                    loginUser(email, password)
                }
            }
        }

        // handle: registerAccount (prompt the user to create a new account)
        registerButton.setOnClickListener {
            setContentView(R.layout.register_layout)

            signUpButton = findViewById(R.id.signUp_button)
            registerBackButton = findViewById(R.id.registerBack_button)

            signUpButton.setOnClickListener {
                val email = (findViewById<EditText>(R.id.editText_email))?.text.toString()
                val password = (findViewById<EditText>(R.id.editText_password))?.text.toString()
                val confirmPassword = (findViewById<EditText>(R.id.editText_confirmPassword))?.text.toString()

                // validate the user input
                when {
                    (email.isBlank()) -> Toast.makeText(this, "Error: Email cannot be blank", Toast.LENGTH_SHORT).show()
                    (!email.isEmail()) -> Toast.makeText(this, "Error: Email address is not valid", Toast.LENGTH_SHORT).show()
                    (password.isBlank()) -> Toast.makeText(this, "Error: Password cannot be blank", Toast.LENGTH_SHORT).show()
                    (confirmPassword.isBlank()) -> Toast.makeText(this, "Error: Confirmation Password cannot be blank", Toast.LENGTH_SHORT).show()
                    (password != confirmPassword) -> Toast.makeText(this, "Error: Passwords must match", Toast.LENGTH_SHORT).show()
                    else -> {
                        registerUser(email, password)
                    }
                }
            }

            // sub-handle: go back to main login
            registerBackButton.setOnClickListener {
                setContentView(R.layout.activity_main)
                loginHandler()
            }
        }

        /* Temporarily add a root login button to override having to sign in each time */
        rootLoginButton.setOnClickListener {
            signInLegacy("test", "test")
        }
    }

    /**
     * registerUser(): Creates an account for a new user
     *
     * @email String: User's email to login with
     * @password String: User's password to login with
     */
    private fun registerUser(email: String, password: String) {
        Log.d(TAG, "Email: ${email}, Password: ${password}")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "successfully created a new user: ${email}")
                    loginUser(email, password)
                } else {
                    when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Error: Password must be 6 characters", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this, "Error: Invalid email address specified", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(this, "Error: User already exists with that email", Toast.LENGTH_SHORT).show()
                        else -> {
                            Toast.makeText(this, "Error: Unknown error occured, contact the developer", Toast.LENGTH_SHORT).show()
                            Log.w(TAG, "Unhandled error occured: ${task.exception}")
                        }
                    }
                }
            }
    }

    /**
     * loginUser(): login user account
     *
     * @email String: User's email to login with
     * @password String: User's password to login with
     */
    private fun loginUser(email: String, password: String) {
        Log.d(TAG, "Email: ${email}, Password: ${password}")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "successfully created a new user")
                    postAuthenticationSuccess()
                } else {
                    Log.d(TAG, "failed to login to server")
                }
            }
    }

    /**
     * isEmail(): validate if input is an email
     */
    private fun String.isEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    /**
     * postAuthenticationSuccess(): start nav bar and app
     */
    private fun postAuthenticationSuccess(){
        // Spawn the NavController after successful authentication
        val intent = Intent(this, NavController::class.java)
        startActivity(intent)
    }

    // legacy functions
    // ---------------------------------------------------------------------------------
    /**
     * signUpLegacy(): Creates an account for a new user
     *  - old method for creating user accounts
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
                    signInLegacy(username, password)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * signInLegacy(): Authenticates user to app
     *
     * @username String: User's username to login with
     * @password String: User's password to login with
     */
    private fun signInLegacy(username: String, password: String) {
        db.collection("users").document(username).get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    Log.d(TAG, "${user.data}")
                    if (user.data?.get("password") == password) {

                        // Save the username of the person logging in locally
                        var editor = prefs!!.edit()
                        editor.putString("USERNAME", username)
                        editor.apply()

                        postAuthenticationSuccess()
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