package ru.wutiarn.edustor.android

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.flipboard.bottomsheet.BottomSheetLayout
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    val docService = DaggerAppComponent.create().documentsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            IntentIntegrator(this).initiateScan(IntentIntegrator.QR_CODE_TYPES)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents?.let {
            Snackbar.make(findViewById(R.id.container), "Found ${it}", Snackbar.LENGTH_LONG).show()

            val bottomSheetLayout = findViewById(R.id.bottomsheet) as BottomSheetLayout
            val documentSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.sheet_document, bottomSheetLayout, false)
            val uuidView = documentSheetView.findViewById(R.id.uuid) as TextView
            uuidView.text = it
            bottomSheetLayout.showWithSheetView(documentSheetView)

            docService.documentUUIDInfo(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
