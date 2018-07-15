package theawesomebox.com.app.awesomebox.apps.data.models.backup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Size {

@SerializedName("remaining")
@Expose
private int remaining;
@SerializedName("allocated")
@Expose
private int allocated;
@SerializedName("used")
@Expose
private int used;

public int getRemaining() {
return remaining;
}

public void setRemaining(int remaining) {
this.remaining = remaining;
}

public int getAllocated() {
return allocated;
}

public void setAllocated(int allocated) {
this.allocated = allocated;
}

public int getUsed() {
return used;
}

public void setUsed(int used) {
this.used = used;
}

}