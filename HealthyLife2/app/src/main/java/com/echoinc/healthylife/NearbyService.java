package com.echoinc.healthylife;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by msi_ on 10-Jan-18.
 */

public interface NearbyService {
    @GET()
    Call<NearbyPlaceResponse> getResponse(@Url String urlString);
}
