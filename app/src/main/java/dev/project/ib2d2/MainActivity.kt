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
            // get the user data from our prefs from last sign-in
            val savedUserID = prefs?.getString("USERID", null)
            val savedUserDN = prefs?.getString("DISPLAYNAME", null)

            if (currentUser.uid == savedUserID) {
                // we have user data! no need to log in again
                Log.d(TAG, "Signed in user ${currentUser.uid}")
                Toast.makeText(this, "$savedUserDN auto signed in...", Toast.LENGTH_SHORT).show()
                postAuthenticationSuccess()
            } else {
                // report failed session start, send to login handler
                Log.d(TAG, "UID Mis-match, signing the user out...")
                Toast.makeText(this, "Session signed out, please login again", Toast.LENGTH_SHORT).show()
                auth.signOut()

                // send user to login handler
                loginHandler()
            }
        } else {
            Log.d(TAG, "No user found, sending to loginHandler()")
            loginHandler()
        }
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
           loginUser("admin@b2d2.dev", "password123")
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
                    val userID = auth.currentUser?.uid
                    if (userID != null) {
                        Log.w(TAG, "Creating collection for  ${userID}, and signing them in")
                        postRegisterAddToCollection(userID, email)
                        loginUser(email, password)
                    } else {
                        Log.w(TAG, "Variable: userID was null before collection creation")
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Error: Password must be 6 characters", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this, "Error: Invalid email address specified", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(this, "Error: User already exists with that email", Toast.LENGTH_SHORT).show()
                        else -> {
                            Toast.makeText(this, "Error: Unknown error occurred, contact the developer", Toast.LENGTH_SHORT).show()
                            Log.w(TAG, "Unhandled error occurred: ${task.exception}")
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
                    Log.d(TAG, "User ${email} signed in successfully")
                    val userID = auth.currentUser?.uid
                    if (userID != null) {
                        // get the users collection data
                        val userData = db.collection("users").document(userID)
                        Log.d(TAG, "Retrieving collection data for ${userID}")
                        userData.get()
                            .addOnSuccessListener { doc ->
                                if (doc != null){
                                    Log.d(TAG, "Document retrieved: ${doc.data}")

                                    // save it to our local prefs
                                    var editor = prefs!!.edit()
                                    editor.putString("USERID", doc.data?.get("userID") as String?)
                                    editor.putString("DISPLAYNAME", doc.data?.get("displayName") as String?)
                                    editor.putString("EMAILADDR", doc.data?.get("emailAddress") as String?)
                                    editor.apply()
                                } else {
                                    Log.d(TAG, "Failed to retrieve user information from collection")
                                }
                            }
                    } else {
                        Log.d(TAG, "Failed to retrieve user information from login")
                    }
                    // now we are signed in, show them the app
                    postAuthenticationSuccess()
                } else {
                    Log.d(TAG, "Users ${email} failed to login because of reason: ${task.exception}")
                    Toast.makeText(this, "Error: Invalid Email/Password ", Toast.LENGTH_SHORT).show()
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

    /**
     * postRegisterAddToCollection(): start nav bar and app
     *
     * @email String: User's email for new account created
     * @password String: User's password for new account created
     *
     * returns: true for success, false for failure
     */
    private fun postRegisterAddToCollection(userID: String, email: String) {
        val newUser = mutableMapOf(
            "userID" to userID,
            "emailAddress" to email,
            "displayName" to email,
            "profilePic" to null,
        )

        // add a user with doc name set to userID and attributes above
        db.collection("users").document(userID)
            .set(newUser)
            .addOnSuccessListener { task ->
                Log.d(TAG, "User ${email} successfully added to collection: ${task}")
            }
            .addOnFailureListener { task ->
                Log.d(TAG, "Failed to add user ${email} to collection due to reason: ${task}")
            }
    }
}