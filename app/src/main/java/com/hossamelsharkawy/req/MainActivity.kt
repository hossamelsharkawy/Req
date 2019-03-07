package com.hossamelsharkawy.req

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import hossamelsharkawy.fastrec.FastRec
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Server.init("https://jsonplaceholder.typicode.com/")

        val rec = FastRec.Builder<Photo>(this)
            .rec(rec)
            .row(R.layout.item)
            .onView { itemView, model, _ ->
                itemView.txt_title.text = model.title
            }
            .build().dataControl


        val ff = Server.create(Api::class.java)

        var xx: Job? = null


        fun loadingView() {
            rec.setData(arrayListOf())
            txt_result.text = "request"
            txt_result.setBackgroundColor(Color.GRAY)
        }

        fun errorView(error: String) {
            txt_result.text = error
            txt_result.setBackgroundColor(Color.RED)
            Log.d("agentInfo", "error $error")
        }

        fun req() {
            Log.d("agentInfo", " Job s ${xx?.hashCode()}")

            loadingView()

            xx = ff.photos().req(
                onResponse = {
                    txt_result.text = "done"
                    txt_result.setBackgroundColor(Color.CYAN)
                    rec.setData(it)
                },
                onError = { errorView(it) }
            )

            Log.d("agentInfo", " Job e ${xx.hashCode()}")

        }


        btn_stop.setOnClickListener {
            txt_result.text = "stop"
            xx?.cancel()
        }

        btn_photo.setOnClickListener {
            req()
            // req()
        }

        btn_exist.setOnClickListener {
            finish()
        }
    }

}


