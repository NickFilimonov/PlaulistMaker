package com.practicum.playlistmaker

import Track
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class SearchHistory (val sharedPreferences: SharedPreferences) {


    val searchedTrackList = mutableListOf<Track>()

    init {
        val searchedTrack = sharedPreferences.getString(TRACKS_LIST_KEY, "") ?: ""
        if (searchedTrack.isNotEmpty()) {
            searchedTrackList.addAll(createTrackListFromJson(searchedTrack))
        }
    }

    fun addNewTrack(track: Track) {

        if (searchedTrackList.contains(track)) searchedTrackList.remove(track)

        searchedTrackList.add(0, track)

        if (searchedTrackList.size == 11) searchedTrackList.removeAt(10)

        sharedPreferences.edit()
            .putString(TRACKS_LIST_KEY, createJsonFromTrackList(searchedTrackList.toTypedArray())) // отдает данные adapter, а createJsonFromFactsList() их серриализует
            .apply()
    }

    fun clearHistory() {
        searchedTrackList.clear()
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    // метод дессириализует массив объектов Fact (в Shared Preference они хранятся в виде json строки)
    private fun createTrackListFromJson(json: String?): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    // метод серриализует массив объектов Fact (переводит в формат json)
    private fun createJsonFromTrackList(facts: Array<Track>): String {
        return Gson().toJson(facts)
    }

}

