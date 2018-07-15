package theawesomebox.com.app.awesomebox.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;

public class BitmapUtils {

    public static Bitmap decodeFile(String filePath, int size, boolean square) {
        return decodeFile(new File(filePath), size, square);
    }

    private static Bitmap decodeFile(File file, int size, boolean square) {
        try {
            // decode image size
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            // Bitmap bitmapSize = BitmapFactory.decodeStream(new
            // FileInputStream(
            // file), null, bitmapOptions);
            // Find the correct scale value. It should be the power of 2.
            if (size > 0) {
                int width_tmp = bitmapOptions.outWidth, height_tmp = bitmapOptions.outHeight;
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < size || height_tmp / 2 < size)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale++;
                }
                // decode with inSampleSize
                bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = scale;
                bitmapOptions.inScaled = true;
                // bitmapSize = null;
                if (square) {
                    return cropToSquare(BitmapFactory.decodeFile(
                            file.getAbsolutePath(), bitmapOptions));
                }
                return BitmapFactory.decodeFile(file.getAbsolutePath(),
                        bitmapOptions);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        if (bitmap != null)// make a square!
        {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                return Bitmap.createBitmap(bitmap,
                        ((bitmap.getWidth() - bitmap.getHeight()) / 2), 0,
                        bitmap.getHeight(), bitmap.getHeight());
            } else if (bitmap.getWidth() < bitmap.getHeight()) {
                return Bitmap.createBitmap(bitmap, 0,
                        ((bitmap.getHeight() - bitmap.getWidth()) / 2),
                        bitmap.getWidth(), bitmap.getWidth());
            }
            // else if they are equal, do nothing!
        }
        return bitmap;
    }

    public static Bitmap getThumbnail(ContentResolver contentResolver, long id, int kind) {
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA}, // Which columns to return
                MediaStore.Images.Media._ID + "=?", // Which rows to return
                new String[]{String.valueOf(id)}, // Selection arguments
                null);// order

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String filePath = cursor.getString(0);
            cursor.close();
            int rotation = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                int exifRotation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
                    switch (exifRotation) {
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id,
                    kind, null);
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } else
            return null;
    }

    public static Bitmap getFullSizeImage(Context context, Uri uri) {
        String filePath = null;
        if (AppUtils.validateUri(uri) && uri.toString().contains("file:") && !(uri.toString().contains("content:")))
            filePath = uri.getPath();
        else
            filePath = getRealPathFromURI(context, uri, MediaStore.Images.Media.DATA);
        if (filePath == null)
            return null;
        try {
            int rotation = 0;
            ExifInterface exifInterface = new ExifInterface(filePath);
            int exifRotation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
                switch (exifRotation) {
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                }
            }
            Matrix matrix = new Matrix();
            matrix.setRotate(rotation);
            Bitmap sourceBitmap = decodeUri(context, uri);
            if (sourceBitmap == null)
                return null;
            return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                    sourceBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            Logger.caughtException(e);
        }
        return null;
    }


    public static int getPixelsFromDP(int dps, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dps * scale + 0.5f));
    }

    public static String[] getRealPathWithIdFromURI(Context context, Uri contentUri, String type, String typeId) {
        Cursor cursor = null;
        String[] data = null, projection = new String[]{type, typeId};
        try {
            /*cursor = context.getContentResolver().
                    query(contentUri, projection,
                            type + "=? ", new String[]{contentUri.getPath()},
                            null);*/
            cursor = context.getContentResolver().
                    query(contentUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int pathIndex = cursor.getColumnIndexOrThrow(projection[0]);
                int idIndex = cursor.getColumnIndexOrThrow(projection[1]);
                data = new String[2];
                data[0] = cursor.getString(pathIndex);
                data[1] = Long.toString(cursor.getLong(idIndex));
                if (data.length != 2)
                    data = getDocumentRealPathWithIdFromUri(context, contentUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return data;
    }

    private static String[] getDocumentRealPathWithIdFromUri(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null,
                null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return new String[]{path, document_id};
    }

    private static String getRealPathFromURI(Context context, Uri contentUri, String type) {
        Cursor cursor = null;
        String path = null;
        try {
            // String[] proj = { MediaStore.Images.Media.DATA };
            String[] projection = {type};
            cursor = context.getContentResolver().
                    query(contentUri, projection, null,
                            null, null);
            if (cursor == null)
                return null;
            int column_index = cursor.getColumnIndexOrThrow(type);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            if (path == null)
                path = getDocumentRealPathFromUri(context, contentUri);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return path;
    }

    private static String getDocumentRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null,
                null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public static void createScaledImage(String sourceFile, String destinationFile, int desiredWidth, int desiredHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceFile, options);
        int srcWidth = options.outWidth;
        // int srcHeight = options.outHeight;

        if (desiredWidth > srcWidth)
            desiredWidth = srcWidth;

        int inSampleSize = 1;
        while (srcWidth / 2 > desiredWidth) {
            srcWidth /= 2;
            // srcHeight /= 2;
            inSampleSize *= 2;
        }
        float desiredScale = (float) desiredWidth / srcWidth;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(sourceFile, options);

        Matrix matrix = new Matrix();
        matrix.postScale(desiredScale, desiredScale);
        Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
                sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
                matrix, true);
        sampledSrcBitmap = null;
        try {
            FileOutputStream out = new FileOutputStream(destinationFile);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            scaledBitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateInSampleSizeUsingPower2(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public static int calculateInSampleSizeUsingRatio(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
                inSampleSize++;
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapFromURI(final Context context, final Uri uri) {
        int maxSize = 400;
        if (!AppUtils.validateUri(uri))
            return null;
        Bitmap bitmap = null;
        String realPathFromURI = getRealPathFromURI(context, uri, MediaStore.Images.Media.DATA);
        if (realPathFromURI != null) {
            File file = new File(realPathFromURI);
            if (file.exists())
                bitmap = getBitmapFromPath(maxSize, realPathFromURI);
        }
        return bitmap;
    }

    private static Bitmap getBitmapFromPath(int maxSize, String realPathFromURI) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realPathFromURI, options);
        options.inSampleSize = calculateInSampleSizeUsingPower2(options, maxSize, maxSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(realPathFromURI, options);
    }


    private static Bitmap decodeUri(Context context, Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = null;
            if (parcelFD != null) {
                imageSource = parcelFD.getFileDescriptor();
            }

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 900;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 2;
//            while (true) {
//                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
//                    break;
//                }
//                width_tmp /= 2;
//                height_tmp /= 2;
//                scale *= 2;
//            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
            return bitmap;

        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
        return null;
    }

    public static String convertToBase64andCompress(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return encodeToString(byteArrayOutputStream.toByteArray(), DEFAULT);
    }

    public static String convertToBase64(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)) {
            String base64 = encodeToString(byteArrayOutputStream.toByteArray(), DEFAULT);
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return base64;
        }
        return null;
    }

    public static String convertToBase64andCompressWith30(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return encodeToString(byteArrayOutputStream.toByteArray(), DEFAULT);
    }

    public static void compressBitmap(File file) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap == null)
            return;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] bitmapdata = byteArrayOutputStream.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
    }
}
