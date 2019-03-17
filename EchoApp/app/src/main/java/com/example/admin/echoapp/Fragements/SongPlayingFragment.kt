package com.example.admin.echoapp.Fragements


import android.app.Activity
import android.content.Context
import android.hardware.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TimeUtils
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.VisualizerDbmHandler
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.admin.echoapp.Databases.EchoDatabase
import com.example.admin.echoapp.R
import com.example.admin.echoapp.Songs
import com.example.admin.echoapp.currentSongHelper
import org.w3c.dom.Text
import java.io.Console
import java.lang.Exception
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragement : Fragment(), SeekBar.OnSeekBarChangeListener {


    object Statified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null

        var _currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        var currentSongHelper: currentSongHelper? = null

        var audiovisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        var fab: ImageButton? = null

        var favouriteContent: EchoDatabase? = null

        var updateSongTime = object : Runnable {
            override fun run() {


                val getcurrent = Statified.mediaPlayer?.currentPosition
                var minutes = TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long)
                var seconds = TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) % 60
                if ((minutes <= 9 && minutes >= 0) && (seconds <= 9 && seconds >= 0)) {
                    Statified.startTimeText?.setText(String.format("0%d:0%d", minutes, seconds))
                }
                if (minutes > 9 && seconds > 9) {
                    Statified.startTimeText?.setText(String.format("%d:%d", minutes, seconds))
                }
                if (minutes > 9 && (seconds <= 9 && seconds >= 0)) {
                    Statified.startTimeText?.setText(String.format("%d:0%d", minutes, seconds))
                }
                if ((minutes <= 9 && minutes >= 0) && seconds > 9) {
                    Statified.startTimeText?.setText(String.format("0%d:%d", minutes, seconds))
                }
//                TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long ) -
//                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong()) as Long)))
                Handler().postDelayed(this, 1000)
            }


        }

        var mSensorManager: SensorManager? = null
        var mSensorListner: SensorEventListener? = null

        var MY_PREFS_NAME = "ShakeFeature"

    }

    var mAccelaration: Float = 0f
    var mAcccelarationcurrent: Float = 0f
    var mAcccelarationlast: Float = 0f

    object Staticated {

        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playnext("PlaynextLikenormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified._currentPosition)
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.currentPosition = Statified._currentPosition
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long

                    Staticated.updateTextViews(
                        Statified.currentSongHelper?.songTitle as String,
                        Statified.currentSongHelper?.songArtist as String
                    )


                    Statified.mediaPlayer?.reset()
                    try {

                        Statified.mediaPlayer?.setDataSource(
                            Statified.myActivity,
                            Uri.parse(Statified.currentSongHelper?.songPath)
                        )
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    Staticated.playnext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_off
                    )
                )

            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleupdated = songTitle
            var songArtistupdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleupdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistupdated = "unknown"
            }
            Statified.songTitleView?.setText(songTitleupdated)
            Statified.songArtistView?.setText(songArtistupdated)
        }


        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekBar?.max = finalTime
            var startminute = TimeUnit.MILLISECONDS.toMinutes(startTime.toLong() as Long)
            var startsecond = TimeUnit.MILLISECONDS.toSeconds(startTime.toLong() as Long) % 60
            var startdiff = startsecond - TimeUnit.MILLISECONDS.toSeconds(startminute)
            var finalminute = TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong() as Long)
            var finalsecond = TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong() as Long) % 60
            var finaldiff = finalsecond - TimeUnit.MILLISECONDS.toSeconds(finalminute)
            // var ss = startsecond%60

            Log.i("startsec", startdiff.toString())
            Log.i("endsec", finaldiff.toString())
            Statified.startTimeText?.setText(String.format("0%d:0%d", startminute, startsecond))



            if ((finalsecond <= 9 && finalsecond >= 0) && (finalminute <= 9 && finalminute >= 0)) {
                Statified.startTimeText?.setText(String.format("0%d:0%d", startminute, startsecond))
                Statified.endTimeText?.setText(String.format("0%d:0%d", finalminute, finalsecond))
            }
            if ((finalminute <= 9 && finalminute >= 1) && finalsecond > 9) {
                Statified.startTimeText?.setText(String.format("0%d:%d", startminute, startsecond))
                Statified.endTimeText?.setText(String.format("0%d:%d", finalminute, finalsecond))
            }
            if (finalminute > 9 && (finalsecond <= 9 && finalsecond >= 0)) {
                Statified.startTimeText?.setText(String.format("%d:0%d", startminute, startsecond))
                Statified.endTimeText?.setText(String.format("%d:0%d", finalminute, finalsecond))
            }
            if (finalminute > 9 && finalsecond > 9) {
                Statified.startTimeText?.setText(String.format("%d:%d", startminute, startsecond))
                Statified.endTimeText?.setText(String.format("%d:%d", finalminute, finalsecond))
            }


            Statified.seekBar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }


        fun playnext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified._currentPosition += 1
            } else if (check.equals("PlaynextLikenormalShuffle", true)) {

                var randomobject = Random()
                var randomPosition = randomobject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified._currentPosition = randomPosition

            }
            if (Statified._currentPosition == Statified.fetchSongs?.size) {
                Statified._currentPosition = 0
            }
            Statified.currentSongHelper?.isLoop = false
            var nextSong = Statified.fetchSongs?.get(Statified._currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.currentPosition = Statified._currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            Staticated.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )


            Statified.mediaPlayer?.reset()
            try {

                Statified.mediaPlayer?.setDataSource(
                    Statified.myActivity,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_off
                    )
                )

            }
        }

        fun playPrevious() {
            Statified._currentPosition -= 1
            if (Statified._currentPosition == -1) {
                Statified._currentPosition = 0
            }
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isLoop = false
            var nextSong = Statified.fetchSongs?.get(Statified._currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.currentPosition = Statified._currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            Staticated.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )


            Statified.mediaPlayer?.reset()
            try {

                Statified.mediaPlayer?.setDataSource(
                    Statified.myActivity,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_off
                    )
                )

            }

        }


    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        if (Statified.mediaPlayer != null) {
            Statified.mediaPlayer?.seekTo(progress)
        }


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_song_playing_fragement, container, false)

        setHasOptionsMenu(true)
        activity?.title = "Now Playing"
        Statified.seekBar = view.findViewById(R.id.seekbar)
        Statified.startTimeText = view.findViewById(R.id.startTime)
        Statified.endTimeText = view.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view.findViewById(R.id.nextButton)
        Statified.previousImageButton = view.findViewById(R.id.previousButton)
        Statified.loopImageButton = view.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view.findViewById(R.id.songArtist)
        Statified.songTitleView = view.findViewById(R.id.songTitle)
        Statified.glView = view.findViewById(R.id.visualizer_view)
        Statified.fab = view.findViewById(R.id.favourite_icon)
        Statified.fab?.alpha = 0.8f


        return view
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val item: MenuItem = menu!!.findItem(R.id.action_redirect)
        item.isVisible = true

    }

