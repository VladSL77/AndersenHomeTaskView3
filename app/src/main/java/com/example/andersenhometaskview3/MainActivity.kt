package com.example.andersenhometaskview3

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrlImage: EditText
    private lateinit var imageView: ImageView
    private var urlImage = ""
    private var bitmap: Bitmap? = null
    private lateinit var task: DownloadImageTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextUrlImage = findViewById(R.id.editTextImage)
        imageView = findViewById(R.id.imageView)
        task = DownloadImageTask()
        editTextUrlImage.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                urlImage = editTextUrlImage.text.toString()
                try {
                    bitmap = task.execute(urlImage).get()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.error_load),
                        Toast.LENGTH_LONG
                    ).show()
                }
                hideKeyboard()
                return@OnKeyListener true
            }
            false
        })
    }

    private class DownloadImageTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg p0: String?): Bitmap? {
            var urlConnection: HttpURLConnection? = null
            var url: URL
            var bitmap: Bitmap? = null
            try {
                url = URL(p0[0].toString())
                urlConnection = url.openConnection() as HttpURLConnection?
                val inputStream = urlConnection?.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                urlConnection?.disconnect()
            }
            return bitmap
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}