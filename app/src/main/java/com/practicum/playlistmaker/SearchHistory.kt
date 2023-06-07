package com.practicum.playlistmaker

import Track
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

object SearchHistory {

     fun addNewTrack(track: Track, searchedTrackList: MutableList<Track>): MutableList<Track> {

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

        return searchedTrackList
    }

}