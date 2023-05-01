package com.practicum.playlistmaker

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
import kotlin.properties.Delegates.notNull

class SearchActivity : AppCompatActivity() {
    // данный конструкт помогает отложить реализацию переменной
    private var searchQuery by notNull<String>() //

    companion object {
        const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
    }
    // сохраняем поисковой запрос
    override fun onSaveInstanceState(outState: Bundle) {
        val editText = findViewById<EditText>(R.id.edit_text_search)
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
        val libraryButton = findViewById<Button>(R.id.library_button_search_activity)
        val settingsButton = findViewById<Button>(R.id.settings_button_search_activity)

        libraryButton.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
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
}
    // Метод делает видимой или не видимой кнопку clearButton
    // в зависимости от того есть или нет текст в поле editText
    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}
