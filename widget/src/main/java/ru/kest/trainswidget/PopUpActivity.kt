package ru.kest.trainswidget

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

/**
 * Activity for processing train selection
 *
 * Created by Konstantin on 27.05.2017.
 */
class PopUpActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(0))

        Log.i(LOG_TAG, "PopUpActivity.onCreate()")
        // Should do a proper argument verification here
        val extras = intent.extras
        if (extras != null && extras.containsKey(DETAILS)) {
            displayDialog(extras)
        } else {
            Log.w(LOG_TAG, "PopUpActivity: detail for dialog not found, finishing")
            finish()
        }
    }

    private fun displayDialog(extras: Bundle) {
        val details = extras.getString(DETAILS)
        AlertDialog.Builder(this)
                .setTitle("Расписание электричек")
                .setMessage("Следить за рейсом $details?")
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    //                Intent onClickIntent = new Intent(context, TrainsWidget.class);
                    val notificationIntent = Intent(this@PopUpActivity, TrainsWidget::class.java).apply {
                        action = CREATE_NOTIFICATION
                        putExtra(RECORD_HASH, extras.getInt(RECORD_HASH))
                    }
                    sendBroadcast(notificationIntent)

                    // Handle a positive answer
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    // Handle a negative answer
                    dialog.dismiss()
                    finish()
                }
                .setIcon(R.mipmap.ic_launcher)
                .setOnDismissListener { finish() }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_pop_up, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


}
