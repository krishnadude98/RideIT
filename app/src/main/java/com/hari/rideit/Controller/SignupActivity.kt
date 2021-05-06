package com.hari.rideit.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.hari.rideit.R

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)
        val roll= AnimationUtils.loadAnimation(this,R.anim.bounce)
        val logos= findViewById(R.id.logo_signup) as ImageView
        logos.startAnimation(roll)
    }
}