//    fun onBackpressed(
//        super.onBa
//    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                // MainScreenFragement.Statified.nowPlayingBottombar?.visibility = View.VISIBLE
                /// MainScreenFragement.Statified.songTitle?.setText(Statified.currentSongHelper?.songTitle)
                return false

            }


        }
        return false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audiovisualization?.onResume()
        Statified.mSensorManager?.registerListener(
            Statified.mSensorListner,
            Statified.mSensorManager?.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        Statified.audiovisualization?.onPause()
        super.onPause()

        Statified.mSensorManager?.unregisterListener(Statified.mSensorListner)
    }

    override fun onDestroyView() {
        Statified.audiovisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.seekBar?.setOnSeekBarChangeListener(this)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcccelarationcurrent = 0.0f
        mAcccelarationcurrent = SensorManager.GRAVITY_EARTH
        mAcccelarationlast = SensorManager.GRAVITY_EARTH
        bindShakeListner()
        Statified.seekBar?.setOnSeekBarChangeListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audiovisualization = Statified.glView as AudioVisualization
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favouriteContent = EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper = currentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isShuffle = false
        Statified.currentSongHelper?.isLoop = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var _songId: Long = 0
        Statified.seekBar?.setOnSeekBarChangeListener(this)


        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            _songId = arguments?.getInt("songId")!!.toLong()
            Statified._currentPosition = arguments?.getInt("songPosition") as Int
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")


            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = _songId
            Statified.currentSongHelper?.currentPosition = Statified._currentPosition

            Staticated.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


        var fromFavBottombar = arguments?.get("FavBottombar") as? String
        if (fromFavBottombar == null) {
            //Statified.mediaPlayer = FavouriteFragement.Statified.mediaPlayer

            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                if (path == null) {
                    Log.i("error Info", "path is null")
                }

                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
//            Statified.mediaPlayer?.setOnPreparedListener(
//                MediaPlayer.OnPreparedListener {
//                    Statified.mediaPlayer?.start()
//                }
                //    Statified.mediaPlayer?.prepareAsync()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()
        } else {

        }
        // if(Statified.mediaPlayer != null){
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        //}
//        onPrepared(Statified.mediaPlayer)

        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }

        Statified.mediaPlayer?.setOnCompletionListener {

            Staticated.onSongComplete()


        }
        clickHandler()

        var VisualizerDbmHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audiovisualization?.linkTo(VisualizerDbmHandler)

        var prefsForShuffle =
            Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }

        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

        } else {
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }

        if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))

        }


    }
