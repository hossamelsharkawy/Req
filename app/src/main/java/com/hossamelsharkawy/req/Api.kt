package com.hossamelsharkawy.req

import com.google.gson.JsonElement
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Hossam Elsharkawy
0201099197556
on 06/03/19.  time :14:46

 */
interface Api {
    @GET("todos/{id}")
    fun agentInfoAsync(
        @Path("id") agentId: String = "4"
    ): Deferred<JsonElement>

    @GET("photos")
    fun photos(): Deferred<ArrayList<Photo>>

    @GET("albums")
    fun albums(): Deferred<ArrayList<Album>>



}
