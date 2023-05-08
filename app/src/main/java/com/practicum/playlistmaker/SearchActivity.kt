package com.practicum.playlistmaker

import Track
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates.notNull

class SearchActivity : AppCompatActivity() {
    // данный конструкт помогает отложить реализацию переменной
    private var searchQuery by notNull<String>()
    private lateinit var editText: EditText

    companion object {
        const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
    }


    // сохраняем поисковой запрос
    override fun onSaveInstanceState(outState: Bundle) {
//        val editText = findViewById<EditText>(R.id.edit_text_search)
        searchQuery = editText.text.toString()
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_USER_INPUT, searchQuery)
    }
    // восстанавливаем поисковой запрос после пересоздания активити
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_USER_INPUT,"")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clear_search_button)
        val editText = findViewById<EditText>(R.id.edit_text_search)
        val backButton = findViewById<ImageView>(R.id.back_button_search_activity)
//        val libraryButton = findViewById<Button>(R.id.library_button_search_activity)
//        val settingsButton = findViewById<Button>(R.id.settings_button_search_activity)

//        libraryButton.setOnClickListener {
//            val displayIntent = Intent(this, LibraryActivity::class.java)
//            startActivity(displayIntent)
//        }
//
//        settingsButton.setOnClickListener {
//            val displayIntent = Intent(this, SettingsActivity::class.java)
//            startActivity(displayIntent)
//        }
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            //стираем текст по нажатию на clearButton
            editText.setText("")
            //убираем клавиатуру
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editText.addTextChangedListener(searchTextWatcher)

        val trackAdapter = TrackAdapter(
            mutableListOf<Track>(
                Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
                Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
                Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
                Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
                Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
            )
        )

        val rvSearchTrack = findViewById<RecyclerView>(R.id.rv_search_track)
        rvSearchTrack.adapter = trackAdapter
}
    // Метод делает видимой или не видимой кнопку clearButton
    // в зависимости от того есть или нет текст в поле editText
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}
