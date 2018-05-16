package android.com.madaboutmusic.Home

import android.Manifest
import android.app.Activity
import android.com.madaboutmusic.*
import android.com.madaboutmusic.songs.Song
import android.com.madaboutmusic.songs.SongActivity
import android.com.madaboutmusic.songs.SongsListAdapter
import android.com.madaboutmusic.utils.OptionItemClickListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.Comparator
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.requestPermissions
import android.system.Os.accept
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activtity_song.*
import java.io.File
import java.io.FilenameFilter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() , OptionItemClickListener {

    lateinit var musicResolver: ContentResolver
    lateinit var musicUri: Uri
    lateinit var musicAlbumUri: Uri
    lateinit var musicCursor: Cursor
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var songsListAdapter: SongsListAdapter
    var songList: ArrayList<Song> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
       /* Blurry.with(this).radius(25).sampling(2).onto(blurIv as ViewGroup)*/
        BlurImage()
        checkPermissions()
        initView()

    }

    private fun BlurImage() {

        Glide.with(this).load(R.drawable.background_blur_2)
                .override(18,18)
                .into(blurIv);

    }

    private fun checkPermissions(): Boolean {
        val permissionState = this?.let {
            ActivityCompat.checkSelfPermission(it,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {

            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    loadSongs()
            }
        }
    }

    fun initView() {

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            loadSongs()
        }
    }


    var songsList: ArrayList<Song>? = null

    private fun loadSongs() {

        progressBar.visibility = View.VISIBLE

        songsList = ArrayList()

        songsList = getPlayList()

        Collections.sort(songsList, object : Comparator<Song> {
            override fun compare(a: Song?, b: Song?): Int {
                return a?.songTitle!!.compareTo(b!!.songTitle)
            }
        })


        musicResolver = contentResolver
        musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        musicAlbumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        musicCursor = musicResolver.query(musicUri, null, null, null, null)
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        if ( cursor!= null && cursor.moveToFirst()) {

            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val fullPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val thisId = cursor.getLong(idColumn)
                val thisTitle = cursor.getString(titleColumn)
                val thisArtist = cursor.getString(artistColumn)
                val thisImage =  "" //musicCursor.getString(filePathIndex)
                val thisPath = cursor.getString(fullPath)
                songList.add(Song(thisId, thisTitle, thisArtist, thisImage,thisPath))
            } while (cursor.moveToNext())
        }

        Collections.sort(songList, object : Comparator<Song> {
            override fun compare(a: Song?, b: Song?): Int {
                return a?.songTitle!!.compareTo(b!!.songTitle)
            }

        })


        Log.e("songs::1",songList.toString())
        Log.e("songs::2",songsList.toString())

        progressBar.visibility = View.GONE

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        songsRV.layoutManager = linearLayoutManager
        songsListAdapter = SongsListAdapter(songList, this)
        songsRV.adapter = songsListAdapter

    }

    private fun getPlayList(): ArrayList<Song>? {

        val MEDIA_PATH = "/sdcard/"
        var home: File = File(MEDIA_PATH)

        if (home.listFiles(FileExtensionFilter()).size > 0) {
            for(file in home.listFiles(FileExtensionFilter())){
                var song : Song = Song()
                song.songTitle = file.name
                song.songArtists = file.path
                songsList?.add(song)
            }
        }
        return songsList;
    }


    override fun onItemClick(position: Int, action: String) {

        if (action.equals("open_song", true)) {

            Log.e("open_song", songsListAdapter.getItem(position).toString())
            startActivity(Intent(this@MainActivity, SongActivity::class.java)
                    .putExtra("song_artist", songsListAdapter.getItem(position).songArtists)
                    .putExtra("song_name", songsListAdapter.getItem(position).songTitle)
                    .putExtra("song_image", songsListAdapter.getItem(position).songsImage)
                    .putExtra("song_path",songsListAdapter.getItem(position).songPath))

                /*    .putExtra("song_prev_artist", songsListAdapter.getItem(position - 1).songArtists)
                    .putExtra("song_prev_name", songsListAdapter.getItem(position - 1).songTitle)
                    .putExtra("song_prev_image", songsListAdapter.getItem(position - 1).songsImage)
                    .putExtra("song_prev_path",songsListAdapter.getItem(position - 1).songPath)

                    .putExtra("song_next_artist", songsListAdapter.getItem(position + 1).songArtists)
                    .putExtra("song_next_name", songsListAdapter.getItem(position + 1).songTitle)
                    .putExtra("song_next_image", songsListAdapter.getItem(position + 1).songsImage)
                    .putExtra("song_next_path",songsListAdapter.getItem(position + 1).songPath))*/

        }
    }


    class FileExtensionFilter : FilenameFilter {

        override fun accept(dir: File?, name: String?): Boolean {
            return (name!!.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }


}