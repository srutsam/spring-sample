package googlePOC;

import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesModel {
    private String name;
    private String description;
    private String message;
    private JSONObject finalResult;
    private ArrayList<JSONObject> results;

    public PlacesModel() {

    }

    public PlacesModel(JSONObject finalResult) {
        this.finalResult = finalResult;
    }

    public PlacesModel(String message) {
        this.message = message;
    }

    public PlacesModel(ArrayList<JSONObject> results) {
        this.results = results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
