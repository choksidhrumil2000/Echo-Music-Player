package com.example.admin.echoapp.Fragements


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echoapp.Adapters.FavouriteAdapter
import com.example.admin.echoapp.Databases.EchoDatabase
import com.example.admin.echoapp.R
import com.example.admin.echoapp.R.id.playPauseButton
import com.example.admin.echoapp.Songs
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavouriteFragement : Fragment() {

    var myActivity: Activity? = null
    var favouriteContent: EchoDatabase? = null

    var noFavourites: TextView? = null
    var playPauseButton: ImageButton? = null
    var recyclerView: RecyclerView? = null

    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null


    var songTitle: TextView? = null
    var nowPlayingBottombar: RelativeLayout? = null

    object Statified {
        var trackPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null


    }


    fun bottomBarSetup() {

        bottomBarClickHandler()

        try {
            if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {

                nowPlayingBottombar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottombar?.visibility = View.INVISIBLE
            }
            songTitle?.setText(SongPlayingFragement.Statified.currentSongHelper?.songTitle)
            SongPlayingFragement.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragement.Statified.currentSongHelper?.songTitle)
                SongPlayingFragement.Staticated.onSongComplete()


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun bottomBarClickHandler() {

        nowPlayingBottombar?.setOnClickListener {

            //Statified.mediaPlayer = SongPlayingFragement.Statified.mediaPlayer

            val songPlayingFragement = SongPlayingFragement()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragement.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragement.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragement.Statified.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragement.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragement.Statified.currentSongHelper?.currentPosition as Int)
            args.putParcelableArrayList("songData", SongPlayingFragement.Statified.fetchSongs)
            args.putString("FavBottombar", "Success")
            songPlayingFragement.arguments = args
            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragement, songPlayingFragement)
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()

        }
        playPauseButton?.setOnClickListener {

            if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragement.Statified.mediaPlayer?.pause()
                Statified.trackPosition = SongPlayingFragement.Statified.mediaPlayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragement.Statified.mediaPlayer?.seekTo(Statified.trackPosition)
                SongPlayingFragement.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite_fragement, container, false)

        activity?.title = "Favourites"
        noFavourites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottombar = view?.findViewById(R.id.hiddenbarfavouriteScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        songTitle = view?.findViewById(R.id.songTitlefavouriteScreen)
        recyclerView = view?.findViewById(R.id.favouriteRecycler)
        // Inflate the layout for this fragment
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favouriteContent = EchoDatabase(myActivity)

        displayFavouritesBySearching()
        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            var songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentID = songCursor.getLong(songID)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentID, currentTitle, currentArtist, currentData, currentDate))
            }
        }

        return arrayList

    }


    fun displayFavouritesBySearching() {

        if (favouriteContent?.checkSize() as Int > 0) {

            refreshList = ArrayList<Songs>()
            getListfromDatabase = favouriteContent?.querydbList()
            var fetchListfromDevice = getSongsFromPhone()
            if (fetchListfromDevice != null) {
                for (i in 0..fetchListfromDevice.size - 1) {
                    for (j in 0..getListfromDatabase?.size as Int - 1) {
                        if ((getListfromDatabase?.get(j)?.songID) == (fetchListfromDevice.get(i).songID)) {

                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }
            } else {

            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE

            } else {
                var favouriteAdapter = FavouriteAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favouriteAdapter
                recyclerView?.setHasFixedSize(true)
            }

        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }
    }


}
