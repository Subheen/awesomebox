package theawesomebox.com.app.awesomebox.apps.data.models.backup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BackupJson {

@SerializedName("type")
@Expose
private boolean type;
@SerializedName("data")
@Expose
private Data data;

public boolean isType() {
return type;
}

public void setType(boolean type) {
this.type = type;
}

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

}