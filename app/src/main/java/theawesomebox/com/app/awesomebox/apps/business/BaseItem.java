package theawesomebox.com.app.awesomebox.apps.business;

public interface BaseItem {

    int ITEM_TYPE_FOOTER = 1;
    int ITEM_TYPE_ROW_DETAILS = 2;
    int ITEM_TYPE_ROW_DETAILS_CLICKABLE = 3;
    int ITEM_TYPE_DOCTOR = 4;

    int getItemType();
}
