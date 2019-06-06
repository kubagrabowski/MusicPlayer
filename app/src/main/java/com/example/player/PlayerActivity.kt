package com.example.player

import android.content.Intent
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_player.*
import java.io.File

class PlayerActivity : AppCompatActivity() {


    companion object {
        var myMediaPlayer:MediaPlayer? = null
        var aktind = -1
        var updateSeekBar:Thread? = null
    }

    private var progressionMusic:SeekBar? =  null
    private var position = 0
    private var piosenki:ArrayList<File> = ArrayList()
    private var songname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        progressionMusic = postepPiosenkiSeekBar

        if(updateSeekBar==null) {
            updateSeekBar = myThread()
        }

        rozpakujPaczke()

        if(myMediaPlayer!=null && aktind!=position){
            myMediaPlayer!!.stop()
            myMediaPlayer!!.release()
        }


        val uri:Uri = Uri.parse(piosenki[position].toString())

        if(aktind!=position) {

            myMediaPlayer = MediaPlayer.create(applicationContext, uri)
            myMediaPlayer!!.setOnCompletionListener {
                next()
            }
            myMediaPlayer!!.start()
            setProgressionBar()

            if(!updateSeekBar!!.isAlive){
                updateSeekBar!!.start()
            }
            aktind = position

        }
        else{
            setProgressionBar()
            if(!myMediaPlayer!!.isPlaying){
                pause.setBackgroundResource(R.drawable.icon_play)
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setProgressionBar(){
        progressionMusic!!.max = myMediaPlayer!!.duration
        progressionMusic!!.progressDrawable.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        progressionMusic!!.thumb.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
        progressionMusic!!.setOnSeekBarChangeListener(seekBarCH())
        (updateSeekBar as myThread).setprogresBar(progressionMusic)
    }

    private fun rozpakujPaczke(){
        val intent: Intent = intent

        position = intent.extras.getInt("POSITION", 0)
        piosenki = (intent.extras.getParcelableArrayList<Parcelable>("SONGS")) as (ArrayList<File>)
        songname = piosenki.get(position).name.toString()

        songnametxt.text = songname
        songnametxt.isSelected = true


    }

    fun onPauseClick(view: View){
        progressionMusic!!.max = myMediaPlayer!!.duration
        if(myMediaPlayer!!.isPlaying){
            myMediaPlayer!!.pause()
            pause.setBackgroundResource(R.drawable.icon_play)
        }
        else{
            myMediaPlayer!!.start()
            pause.setBackgroundResource(R.drawable.icon_pause)
        }
    }

    fun onNextClick(view:View){
        changeSong((position+1)%piosenki.size)
    }

    fun onPreviousClick(view:View){
        changeSong(if((position-1)<0)(piosenki.size -1)else(position-1))
    }

    fun next(){
        changeSong((position+1)%piosenki.size)
    }

    private fun changeSong(newPosition:Int){
        myMediaPlayer!!.stop()
        myMediaPlayer!!.release()
        position = newPosition
        aktind = position

        val uri:Uri = Uri.parse(piosenki[position].toString())

        myMediaPlayer = MediaPlayer.create(applicationContext, uri)
        myMediaPlayer!!.setOnCompletionListener {
            next()
        }
        songname = piosenki.get(position).name
        songnametxt.text = songname
        progressionMusic!!.max = myMediaPlayer!!.duration
        progressionMusic!!.progress = 0
        updateSeekBar = myThread()
        (updateSeekBar as myThread).setprogresBar(progressionMusic)
        updateSeekBar!!.start()
        myMediaPlayer!!.start()
    }





    class seekBarCH:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {}

        override fun onStartTrackingTouch(p0: SeekBar?) {}

        override fun onStopTrackingTouch(p0: SeekBar?) {
            PlayerActivity.myMediaPlayer!!.seekTo(p0!!.progress)
        }
    }
}
