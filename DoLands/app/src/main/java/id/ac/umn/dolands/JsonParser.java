package id.ac.umn.dolands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object) {
        // Initialize Hash Map
        HashMap<String, String> dataList = new HashMap<>();

        try {
            // Get Name From Object
            String name = object.getString("name");
            // Get Latitude From Object
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            // Get Longitude From Object
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            // Put All Value in Hash Map
            dataList.put("name", name);
            dataList.put("lat", name);
            dataList.put("lng", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return Hash Map
        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        // Initialize Hash Map List
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {

            try {
                // Initialize Hash Map
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                // Add Data in Hash Map List
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Return Hash Map List
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object) {
        // Initialize Json Array
        JSONArray jsonArray = null;

        try {
            // Get Result Array
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return Array
        return parseJsonArray(jsonArray);
    }
}
