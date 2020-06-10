package com.hari.rideit.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hari.rideit.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        accountSettingsBtn.setOnClickListener {
            val accountSettingsIntent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(accountSettingsIntent)
        }

        languageBtn.setOnClickListener {
            val languageIntent = Intent(this, LanguageActivity::class.java)
            startActivity(languageIntent)
        }

        aboutUsBtn.setOnClickListener {
            val aboutUsIntent = Intent(this, AboutUs::class.java)
            startActivity(aboutUsIntent)
        }

        termsAndCondtionsBtn.setOnClickListener {
            val termsAndCondtionsIntent = Intent(this, TermsAndCondtionsActivity::class.java)
            startActivity(termsAndCondtionsIntent)
        }

        privacyPolicyBtn.setOnClickListener {
            val privacyPolicyIntent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(privacyPolicyIntent)
        }

    }
}