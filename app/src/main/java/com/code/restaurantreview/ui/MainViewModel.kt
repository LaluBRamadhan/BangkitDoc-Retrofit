package com.code.restaurantreview.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.restaurantreview.data.response.CustomerReviewsItem
import com.code.restaurantreview.data.response.PostReviewResponse
import com.code.restaurantreview.data.response.Restaurant
import com.code.restaurantreview.data.response.RestaurantResponse
import com.code.restaurantreview.data.retrofit.ApiConfig
import com.code.restaurantreview.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {



    companion object{
        const val TAG = "MainViewModel"
        const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview:LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText:LiveData<Event<String>> = _snackBarText

    init {
        findRestaurant()
    }
    private fun findRestaurant() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                _isLoading.value =false

                if (response.isSuccessful){
                    val responseBody = response.body()
                    _restaurant.value = responseBody?.restaurant
                    _listReview.value = responseBody?.restaurant?.customerReviews

                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                _isLoading.value = false

                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })

    }

     fun postReview(review: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Rawr", review)
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    _listReview.value = responseBody?.customerReviews
                    _snackBarText.value = Event(responseBody?.message.toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}