package theawesomebox.com.app.awesomebox.apps.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import theawesomebox.com.app.awesomebox.apps.business.BaseItem;


public class HttpResponseItem {

    /**
     * http response code
     */
    private int responseCode;
    /**
     * Raw Http response of given request
     */
    private String response = null;

    /**
     * List of {@link BaseItem}
     * Consider you need a list of item or a single item against your http request you need
     * to parse your {@link JSONObject} or {@link JSONArray} into {@link BaseItem} or {@link List<BaseItem>}
     * and use in your code.
     * <p>
     * To add a single item in list use {@link #setResponseItem(BaseItem)} or want to
     * add complete list use {@link #setItems(List)}
     */
    private List<BaseItem> items = null;
    /**
     * URL of given request
     */
    private String httpRequestUrl;

    /**
     * Response for given request
     */
    private String httpRequestType = NetworkUtils.HTTP_GET;

    public HttpResponseItem() {
    }

    public HttpResponseItem(int responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getHttpRequestUrl() {
        return httpRequestUrl;
    }

    public void setHttpRequestUrl(String httpRequestUrl) {
        this.httpRequestUrl = httpRequestUrl;
    }

    public List<BaseItem> getItems() {
        return items;
    }

    public void setItems(List<BaseItem> items) {
        this.items = items;
    }

    /**
     * @param item {@link BaseItem}
     */
    public void setResponseItem(BaseItem item) {
        if (items == null)
            items = new ArrayList<>();
        items.add(item);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getHttpRequestType() {
        return httpRequestType;
    }

    public void setHttpRequestType(String httpRequestType) {
        this.httpRequestType = httpRequestType;
    }

    public String getDefaultResponse() {
        return "Rest API " + httpRequestUrl + " response is " + response + "(" + responseCode + ")";
    }

    @Override
    public String toString() {
        return "HttpResponseItem{" +
                "responseCode=" + responseCode +
                ", response='" + response + '\'' +
                ", items=" + items +
                ", httpRequestUrl=" + httpRequestUrl +
                '}';
    }
}
