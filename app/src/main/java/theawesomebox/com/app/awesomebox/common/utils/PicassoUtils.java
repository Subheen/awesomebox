package theawesomebox.com.app.awesomebox.common.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.concurrent.Executors;

import theawesomebox.com.app.awesomebox.BuildConfig;

import static com.squareup.picasso.NetworkPolicy.NO_CACHE;
import static com.squareup.picasso.NetworkPolicy.NO_STORE;

/**
 * Created by Haris on 03-Dec-15.
 */
public class PicassoUtils {

    public static Picasso getPicassoInstance(Context context) {
        Picasso picasso = new Picasso.Builder(context).
                executor(Executors.newSingleThreadExecutor()).
                listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Logger.error("PICASSO", "URL: " + uri + " EXCEPTION :" + exception.toString());
                    }
                }).build();
        picasso.setLoggingEnabled(BuildConfig.IS_DEBUG_ABLE);
        return picasso;
    }

    public static boolean isAllowedPicassoRequest(Context context, ImageView imageView, String url) {
        return context == null || imageView == null || !AppUtils.ifNotNullEmpty(url);
    }

    public static RequestCreator getPicassoRequestCreator(Context context, String url, int fallbackResourceId) {
        RequestCreator picassoRequestCreator = Picasso.with(context).load(url);
        if (fallbackResourceId > 0) {
            picassoRequestCreator = picassoRequestCreator.error(fallbackResourceId).placeholder(fallbackResourceId);
        }
        return picassoRequestCreator.memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE);
        // return picassoRequestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);
    }

    public static void picassoReloadImageFromNetwork(Context context, final String url) {
        if (context == null || !AppUtils.ifNotNullEmpty(url)) return;
        Picasso.with(context).load(url).networkPolicy(NO_CACHE);
    }

    public static void picassoLoadImageFromUrl(Context context, final ImageView imageView, final String url,
                                               final int errorResId) {
        if (isAllowedPicassoRequest(context, imageView, url)) return;
        getPicassoRequestCreator(context, url, errorResId).into(imageView);
    }

    public static void picassoLoadUrlUsingListTag(Context context, final ImageView imageView,
                                                  final String url, String picassoTag,
                                                  final int errorResId, int size, boolean centerCrop) {
        if (isAllowedPicassoRequest(context, imageView, url)) return;
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        picassoRequestCreator = picassoRequestCreator.tag(picassoTag);
        if (size > 0)
            picassoRequestCreator = picassoRequestCreator.resize(size, size);
        if (centerCrop) picassoRequestCreator = picassoRequestCreator.centerCrop();
        picassoRequestCreator.memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE).into(imageView);
    }

    public static void picassoLoadImageFromUrl(Context context, final ImageView imageView, final String url,
                                               final int errorResId, final Callback callback, int size) {
        if (isAllowedPicassoRequest(context, imageView, url)) return;
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        if (size > 0)
            picassoRequestCreator = picassoRequestCreator.resize(size, size);
        if (callback == null)
            picassoRequestCreator.into(imageView);
        else
            picassoRequestCreator.into(imageView, callback);
    }

    public static void picassoLoadInvalidateImageFromUrl(Context context, final ImageView imageView,
                                                         final String url, final int errorResId, int size) {
        if ((context == null || imageView == null) || !AppUtils.ifNotNullEmpty(url)) return;
        Picasso.with(context).invalidate(url);
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        if (size > 0)
            picassoRequestCreator = picassoRequestCreator.resize(size, size);
        picassoRequestCreator.networkPolicy(NO_CACHE, NO_STORE).into(imageView);
    }

    public static void picassoLoadImageFromUrl(Context context, final ImageView imageView, final String url,
                                               final int errorResId, boolean isCompressed) {
        if (isAllowedPicassoRequest(context, imageView, url)) return;
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        if (isCompressed)
            picassoRequestCreator = picassoRequestCreator.fit();
        else
            picassoRequestCreator.into(imageView);
    }

    public static void picassoLoadImageFromUrl(Context context, final ImageView imageView, final String url,
                                               final int errorResId, int size) {
        if (isAllowedPicassoRequest(context, imageView, url)) return;
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        if (size > 0)
            picassoRequestCreator = picassoRequestCreator.resize(size, size);
        picassoRequestCreator.into(imageView);
    }

    public static void picassoLoadImageFromUrl(Context context, final ImageView imageView, final String url,
                                               final int errorResId, int size, boolean centerCrop) {
        if ((context == null || imageView == null) || !AppUtils.ifNotNullEmpty(url) || size <= 0)
            return;
        RequestCreator picassoRequestCreator = getPicassoRequestCreator(context, url, errorResId);
        if (centerCrop)
            picassoRequestCreator = picassoRequestCreator.centerCrop();
        if (size > 0)
            picassoRequestCreator = picassoRequestCreator.resize(size, size);
        picassoRequestCreator.into(imageView);
    }
}
