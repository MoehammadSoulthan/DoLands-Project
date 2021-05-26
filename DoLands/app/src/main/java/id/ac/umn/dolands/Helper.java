package id.ac.umn.dolands;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Helper {
    @GET("radius")
    Call<ArrayList<LocationInfoModel>> getLocation(@QueryMap HashMap<String, String> radius);

    @GET("xid/{idLocation}")
    Call<NearbyDetailModel> getLocationDetail(@Path("idLocation") String idLocation, @Query("apikey") String apikey);
}
