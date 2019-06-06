package com.example.player

import android.util.Log
import android.widget.SeekBar
import java.lang.Exception

class myThread:Thread() {

    private var progresBar: SeekBar? = null
    private var totalDuration = 0

    fun setprogresBar(prog: SeekBar?){
        progresBar = prog
        setTotalDuration(progresBar!!.max)
    }

    private fun setTotalDuration(duration:Int){

        totalDuration=duration
        Log.d("DUR",totalDuration.toString())
    }

    override fun run() {
        var currentPosition = 0
        Log.d("DDT", totalDuration.toString())
        Log.d("DD", currentPosition.toString())
        while(currentPosition < totalDuration){
            try {
                Log.d("DD", currentPosition.toString())
                sleep(500)
                currentPosition = PlayerActivity.myMediaPlayer!!.currentPosition
                Log.d("DD", currentPosition.toString())
                if(progresBar!=null) {
                    progresBar!!.progress = currentPosition
                }
            }
            catch(e: Exception){ Log.d("THREAD_EXC", e.printStackTrace().toString())}
        }
    }
}