package dev.project.ib2d2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.project.ib2d2.Fragments.AboutFragment
import dev.project.ib2d2.Fragments.FilesFragment

class NavActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.tab_files -> {
                val filesFragment = FilesFragment()
                setCurrentFragment(filesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_2 -> {
                //profileScreen()
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_3 -> {
                //bottomScreenChange(R.layout.home_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_4 -> {
                //bottomScreenChange(R.layout.settings_layout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_about-> {
                val aboutFragment = AboutFragment()
                setCurrentFragment(aboutFragment)
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

    private fun setCurrentFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}