package com.zitech.audio.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {

    @GET("?do=product_all")
    public Call<List<ProductFeatures>> getProductFeatures(@Query("product") String product);

    @GET("?do=product_all")
    public Call<List<ProductFeatures>> getAllProductsFeatures();

    @GET("software-edition")
    public Call<Version> getSoftwareVersion();

    @GET("firmware-edition")
    public Call<Version> getFirmwareVersion();

    @GET("?do=system_info")
    public Call<Status> sendSystemInfo( @Query("btAdd") String blueAddr,
                                                       @Query("btModel") String btModel,
                                                       @Query("phModel") String phModel,
                                                       @Query("opSys") String opSys );

}
