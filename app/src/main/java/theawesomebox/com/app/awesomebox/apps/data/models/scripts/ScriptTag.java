package theawesomebox.com.app.awesomebox.apps.data.models.scripts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScriptTag {

@SerializedName("value")
@Expose
private String value;
@SerializedName("_id")
@Expose
private String id;

public String getValue() {
return value;
}

public void setValue(String value) {
this.value = value;
}

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

}