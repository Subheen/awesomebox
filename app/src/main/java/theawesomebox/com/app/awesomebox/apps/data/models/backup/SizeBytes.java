package theawesomebox.com.app.awesomebox.apps.data.models.backup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SizeBytes {

@SerializedName("used")
@Expose
private int used;
@SerializedName("allocated")
@Expose
private int allocated;

public int getUsed() {
return used;
}

public void setUsed(int used) {
this.used = used;
}

public int getAllocated() {
return allocated;
}

public void setAllocated(int allocated) {
this.allocated = allocated;
}

}