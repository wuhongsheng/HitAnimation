package com.hd.hitanimation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var tv:TextView = findViewById(R.id.tv)
        var tvPlay:TextView  = findViewById(R.id.tv_playAnimation)
        tvPlay.setOnClickListener(View.OnClickListener {
            Log.i("MainActivity", "onCreate: ")
            HitAnimationHelper.getInstance(this).startAnimation(tv)
        })
    }
}