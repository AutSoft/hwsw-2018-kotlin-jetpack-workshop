package hu.autsoft.hwswmobile18.jobs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContent, JobListFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

}
