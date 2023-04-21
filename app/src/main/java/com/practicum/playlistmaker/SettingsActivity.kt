package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_button)
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        val supportButton = findViewById<FrameLayout>(R.id.write_to_support_button)
        val agreementButton = findViewById<FrameLayout>(R.id.user_agreement_button)

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://practicum.yandex.ru/android-developer/")
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("nfilimonov1@gmail.com"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(shareIntent)
        }

        agreementButton.setOnClickListener {
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}