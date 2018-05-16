package android.com.madaboutmusic.songs

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.com.madaboutmusic.utils.OptionItemClickListener
import android.com.madaboutmusic.R
import android.media.ThumbnailUtils
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever



/**
 *
 * Created by nasima on 02/03/18.
 */

class SongsListAdapter(private var mSongsList: ArrayList<Song>, private var mOptionItemClickListener: OptionItemClickListener) : RecyclerView.Adapter<SongsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = View.inflate(parent?.context, R.layout.item_song, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSongsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder?.songTitle?.text = mSongsList[position].songTitle
        holder?.songArtist?.text = mSongsList[position].songArtists

        val mmr = MediaMetadataRetriever()

        mmr.setDataSource(mSongsList[position].songPath)
        try {
            if (mmr != null) {
                val art = mmr.embeddedPicture
                var bmp: Bitmap? = BitmapFactory.decodeByteArray(art, 0, art.size)
                if (bmp != null) {
                    bmp = ThumbnailUtils.extractThumbnail(bmp, 100, 100)
                    holder?.songsImageView?.setImageBitmap(bmp)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getItem(position: Int): Song {
        return mSongsList[position]
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var songTitle: TextView = itemView.findViewById(R.id.songTitleTv)
        var songArtist : TextView = itemView.findViewById(R.id.songArtistTv)
        var songsImageView:ImageView = itemView.findViewById(R.id.songIv)
        var songView : View = itemView.findViewById(R.id.songView)

        init {
            songView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {

            val position = adapterPosition
            if( position != RecyclerView.NO_POSITION ) {
                when( view!!.id ) {
                    R.id.songView -> {
                        mOptionItemClickListener.onItemClick(position, "open_song")
                    }
                }
            }


        }

    }
}