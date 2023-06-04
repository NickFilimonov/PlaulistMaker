package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat

const val THEME_PREFERENCES = "theme_preferences" // название файла для сохранения значений
const val THEME_KEY = "key_for_theme" // ключ, с его помощью будем сохранять и забирать данные из файла

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE) // метод getSharedPreferences() создает экземпляр SharedPreferences
                                                                               // MODE_PRIVATE - доступ к сохранённым данным для их чтения и изменения будет только у приложения, которое их породило
        val backButton = findViewById<ImageView>(R.id.back_button_settings_activity)
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        val supportButton = findViewById<FrameLayout>(R.id.write_to_support_button)
        val agreementButton = findViewById<FrameLayout>(R.id.user_agreement_button)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.theme_switcher)
//        val libraryButton = findViewById<Button>(R.id.library_button_settings_activity)
//        val searchButton = findViewById<Button>(R.id.search_button_settings_activity)
        themeSwitcher.setOnCheckedChangeListener { buttonView, isChecked ->  }

        backButton.setOnClickListener {
            finish()
        }
//        libraryButton.setOnClickListener {
//            val displayIntent = Intent(this, LibraryActivity::class.java)
//            startActivity(displayIntent)
//        }
//
//        searchButton.setOnClickListener {
//            val displayIntent = Intent(this, SearchActivity::class.java)
//            startActivity(displayIntent)
//        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer_course))
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val message = getString(R.string.message_for_developers)
            val subject = getString(R.string.subject_for_developers)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_support)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(shareIntent)
        }

        agreementButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.practicum_offer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}