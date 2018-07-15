package theawesomebox.com.app.awesomebox.apps.data.models.backup;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("nextTime")
@Expose
private String nextTime;
@SerializedName("sizeBytes")
@Expose
private SizeBytes sizeBytes;
@SerializedName("email")
@Expose
private String email;
@SerializedName("size")
@Expose
private Size size;
@SerializedName("monthday")
@Expose
private int monthday;
@SerializedName("weekday")
@Expose
private String weekday;
@SerializedName("minutes")
@Expose
private int minutes;
@SerializedName("hours")
@Expose
private int hours;
@SerializedName("noOfCopies")
@Expose
private int noOfCopies;
@SerializedName("frequency")
@Expose
private int frequency;
@SerializedName("path")
@Expose
private List<Path> path = null;
@SerializedName("time")
@Expose
private String time;
@SerializedName("folderCount")
@Expose
private int folderCount;
@SerializedName("fileCount")
@Expose
private int fileCount;
@SerializedName("last_backup")
@Expose
private String lastBackup;
@SerializedName("is_set")
@Expose
private boolean isSet;

public String getNextTime() {
return nextTime;
}

public void setNextTime(String nextTime) {
this.nextTime = nextTime;
}

public SizeBytes getSizeBytes() {
return sizeBytes;
}

public void setSizeBytes(SizeBytes sizeBytes) {
this.sizeBytes = sizeBytes;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public Size getSize() {
return size;
}

public void setSize(Size size) {
this.size = size;
}

public int getMonthday() {
return monthday;
}

public void setMonthday(int monthday) {
this.monthday = monthday;
}

public String getWeekday() {
return weekday;
}

public void setWeekday(String weekday) {
this.weekday = weekday;
}

public int getMinutes() {
return minutes;
}

public void setMinutes(int minutes) {
this.minutes = minutes;
}

public int getHours() {
return hours;
}

public void setHours(int hours) {
this.hours = hours;
}

public int getNoOfCopies() {
return noOfCopies;
}

public void setNoOfCopies(int noOfCopies) {
this.noOfCopies = noOfCopies;
}

public int getFrequency() {
return frequency;
}

public void setFrequency(int frequency) {
this.frequency = frequency;
}

public List<Path> getPath() {
return path;
}

public void setPath(List<Path> path) {
this.path = path;
}

public String getTime() {
return time;
}

public void setTime(String time) {
this.time = time;
}

public int getFolderCount() {
return folderCount;
}

public void setFolderCount(int folderCount) {
this.folderCount = folderCount;
}

public int getFileCount() {
return fileCount;
}

public void setFileCount(int fileCount) {
this.fileCount = fileCount;
}

public String getLastBackup() {
return lastBackup;
}

public void setLastBackup(String lastBackup) {
this.lastBackup = lastBackup;
}

public boolean isIsSet() {
return isSet;
}

public void setIsSet(boolean isSet) {
this.isSet = isSet;
}

}