package googlePOC;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.maps.model.LatLng;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Map;
import java.io.FileWriter;



@Controller
@RequestMapping("/get-data/{keyword}")
public class PlacesController {
    private HashMap<String, LatLng> PlacesMap = new HashMap<String, LatLng>();

    private static final String API_KEY = "AIzaSyDBMgy7k1P8oZcs5L3quapwkFkfnZ1Cbtc";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch";
    private static final String PLACES_DETAILS_BASE = "https://maps.googleapis.com/maps/api/place/details";
    private static final String OUTPUT_TYPE = "/json";
    private static final int RADIUS = 5000;

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=41.8781136,-87.6297982&radius=50000
    // &types=point_of_interest|museum|cafe|aquarium|art_gallery|zoo|poi
    // &key=AIzaSyDBMgy7k1P8oZcs5L3quapwkFkfnZ1Cbtc

    public void fillMap() {
        PlacesMap.put("Chicago", new LatLng(41.8781136,-87.6297982));
        PlacesMap.put("Venice", new LatLng(45.440847,12.315515));
        PlacesMap.put("Sydney", new LatLng(-33.867487,151.206990));
    }

    public ArrayList<Place> search(double lat, double lng, String keyword) {
        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(OUTPUT_TYPE);
            sb.append("?sensor=false");
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + RADIUS);
            sb.append("&key=" + API_KEY);
            //sb.append("&types=" + SEARCH_TYPE);
            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));

            URL url = new URL(sb.toString());
            System.out.println("URL: " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {

            return resultList;
        } catch (IOException e) {

            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            // System.out.println(jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.setPlaceId(predsJsonArray.getJSONObject(i).getString("place_id"));
                resultList.add(place);
            }
        } catch (JSONException e) {
        }

        return resultList;
    }

    public static JSONObject details(String placeId, String city) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_DETAILS_BASE);
            sb.append(OUTPUT_TYPE);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&placeid=" + URLEncoder.encode(placeId, "utf8"));

            URL url = new URL(sb.toString());
            System.out.println("Details: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Place place = null;
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
            return jsonObj;

            /* place = new Place();
            place.setIconUrl(jsonObj.getString("icon"));
            place.setName(jsonObj.getString("name"));
            place.setPlaceId(jsonObj.getString("placeid"));
            place.setAddress(jsonObj.getString("formatted_address"));
            if (jsonObj.has("formatted_phone_number")) {
                place.setPhoneNumber(jsonObj.getString("formatted_phone_number"));
            } */
            //writeToFile(placeId, jsonObj, city);

        } catch (JSONException e) {
            return null;
        }
    }

    public static void writeToFile (String fileName, JSONObject jsonObj, String city) {
        try {
            System.out.println("Trying to write to " + fileName);
            FileWriter file = new FileWriter("/Users/ssamraj/Desktop/GooglePlacesData/" + city + "/" + fileName + ".json");

            try {
                file.write(jsonObj.toString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + jsonObj);

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                file.flush();
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    public PlacesModel places(@PathVariable String keyword) {
        fillMap();
        ArrayList<JSONObject> results = new ArrayList<JSONObject>();

        for (Map.Entry<String, LatLng> entry : PlacesMap.entrySet()) {
            ArrayList<Place> placeResults = search(entry.getValue().lat, entry.getValue().lng, keyword);

            if (placeResults.isEmpty())
                return null;
            else {
                for (Place placeResult : placeResults) {
                    results.add(details(placeResult.getPlaceId(), entry.getValue().toString()));
                }
            }
        }
        if (results.isEmpty()) {
            return new PlacesModel("No data available");
        } else {
            return new PlacesModel(results.toString());
        }
    }

}
