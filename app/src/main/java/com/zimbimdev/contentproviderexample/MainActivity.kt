package com.zimbimdev.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.SyncStateContract
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.annotations.Contract
import kotlin.contracts.contract

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1


class MainActivity : AppCompatActivity() {

//    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, "onCreate: checkSelfPermission returned $hasReadContactPermission")

//        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "onCreate permission granted")
////            readGranted = true          //TODO don`t do this
//        } else {
//            Log.d(TAG, "onCreate: requesting permission")
//            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
//        }
        if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }
        fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick starts")

//            if (readGranted == true) {
            if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()      //create a list to hold our contacts
                cursor?.use {
                    //loop through this cursor
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }
                val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
                contact_names.adapter = adapter as ListAdapter?
            } else {
                Snackbar.make(
                    view,
                    "App needs contact access permission to work. Go to app permissions to enable it!",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Grant access") {
                        Log.d(TAG, "Snackbar onClick starts")
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {
                            Log.d(TAG, "Snackbar onClick: calling requestPermissions")
                            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)

                        } else {
                            // users perma disabled permission
                            Log.d(TAG, "Snackbar onClick: launches settings")
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", this.packageName, null)
                            Log.d(TAG, "Snackbar onClick Uri is $uri")

                            intent.data = uri
                            this.startActivity(intent)
                        }
                        Log.d(TAG, "Snackbar onClick ends")

                    }.show()
            }
            Log.d(TAG, "fab onClick ends")
        }
        Log.d(TAG, "onCreate: ends")
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        Log.d(TAG, "onRequestPermissionsResult starts")
//        when (requestCode) {
//            REQUEST_CODE_READ_CONTACTS -> {
////                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted. Do the contacts related task
//                    Log.d(TAG, "onRequestPermissionsResult: granted")
//
//                } else {
//                    //permission denied. Disable functionality
//                    Log.d(TAG, "onRequestPermissionsResult: denied")
//                }
//            }
//        }
//        Log.d(TAG, "onRequestPermissionsResult: ends")
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
