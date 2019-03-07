package com.hossamelsharkawy.req

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import hossamelsharkawy.fastrec.DataControl
import hossamelsharkawy.fastrec.FastRec
import hossamelsharkawy.fastrec.setGrid
import kotlinx.android.synthetic.main.activity_b.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.coroutines.Job

class TowReqActivity : AppCompatActivity() {


    private lateinit var recAlbums: DataControl<Album>

    private lateinit var recPhotos: DataControl<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)

        Server.init("https://jsonplaceholder.typicode.com/")

        iniRecAlbums()
        iniRecPhotos()

        val api = Server.create(Api::class.java)

        var photosJobs: Job? = null
        var albumsJobs: Job? = null


        fun loadingView() {
            recPhotos.setData(arrayListOf())

        }

        fun errorView(error: String) {

            Log.d("agentInfo", "error $error")
        }

        fun reqPhotos() {
            Log.d("agentInfo", " Job photosJobs ${photosJobs?.hashCode()}")
            recPhotos.setData(arrayListOf())

            photosJobs = api.photos().req(
                onResponse = {
                    recPhotos.setData(it)
                },
                onError = { errorView(it) }
            )

            Log.d("agentInfo", " Job photosJobs ${photosJobs.hashCode()}")
        }

        fun reqAlbums() {
            Log.d("agentInfo", " albumsJobs s ${albumsJobs?.hashCode()}")
            recAlbums.setData(arrayListOf())

            albumsJobs = api.albums().req(
                onResponse = {
                    recAlbums.setData(it)
                },
                onError = { errorView(it) }
            )
            Log.d("agentInfo", " albumsJobs s ${albumsJobs?.hashCode()}")
        }



        btn_albums.setOnClickListener { reqAlbums() }
        btn_photo.setOnClickListener { reqPhotos() }
        btn_reqAll.setOnClickListener { reqPhotos();reqAlbums() }

        btn_StopAll.setOnClickListener {
            photosJobs?.cancel(); albumsJobs?.cancel()
        }

    }

    private fun iniRecPhotos() {

        rec_photo.setGrid(3, RecyclerView.HORIZONTAL)
        recPhotos = FastRec.Builder<Photo>(this)
            .rec(rec_photo)
            .row(R.layout.item)
            .onView { itemView, model, _ ->
                itemView.txt_title.text = model.title
            }
            .build().dataControl

    }

    private fun iniRecAlbums() {
        rec_albums.setGrid(3, RecyclerView.HORIZONTAL)

        recAlbums = FastRec.Builder<Album>(this)
            .rec(rec_albums)
            .row(R.layout.item)
            .onView { itemView, model, _ ->
                itemView.txt_title.text = model.title
            }
            .build().dataControl

    }

}



