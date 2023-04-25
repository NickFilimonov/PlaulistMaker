package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import kotlin.properties.Delegates.notNull

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
    }
    private var userInput by notNull<String>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_USER_INPUT, userInput)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (savedInstanceState != null) {
            userInput = savedInstanceState.getString(SEARCH_USER_INPUT, "")
        }

        val editText = findViewById<EditText>(R.id.edit_text_search)
        val clearButton = findViewById<ImageView>(R.id.clear_search_button)


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
