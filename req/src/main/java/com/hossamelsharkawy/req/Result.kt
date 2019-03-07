package com.hossamelsharkawy.req


/**
 * Created by Hossam Elsharkawy
0201099197556
on 9/26/2018.  time :16:29

 */
class Result<T>(val onRes: ((T) -> Unit)? = null
                , val onError: ((msg :String?) -> Unit)? = null
                , val onProgress: ((Int) -> Unit)? = null
                , val onEmpty: ((String?) -> Unit)? = null)

