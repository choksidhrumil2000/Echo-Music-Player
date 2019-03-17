package com.example.admin.echoapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.admin.echoapp.Songs

class EchoDatabase : SQLiteOpenHelper {


    var _songList = ArrayList<Songs>()

    object Staticated {
        val DB_NAME = "FavouriteDatabase"
        val TABLE_NAME = "FavouriteTable"
        val COLUMN_ID = "SongId"
        val COLUMN_SONG_TITLE = "songTitle"
        val COLUMN_SONG_ARTIST = "songArtist"
        val COLUMN_SONG_PATH = "songPath"
        val DB_VERSION = 1


    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {

        sqLiteDatabase?.execSQL(
            "CREATE TABLE " + Staticated.TABLE_NAME + "(" + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," +
                    Staticated.COLUMN_SONG_TITLE + " STRING," + Staticated.COLUMN_SONG_PATH + " STRING);"
        )


    }

    fun storeAsFabourite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(Staticated.COLUMN_ID, id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentValues)
        db.close()

    }

    fun querydbList(): ArrayList<Songs>? {

        try {

            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {

                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songpath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songpath, 0))
                } while (cSor.moveToNext())
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return _songList

    }

    fun checkifIdExists(_id: Int): Boolean {
        var storeId = -1090
        try {

            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongId = '$_id'"
            val cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                } while (cSor.moveToNext())
            } else {
                Log.i("id info", "cursor is not moving to first")
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        return storeId != -1090
    }

    fun deletefavourite(_id: Int) {

        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(
        context,
        Staticated.DB_NAME,
        null,
        Staticated.DB_VERSION
    )

    fun checkSize(): Int {

        var counter = 0
        try {

            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            val cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    counter += 1
                } while (cSor.moveToNext())
            } else {
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return counter
    }
}