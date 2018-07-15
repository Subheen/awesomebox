package theawesomebox.com.app.awesomebox.apps.data.models.scripts;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("_id")
@Expose
private String id;
@SerializedName("script_name")
@Expose
private String scriptName;
@SerializedName("script_description")
@Expose
private String scriptDescription;
@SerializedName("script_createdBy")
@Expose
private String scriptCreatedBy;
@SerializedName("script_createdBy_uname")
@Expose
private String scriptCreatedByUname;
@SerializedName("__v")
@Expose
private int v;
@SerializedName("file_path")
@Expose
private String filePath;
@SerializedName("file_name")
@Expose
private String fileName;
@SerializedName("file_orignal_name")
@Expose
private String fileOrignalName;
@SerializedName("script_tags")
@Expose
private List<ScriptTag> scriptTags = null;
@SerializedName("script_filter")
@Expose
private List<ScriptFilter> scriptFilter = null;
@SerializedName("file_size")
@Expose
private int fileSize;
@SerializedName("scriptid")
@Expose
private int scriptid;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getScriptName() {
return scriptName;
}

public void setScriptName(String scriptName) {
this.scriptName = scriptName;
}

public String getScriptDescription() {
return scriptDescription;
}

public void setScriptDescription(String scriptDescription) {
this.scriptDescription = scriptDescription;
}

public String getScriptCreatedBy() {
return scriptCreatedBy;
}

public void setScriptCreatedBy(String scriptCreatedBy) {
this.scriptCreatedBy = scriptCreatedBy;
}

public String getScriptCreatedByUname() {
return scriptCreatedByUname;
}

public void setScriptCreatedByUname(String scriptCreatedByUname) {
this.scriptCreatedByUname = scriptCreatedByUname;
}

public int getV() {
return v;
}

public void setV(int v) {
this.v = v;
}

public String getFilePath() {
return filePath;
}

public void setFilePath(String filePath) {
this.filePath = filePath;
}

public String getFileName() {
return fileName;
}

public void setFileName(String fileName) {
this.fileName = fileName;
}

public String getFileOrignalName() {
return fileOrignalName;
}

public void setFileOrignalName(String fileOrignalName) {
this.fileOrignalName = fileOrignalName;
}

public List<ScriptTag> getScriptTags() {
return scriptTags;
}

public void setScriptTags(List<ScriptTag> scriptTags) {
this.scriptTags = scriptTags;
}

public List<ScriptFilter> getScriptFilter() {
return scriptFilter;
}

public void setScriptFilter(List<ScriptFilter> scriptFilter) {
this.scriptFilter = scriptFilter;
}

public int getFileSize() {
return fileSize;
}

public void setFileSize(int fileSize) {
this.fileSize = fileSize;
}

public int getScriptid() {
return scriptid;
}

public void setScriptid(int scriptid) {
this.scriptid = scriptid;
}

}