package id.ac.umn.dolands;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Helper {
    @GET("radius")
    Call<ArrayList<LocationInfoModel>> getPost(@QueryMap HashMap<String, String> radius);
}
