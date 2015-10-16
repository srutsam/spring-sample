package googlePOC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

@Controller
@RequestMapping("/lookup/{city}")
public class PlaceLookupController {
    private static final String API_KEY = "AIzaSyA9ZErfMC-rwIRsRjiwrOF0b_J7QR6LqEY";
    private static final String ENTERPRISE_API_KEY = "AIzaSyDBMgy7k1P8oZcs5L3quapwkFkfnZ1Cbtc";
    private static final String GEOCODE_BASE = "https://maps.googleapis.com/maps/api/geocode";
    private static final String OUTPUT_TYPE = "/json";

    public static String[] doGoogleSearch(String city) {
        String[] latLng = new String[2];
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(GEOCODE_BASE);
            sb.append(OUTPUT_TYPE);
            sb.append("?address=" + city);
            sb.append("&key=" + API_KEY);

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

            return latLng;
        } catch (IOException e) {

            return latLng;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
            System.out.println(">> " + jsonObj.getString("geometry").toString());


        } catch (JSONException e) {
            return latLng;
        }

        return latLng;

    }

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public PlacesModel getLocation(@PathVariable String city) {
        doGoogleSearch(city);
        return new PlacesModel("Ha");

    }
}
