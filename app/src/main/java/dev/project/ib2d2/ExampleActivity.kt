/* ExampleActivity:
   - Provides structure for back buttons and tree of activities
   - Will be useful in team management, file management, etc
 */

package dev.project.ib2d2

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_layout)

        val mTopToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mTopToolbar)
        setTitle("test")

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        mTopToolbar.setNavigationOnClickListener{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }
}