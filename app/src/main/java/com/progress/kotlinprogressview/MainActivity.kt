package com.progress.kotlinprogressview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val r = Random(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressCircle.applyGradient(Color.GREEN, Color.YELLOW, Color.RED)
        progressButton.setOnClickListener {
            progressCircle.progress = r.nextFloat()
            progressLine.progress = r.nextFloat()
        }
    }
}
