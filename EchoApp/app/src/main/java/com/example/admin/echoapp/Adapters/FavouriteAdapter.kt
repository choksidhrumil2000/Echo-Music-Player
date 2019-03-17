package com.example.admin.echoapp.Adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echoapp.Fragements.SongPlayingFragement
import com.example.admin.echoapp.R
import com.example.admin.echoapp.Songs

class FavouriteAdapter(_songDetails: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<FavouriteAdapter.MyviewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mcontext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mcontext = _context
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyviewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)

        return MyviewHolder(itemView)

    }

    override fun getItemCount(): Int {

        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {

        val songObject = songDetails?.get(position)
        holder.trackTitle?.setText(songObject?.songTitle)
        holder.trackArtist?.setText(songObject?.artist)
        holder.contentHolder?.setOnClickListener {


            // Toast.makeText(mcontext,"Hey" + songObject?.songTitle,Toast.LENGTH_SHORT).show()

            val songPlayingFragement = SongPlayingFragement()
            var args = Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songId", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragement.arguments = args
            (mcontext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragement, songPlayingFragement)
                .addToBackStack("SongPlayingFragmentFavourite")
                .commit()


        }
    }

    class MyviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = itemView.findViewById(R.id.trackTitle)
            trackArtist = itemView.findViewById(R.id.trackArtist)
            contentHolder = itemView.findViewById(R.id.contentrow)

        }

    }


}