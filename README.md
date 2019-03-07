
# Req
This is Deferred Extension to make an API request on Android UI with an exception message, 
 build on kotlin coroutines and Retrofit.

<a href="https://bintray.com/hossamelshrkawy/Req/Req/0.0.1/link"><img src="https://api.bintray.com/packages/hossamelshrkawy/Req/Req/images/download.svg?version=0.0.1"/></a>

# Installation
**Add the dependencies to your gradle file:**

```javascript
 dependencies { 
   implementation 'com.hossamelsharkawy:req:0.0.1'
 }
```

# Quick start

   **Init Server  and add BaseUrl in your Application class**
  
 ```kotlin
 Server.init("https://jsonplaceholder.typicode.com/")
  ```
  
   **Create API interface**

 ```kotlin
 interface Api {
   @GET("photos")
   fun photos(): Deferred<ArrayList<Photo>>
 }
  ```



   **Create API  instance**

```kotlin
 val typicodeApi = Server.create(Api::class.java)
  ```


   **Request api on success response**

 ```kotlin
  typicodeApi.photos().req { photos -> //handel resonse  } 
  ```


   **Request api on success  or failure response**
 ```kotlin
  typicodeApi.photos().req(  
   { res -> //handel resonse }  
  ,{ errorMsg -> //handel error })
  ```


   **Request api on success  , failure or empty data**
 ```kotlin
  typicodeApi.photos().req(  
    onResponse ={res ->    //handel resonse }
  , onError = {errorMsg -> //handel error } 
  , onEmpty = { emptyMsg-> //handel empty })
    
  ```

   **Cancel request**
   
```kotlin
 val photosReq = typicodeApi.photos().req(  
  onResponse ={res ->   //handel resonse }
 ,onError ={errorMsg -> //handel error } 
)

photosReq.cancel()
  ```
