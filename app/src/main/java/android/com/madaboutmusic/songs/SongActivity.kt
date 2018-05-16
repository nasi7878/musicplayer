package android.com.madaboutmusic.songs

import android.com.madaboutmusic.R
import android.com.madaboutmusic.services.MediaPlayerService
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activtity_song.*
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection
import android.content.Intent
import com.bumptech.glide.Glide


/**
 *
 * Created by nasima on 05/03/18.s
 */
class SongActivity : AppCompatActivity() {


    private var player: MediaPlayerService? = null
    var serviceBound = false

    var song: Song = Song()

    var isPlaying: Boolean? = false

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaPlayerService.LocalBinder
            player = binder.service
            serviceBound = true

          //  Toast.makeText(this@SongActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }


    override  fun onBackPressed() {
        super.onBackPressed()
    }

    private fun playAudio(media: String) {
        //Check is service is active
        if (!serviceBound) {
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            playerIntent.putExtra("media", media)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {

            val playerIntent = Intent(this, MediaPlayerService::class.java)
            playerIntent.putExtra("media", media)
            startService(playerIntent)
            //Service is active
            //Send media with BroadcastReceiver
        }
    }


    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState!!.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            player!!.stopSelf()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activtity_song)
       /* val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        Blur.setBackgroundColor(color)*/
//        Blurry.with(this).radius(25).sampling(2).onto(Blur)
        readIntent()
        songPlayIc.setOnClickListener {
            if(!isPlaying!!) {
                isPlaying = true;
                songPlayIc.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_white_24dp))
                playAudio(song.songPath)
            }else{
                isPlaying = false;
                songPlayIc.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_white_24dp))
                stopAudio(song.songPath)
            }

        }
    }

    private fun stopAudio(songPath: String) {

        if (!serviceBound) {
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            playerIntent.putExtra("media", songPath)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {

            val playerIntent = Intent(this, MediaPlayerService::class.java)
            playerIntent.putExtra("media", songPath)
            startService(playerIntent)
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    fun readIntent() {

        if(intent.hasExtra("song_name")) {
            songTitleTv.text = intent.getStringExtra("song_name")
            song.songTitle = intent.getStringExtra("song_name")
        }
         if(intent.hasExtra("song_artist")) {
             songArtistTv.text = intent.getStringExtra("song_artist")
             song.songArtists = intent.getStringExtra("song_artist")

         }

         if(intent.hasExtra("song_path")) {
             song.songPath = intent.getStringExtra("song_path")

         }
         if(intent.hasExtra("song_image")){
             val mmr = MediaMetadataRetriever()
             mmr.setDataSource(intent.getStringExtra("song_path"))
             song.songsImage = intent.getStringExtra("song_path")
             try {
                 if (mmr != null) {
                     val art = mmr.embeddedPicture
                     var bmp: Bitmap? = BitmapFactory.decodeByteArray(art, 0, art.size)
                     if (bmp != null) {
                         bmp = ThumbnailUtils.extractThumbnail(bmp,2000,2000)
                         songIv.setImageBitmap(bmp)

                         Glide.with(this).load(bmp)
                                 .override(18,18)
                                 .into(blurSongIv);

                     }
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }

    }

}