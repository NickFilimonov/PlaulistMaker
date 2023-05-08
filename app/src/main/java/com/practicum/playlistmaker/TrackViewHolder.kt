package com.practicum.playlistmaker

import Track
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val albumsCover: ImageView = itemView.findViewById(R.id.albums_cover)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val bandName: TextView = itemView.findViewById(R.id.band_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        trackName.text = item.trackName
        bandName.text = item.artistName
        trackTime.text = item.trackTime
        Glide.with(itemView)
        .load(item.artworkUrl100)
        .placeholder(R.drawable.placeholder)
        .centerCrop()
        .transform(RoundedCorners(2))
        .into(albumsCover)

    }
}