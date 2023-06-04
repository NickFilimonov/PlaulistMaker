package com.practicum.playlistmaker

import Track
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates.notNull

const val TRACKS_PREFERENCES = "tracks_preferences"
const val NEW_TRACK_KEY = "key_for_new_track"

class SearchActivity : AppCompatActivity() {
    // данный конструкт помогает отложить реализацию переменной
    // переменная searchQuery хранит пользовательский ввод в edittext
    private var searchQuery by notNull<String>()

    companion object {
        const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }

    // метод сохраняет поисковой запрос
    override fun onSaveInstanceState(outState: Bundle) {
        searchQuery = editText.text.toString()
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_USER_INPUT, searchQuery)
    }

    // метод восстанавливает поисковой запрос после пересоздания активити
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_USER_INPUT,"")
    }

    // Метод делает видимой / невидимой кнопку clearButton
    // в зависимости от того есть или нет текст в поле editText
    // метод возвращает количество последовательности символов
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun sendRequestToServer() {
        rvSearchTrack.visibility = View.VISIBLE
        nothingFoundPlaceholder.visibility = View.GONE
        communicationProblemPlaceholder.visibility = View.GONE
        if (editText.text.isNotEmpty()) { // проверяем, чтобы edittext не был пустым
            iTunesService.findTrack(editText.text.toString()).enqueue(object : Callback<TrackResponce> { // вызываем метод findTrack() у iTunesService, и передаем туда текст из edittext
                override fun onResponse(call: Call<TrackResponce>,                                      // метод enqueue() передает наш запрос на сервер (метод retrofit)
                                        response: Response<TrackResponce>) { // метод onResponse() вызывается, когда сервер дал ответе(response) на запрос
                    if (response.code() == 200) { // code() вызывает код http-статуса, 200 - успех, запрос корректен, сервер вернул ответ
                        tracks.clear() // clear() отчищвет recyclerview от предъидущего списка, без этой строчки отображение нового списка не происходит
                        if (response.body()?.results?.isNotEmpty() == true) { // если ответ(response)  в виде объекта, который указали в Call (body() возвращает) не пустой
                            tracks.addAll(response.body()?.results!!) // добавляем все найденные треки в спсиок addAll() для отображения на экране
                            adapter.notifyDataSetChanged() // уведомляем адаптер об изменении набора данных, перерисовавается весь набор, это не оптимально
                        } else {
                            tracks.clear()
                            rvSearchTrack.visibility = View.GONE
                            nothingFoundPlaceholder.visibility = View.VISIBLE// показываем плейсхолднер nothing_found
                        }
                    } else {
                        tracks.clear()
                        rvSearchTrack.visibility = View.GONE
                        communicationProblemPlaceholder.visibility = View.VISIBLE// показываем плейсхолднер communication_problem
                    }
                }

                override fun onFailure(call: Call<TrackResponce>, t: Throwable) { // метод вызывается если не получилось установить соединение с сервером
                    tracks.clear()
                    rvSearchTrack.visibility = View.GONE
                    communicationProblemPlaceholder.visibility = View.VISIBLE // показываем плейсхолднер communication_problem
                }

            })
        }
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)  //убираем клавиатуру
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)

    private lateinit var clearButton: ImageView
    private lateinit var editText: EditText
    private lateinit var backButton: ImageView
    private lateinit var rvSearchTrack: RecyclerView
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var communicationProblemPlaceholder: LinearLayout
    private lateinit var communicationProblemButton: Button

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        clearButton = findViewById(R.id.clear_search_button)
        editText = findViewById(R.id.edit_text_search)
        backButton = findViewById(R.id.back_button_search_activity)
        rvSearchTrack = findViewById(R.id.rv_search_track)
        nothingFoundPlaceholder = findViewById(R.id.nothing_found_placeholder)
        communicationProblemPlaceholder = findViewById(R.id.communication_problem_placeholder)
        communicationProblemButton = findViewById(R.id.update_button)

        val sharedPreferences = getSharedPreferences(TRACKS_PREFERENCES, MODE_PRIVATE)
        // метод(sharedPreferences, track: Track)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            editText.setText("") //стираем текст по нажатию на clearButton
            closeKeyboard()
            tracks.clear()
            rvSearchTrack.visibility = View.GONE
            nothingFoundPlaceholder.visibility = View.GONE
            communicationProblemPlaceholder.visibility = View.GONE
        }

        adapter.tracks = tracks

        rvSearchTrack.adapter = adapter

        // обработчик нажатия для кнопки выпадающей клавиатуры
        // IME_ACTION_SEARCH - кнопка поиск (добавляется на клавиатуру
        // добавлением в тег edittext (в xml разметке) атрибута imeOptions="actionSearch"
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                sendRequestToServer()
                true
            }
            false
        }

        communicationProblemButton.setOnClickListener {
            sendRequestToServer()
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
}

//        логика для макета дизайна, с кнопками для перехода между экранами внизу активити
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
