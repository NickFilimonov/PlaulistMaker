package com.practicum.playlistmaker

import Track
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates.notNull

class SearchActivity : AppCompatActivity(), TrackAdapter.TrackClickListener {

    private lateinit var clearButton: ImageView
    private lateinit var editText: EditText
    private lateinit var backButton: ImageView
    private lateinit var rvSearchTrack: RecyclerView
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var communicationProblemPlaceholder: LinearLayout
    private lateinit var communicationProblemButton: Button
    private lateinit var rvSearchHistory: RecyclerView
    private lateinit var searchHistoryLayout: NestedScrollView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var clearSearchHistoryButton: Button
    private lateinit var searchHistory: SearchHistory

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)

    private val tracks = ArrayList<Track>()

    private val trackAdapter = TrackAdapter(this)

    private val tracksInHistory = ArrayList<Track>()

    private val searchHistoryAdapter = SearchHistoryAdapter()

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
        rvSearchHistory = findViewById(R.id.rv_search_history)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        clearSearchHistoryButton = findViewById(R.id.search_history_button)

        sharedPreferences = getSharedPreferences(TRACKS_PREFERENCES, MODE_PRIVATE)

        searchHistory = SearchHistory(sharedPreferences)

        tracksInHistory.addAll(searchHistory.searchedTrackList)


        if (tracksInHistory.isEmpty()) {
            searchHistoryLayout.visibility = View.GONE
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == TRACKS_LIST_KEY) {
                val tracks = sharedPreferences?.getString(TRACKS_LIST_KEY, null)
                if (tracks != null) {
                    tracksInHistory.clear()
                    tracksInHistory.addAll(createTrackListFromJson(tracks))
                    searchHistoryAdapter.notifyDataSetChanged()
                }
            }
        }

        clearSearchHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            tracksInHistory.clear()
            searchHistoryLayout.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        editText.setOnFocusChangeListener { v, hasFocus ->
            searchHistoryLayout.visibility = if (hasFocus && editText.text.isEmpty() && tracksInHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

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

        trackAdapter.tracks = tracks

        searchHistoryAdapter.searchHistory = tracksInHistory

        rvSearchTrack.adapter = trackAdapter

        rvSearchHistory.adapter = searchHistoryAdapter


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
                searchHistoryLayout.visibility = if (editText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editText.addTextChangedListener(searchTextWatcher)
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)  //убираем клавиатуру
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
                            trackAdapter.notifyDataSetChanged() // уведомляем адаптер об изменении набора данных, перерисовавается весь набор, это не оптимально
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

    override fun onTrackClick(track: Track) {
        searchHistory.addNewTrack(track)
    }

    // метод дессириализует массив объектов Fact (в Shared Preference они хранятся в виде json строки)
    private fun createTrackListFromJson(json: String?): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
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

    // данный конструкт помогает отложить реализацию переменной
    // переменная searchQuery хранит пользовательский ввод в edittext
    private var searchQuery by notNull<String>()

    companion object {
        const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}
const val TRACKS_PREFERENCES = "tracks_preferences"
const val TRACKS_LIST_KEY = "key_for_tracks_list"

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
