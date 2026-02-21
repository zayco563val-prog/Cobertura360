package com.cobertura360.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = MaterialTextView(this)
        textView.text = "Cobertura360\n\nAplicación iniciada correctamente"
        textView.textSize = 20f

        setContentView(textView)
    }
}
