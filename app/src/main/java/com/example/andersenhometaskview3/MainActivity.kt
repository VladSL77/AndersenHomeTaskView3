package com.example.andersenhometaskview3

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrlImageStandard: EditText
    private lateinit var editTextUrlImagePicasso: EditText
    private lateinit var imageView: ImageView
    private var urlImage = ""
    private var bitmap: Bitmap? = null
    private lateinit var task: DownloadImageTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextUrlImageStandard = findViewById(R.id.editTextImageStandard)
        editTextUrlImagePicasso = findViewById(R.id.editTextImagePicasso)
        imageView = findViewById(R.id.imageView)
        editTextUrlImageStandard.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                task = DownloadImageTask()
                urlImage = editTextUrlImageStandard.text.toString()
                try {
                    Log.i("imageBug", urlImage)
                    bitmap = task.execute(urlImage).get()
                    imageView.setImageBitmap(bitmap!!)
                    editTextUrlImageStandard.text.clear()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.error_load),
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
                hideKeyboard()
                return@OnKeyListener true
            }
            false
        })
        editTextUrlImagePicasso.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                task = DownloadImageTask()
                urlImage = editTextUrlImagePicasso.text.toString()
                Picasso.with(this)
                    .load(urlImage)
                    .into(imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {}
                        override fun onError() {
                            Toast.makeText(
                                baseContext,
                                getString(R.string.error_load),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                editTextUrlImagePicasso.text.clear()
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