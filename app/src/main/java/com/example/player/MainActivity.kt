package com.example.player

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import java.io.File


class MainActivity : AppCompatActivity() {

    private var myListViewSongs:ListView? = null
    private var nazwypiosenek:Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myListViewSongs = myMusicList

        getPermission()
    }

    fun getPermission() {

        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    musicToList()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun getMusicFiles(file:File, pobierz:Int):ArrayList<File>{
        val lista:ArrayList<File> = ArrayList()
        val pliki:Array<File> = file.listFiles()

        for (plik:File in pliki){
            if(plik.isDirectory && !plik.isHidden){
                if(plik.name .contains("Muza") || plik.name .contains("Music") || plik.name .contains("muza") || plik.name .contains("music")){
                    lista.addAll(getMusicFiles(plik,1))
                }
                else {
                    lista.addAll(getMusicFiles(plik,pobierz))
                }
            }
            else{
                if(plik.name.endsWith(".mp3") || plik.name.endsWith(".wav")){
                    if(pobierz == 1){
                        lista.add(plik)
                    }
                }
            }
        }
        return lista
    }

    fun musicToList(){
        val piosenki:ArrayList<File> = getMusicFiles(Environment.getExternalStorageDirectory(), 0)

        nazwypiosenek = Array(piosenki.size){ indeks -> piosenki[indeks].name.toString().replace(".mp3", "").replace(".wav", "")}

        val myAdap: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, nazwypiosenek)

        myListViewSongs?.adapter = myAdap


        myListViewSongs?.onItemClickListener = AdapterView.OnItemClickListener{
                _, _, indeks, _ ->  val intent = Intent(applicationContext, PlayerActivity::class.java)
                                    intent.putExtra("SONGS", piosenki)
                                    intent.putExtra("POSITION", indeks)

                                    startActivity(intent)
                                    }

    }
}