//    override fun onPrepared(mp: MediaPlayer?) {
//        mp?.start()
//
//    }


    fun clickHandler() {

        Statified.fab?.setOnClickListener({
            if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_off
                    )
                )
                Statified.favouriteContent?.deletefavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity, "Removed from favourite", Toast.LENGTH_SHORT).show()
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
                Statified.favouriteContent?.storeAsFabourite(
                    Statified.currentSongHelper?.songId?.toInt() as Int, Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath
                )
                Toast.makeText(Statified.myActivity, "Added to favourites", Toast.LENGTH_SHORT).show()
            }


        })
        Statified.playPauseImageButton?.setOnClickListener {

            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }

        }
        Statified.shuffleImageButton?.setOnClickListener {
            var editershuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editerloop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.currentSongHelper?.isShuffle = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editershuffle?.putBoolean("feature", false)
                editershuffle?.apply()
            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editershuffle?.putBoolean("feature", true)
                editershuffle?.apply()
                editerloop?.putBoolean("feature", false)
                editerloop?.apply()
            }

        }
        Statified.previousImageButton?.setOnClickListener {

            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            Staticated.playPrevious()

        }
        Statified.nextImageButton?.setOnClickListener {


            Statified.currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playnext("PlaynextLikenormalShuffle")
            } else {
                Staticated.playnext("PlayNextNormal")
            }

        }
        Statified.loopImageButton?.setOnClickListener {
            var editershuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editerloop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editerloop?.putBoolean("feature", false)
                editerloop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editershuffle?.putBoolean("feature", false)
                editershuffle?.apply()
                editerloop?.putBoolean("feature", true)
                editerloop?.apply()
            }

        }

    }


    fun bindShakeListner() {

        Statified.mSensorListner = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                mAcccelarationlast = mAcccelarationcurrent
                mAcccelarationcurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = mAcccelarationcurrent - mAcccelarationlast
                mAccelaration = mAccelaration * 0.9f + delta

                if (mAccelaration > 12) {
                    val prefs =
                        Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playnext("PlayNextNormal")

                    } else {
                        Log.i("error", "else part in bindshake")
                    }
                }
            }


        }
    }


}
