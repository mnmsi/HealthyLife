package com.echoinc.healthylife;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by msi_ on 11-Jan-18.
 */

public interface DirectionServiceInterface {
    @GET()
    Call<DirectionResponse> getResponse(@Url String dirUrlString);
}
