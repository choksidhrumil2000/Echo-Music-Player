package com.example.admin.echoapp.Fragements


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.RecoverySystem
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echoapp.Adapters.MainScreenAdapter
import com.example.admin.echoapp.R
import com.example.admin.echoapp.Songs
import kotlinx.android.synthetic.main.fragment_main_screen_fragement.*
import java.lang.Exception
import java.util.*
import java.util.stream.Collectors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainScreenFragement : Fragment() {

    object Statified {

        var nowPlayingBottombar: RelativeLayout? = null
        var playPausebutton: ImageButton? = null
        var songTitle: TextView? = null
        var visibleLayout: RelativeLayout? = null
        var noSongs: RelativeLayout? = null
    }

    var getSongsList: ArrayList<Songs>? = null


    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_main_screen_fragement, container, false)
        setHasOptionsMenu(true)
        activity?.title = "All Songs"
        Statified.nowPlayingBottombar = view.findViewById(R.id.hiddenbarmainScreen)
        Statified.playPausebutton = view.findViewById(R.id.playPauseButtonmain)
        Statified.songTitle = view.findViewById(R.id.songTitlemainScreen)
        Statified.visibleLayout = view.findViewById(R.id.visiblelayout)
        Statified.noSongs = view.findViewById(R.id.noSongs)
        recyclerView = view.findViewById(R.id.contentMain)
//        nowPlayingBottombar?.visibility = View.VISIBLE
        //  playPauseButton?.setBackgroundResource(R.drawable.play_icon)


        return view
    }

    fun bottomBarSetup_main() {

        bottomBarClickHandler_main()

        try {
            if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {

                Statified.nowPlayingBottombar?.visibility = View.VISIBLE
            } else {
                Statified.nowPlayingBottombar?.visibility = View.INVISIBLE
            }

            Statified.songTitle?.setText(SongPlayingFragement.Statified.currentSongHelper?.songTitle)
            SongPlayingFragement.Statified.mediaPlayer?.setOnCompletionListener {
                Statified.songTitle?.setText(SongPlayingFragement.Statified.currentSongHelper?.songTitle)
                SongPlayingFragement.Staticated.onSongComplete()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    fun clickhandler(){
//
//        Statified.nowPlayingBottombar?.setOnClickListener({
//
//
//            var songPlayingFragement = SongPlayingFragement()
//            (this as FragmentActivity).supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.details_fragement,songPlayingFragement)
//                .addToBackStack("SongPlayingFragment")
//                .commit()
//
//
//
//        })
//        Statified.playPausebutton?.setOnClickListener({
//
//            if(SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean)
//            {
//                SongPlayingFragement.Statified.mediaPlayer?.pause()
//                SongPlayingFragement.Statified.currentSongHelper?.isPlaying = false
//                SongPlayingFragement.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
//
//            }else
//            {
//                SongPlayingFragement.Statified.mediaPlayer?.start()
//                SongPlayingFragement.Statified.currentSongHelper?.isPlaying = true
//                SongPlayingFragement.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
//            }
//
//        })
//
//    }

    fun bottomBarClickHandler_main() {

        Statified.nowPlayingBottombar?.setOnClickListener {

            // mediaPlayer = SongPlayingFragement.Statified.mediaPlayer

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
                ?.addToBackStack("SongPlayinFragment")
                ?.commit()

        }
        Statified.playPausebutton?.setOnClickListener {

            if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragement.Statified.mediaPlayer?.pause()
                FavouriteFragement.Statified.trackPosition =
                        SongPlayingFragement.Statified.mediaPlayer?.getCurrentPosition() as Int
                Statified.playPausebutton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragement.Statified.mediaPlayer?.seekTo(FavouriteFragement.Statified.trackPosition)
                SongPlayingFragement.Statified.mediaPlayer?.start()
                Statified.playPausebutton?.setBackgroundResource(R.drawable.pause_icon)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val item2 = menu?.findItem(R.id.action_sort)
        item2?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "false")
            editor?.putString("action_sort_recent", "true")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromPhone()

        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")

        if (getSongsList == null) {
            Statified.visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            var mLayoutmanager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutmanager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }

        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup_main()
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


}
