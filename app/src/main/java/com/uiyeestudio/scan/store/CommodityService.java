package com.uiyeestudio.scan.store;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Michael on 2017/8/22.
 */

public interface CommodityService {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("commodity")
    Call<String> getCommodity(@Body String barcode);
}
