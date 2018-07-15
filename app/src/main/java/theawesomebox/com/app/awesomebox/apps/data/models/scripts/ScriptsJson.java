package theawesomebox.com.app.awesomebox.apps.data.models.scripts;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScriptsJson {

@SerializedName("type")
@Expose
private boolean type;
@SerializedName("result")
@Expose
private List<Result> result = null;

public boolean isType() {
return type;
}

public void setType(boolean type) {
this.type = type;
}

public List<Result> getResult() {
return result;
}

public void setResult(List<Result> result) {
this.result = result;
}

}