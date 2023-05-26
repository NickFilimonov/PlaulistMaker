package com.practicum.playlistmaker

import Track
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatTime(trackTime: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }
}