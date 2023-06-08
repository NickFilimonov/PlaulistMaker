package com.practicum.playlistmaker

import Track
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class SearchHistory (sharedPreferences: SharedPreferences) {

    val searchedTrack = sharedPreferences.getString(TRACKS_LIST_KEY, null)

    lateinit var searchedTrackList: MutableList<Track>

    fun addNewTrack(track: Track): String {

        if (searchedTrack != null) searchedTrackList = createTrackListFromJson(searchedTrack).toMutableList()

        for (i in searchedTrackList.indices) {
            if (searchedTrackList[i].trackId == track.trackId)
                searchedTrackList.removeAt(i)
        }
        searchedTrackList.add(0, track)

        if (searchedTrackList.size > 10) {
            for (i in 10 until searchedTrackList.size) {
                searchedTrackList.removeAt(i)
            }
        }

        return createJsonFromTrackList(searchedTrackList.toTypedArray())
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

