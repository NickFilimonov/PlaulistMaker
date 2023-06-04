package com.practicum.playlistmaker

import Track
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    interface TrackClickListener {
        fun onTrackClick(track: Track) {
            Toast.makeText(this, "Нажали на трек ${track.trackName}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)


    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{clickListener.onTrackClick(tracks[position])}
    }

    override fun getItemCount(): Int =  tracks.size

}