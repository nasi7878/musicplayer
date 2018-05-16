package android.com.madaboutmusic.songs

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * Created by nasima on 02/03/18.
 */
class Song(
        var songId:Long = 0,
        var songTitle:String = "",
        var songArtists:String = "",
        var songsImage: String = "",
        var songPath : String= ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(songId)
        parcel.writeString(songTitle)
        parcel.writeString(songArtists)
        parcel.writeString(songsImage)
        parcel.writeString(songPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }

}