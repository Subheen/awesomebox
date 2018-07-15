package theawesomebox.com.app.awesomebox.apps.business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bilal on 06/02/2018.
 */

public class MyJSONObject extends JSONObject {

    public MyJSONObject(String json) throws JSONException {
        super(json);
    }

    @Override
    public String getString(String name) throws JSONException {
        if (has(name) && !get(name).equals(null))
            return super.getString(name);
        else
            return "";
    }

    @Override
    public JSONObject getJSONObject(String name) throws JSONException {
        if (has(name) && !get(name).equals(null))
            return super.getJSONObject(name);
        else
            return new JSONObject();
    }

    @Override
    public JSONArray getJSONArray(String name) throws JSONException {
        if (has(name) && !get(name).equals(null))
            return super.getJSONArray(name);
        else
            return new JSONArray();
    }
}
