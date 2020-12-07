package dev.project.ib2d2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.project.ib2d2.Fragments.*

class NavController : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Create bottom nav
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Call default fragment upon NavController() being executed
        val newFragment = NewFragment()
        setCurrentFragment(newFragment)
    }

    /**
     * Handle actions on the bottom nav bar
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.tab_files -> {
                val filesFragment = FilesFragment()
                setCurrentFragment(filesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_profile -> {
                val profileFragment = ProfileFragment()
                setCurrentFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_new -> {
                val newFragment = NewFragment()
                setCurrentFragment(newFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_settings -> {
                val settingsFragment = SettingsFragment()
                setCurrentFragment(settingsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_about -> {
                val aboutFragment = AboutFragment()
                setCurrentFragment(aboutFragment)
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

    /**
     * setCurrentFragment(): fill container with fragment
     * @fragment Fragment: the fragment to switch to
     */
    private fun setCurrentFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}