package dev.project.ib2d2

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CreateBackup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_layout)

        val mTopToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle("Create New Backup")

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }
}