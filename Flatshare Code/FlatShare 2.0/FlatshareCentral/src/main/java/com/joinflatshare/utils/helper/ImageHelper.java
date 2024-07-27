package com.joinflatshare.utils.helper;

import static com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_PICK_IMAGE;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.debopam.ImagePicker;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.UrlConstants;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.interfaces.OnStringFetched;
import com.joinflatshare.pojo.notification.NotificationItem;
import com.joinflatshare.pojo.requests.Requester;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile;
import com.joinflatshare.utils.logger.Logger;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageHelper {
    @Deprecated
    public static String getProfileImage(String userId) {
        if (TextUtils.isEmpty(userId))
            return "";
        return UrlConstants.INSTANCE.getUSER_IMAGE_URL() + userId + "/dp.jpg";
    }

    public static String getProfileImageWithAwsFromPath(String path) {
        String link = "";
        if (!TextUtils.isEmpty(path)) {
            link = UrlConstants.INSTANCE.getIMAGE_URL() + path;
            CommonMethod.INSTANCE.makeLog("Image Url", link);
        }
        return link;
    }

    @Deprecated
    public static String getProfileImageWithAws(User user) {
        String link = "";
        if (TextUtils.isEmpty(user.getDp())) {
            link = UrlConstants.INSTANCE.getIMAGE_URL() + getProfileImage(user.getId());
        } else link = UrlConstants.INSTANCE.getIMAGE_URL() + user.getDp();
        CommonMethod.INSTANCE.makeLog("Profile image url", link);
        return link;
    }

    @Deprecated
    public static String getProfileImageWithAws(NotificationItem item) {
        String link = "";
        if (TextUtils.isEmpty(item.getDp())) {
            link = UrlConstants.INSTANCE.getIMAGE_URL() + getProfileImage(item.getId());
        } else link = UrlConstants.INSTANCE.getIMAGE_URL() + item.getDp();
        CommonMethod.INSTANCE.makeLog("Profile image url", link);
        return link;
    }

    @Deprecated
    public static String getProfileImageWithAws(@Nullable Requester requester) {
        String link = "";
        if (requester != null) {
            if (TextUtils.isEmpty(requester.getDp()))
                link = UrlConstants.INSTANCE.getIMAGE_URL() + getProfileImage(requester.getId());
            else link = UrlConstants.INSTANCE.getIMAGE_URL() + requester.getDp();
        }
        CommonMethod.INSTANCE.makeLog("Profile image url", link);
        return link;
    }

    public static String getUserImagesWithAws(String imageLink) {
        return UrlConstants.INSTANCE.getIMAGE_URL() + imageLink;
    }

    public static String getFlatImagesWithAws(String imageLink) {
        return UrlConstants.INSTANCE.getIMAGE_URL() + imageLink;
    }

    public static String getFlatDp(String flatId) {
        return UrlConstants.INSTANCE.getUSER_IMAGE_URL() + flatId + "/flat.jpg";
    }

    public static String getFlatDpWithAws(String flatId) {
        return UrlConstants.INSTANCE.getIMAGE_URL() + getFlatDp(flatId);
    }

    public static void loadProfileImage(Context context, CircleImageView img_profile, TextView txtDp, User user) {
        try {
            txtDp.setVisibility(View.GONE);
            String url = UrlConstants.INSTANCE.getIMAGE_URL() + user.getDp();
            CommonMethod.INSTANCE.makeLog("Image URL", url);
            Glide.with(context).clear(img_profile);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).signature(new ObjectKey(System.currentTimeMillis()));
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, @androidx.annotation.Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                    char fname = user.getName().getFirstName().charAt(0);
                    char lname = user.getName().getLastName().charAt(0);
                    txtDp.setVisibility(View.VISIBLE);
                    img_profile.setVisibility(View.GONE);
                    txtDp.setText("" + fname + lname);
                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    img_profile.setVisibility(View.VISIBLE);
                    img_profile.setImageDrawable(resource);
                    return false;
                }
            }).into(img_profile);
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log(Logger.LOG_TYPE_ERROR, exception.getMessage() == null ? "" : exception.getMessage());
        }
    }

    public static void loadProfileImage(Context context, CircleImageView img_profile, TextView txtDp, String url, String name) {
        try {
            txtDp.setVisibility(View.GONE);
            CommonMethod.INSTANCE.makeLog("Image URL", url);
            Glide.with(context).clear(img_profile);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).signature(new ObjectKey(System.currentTimeMillis()));
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, @androidx.annotation.Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                    char fname = name.charAt(0);
                    char lname = name.charAt(0);
                    txtDp.setVisibility(View.VISIBLE);
                    img_profile.setVisibility(View.GONE);
                    txtDp.setText("" + fname + lname);
                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    img_profile.setVisibility(View.VISIBLE);
                    img_profile.setImageDrawable(resource);
                    return false;
                }
            }).into(img_profile);
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log(Logger.LOG_TYPE_ERROR, exception.getMessage() == null ? "" : exception.getMessage());
        }
    }


    public static void loadImageWithCacheClear(Activity activity, int defaultIcon, ImageView img_profile, String url) {
        try {
            if (defaultIcon == 0) defaultIcon = R.drawable.ic_image_add;
            CommonMethod.INSTANCE.makeLog("Image URL", url);
            if (img_profile != null) {
                Glide.with(activity).clear(img_profile);
                RequestOptions options = new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()));
                Glide.with(activity).load(url).placeholder(defaultIcon).error(defaultIcon).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(options).into(img_profile);
            }
        } catch (Exception exception) {
            Logger.log(Logger.LOG_TYPE_ERROR, exception.getMessage() == null ? "" : exception.getMessage());
        }
    }

    public static void loadImage(Context context, int defaultIcon, ImageView
            img_profile, String url) {
        try {
            if (defaultIcon == 0) defaultIcon = R.drawable.ic_image_add;
            CommonMethod.INSTANCE.makeLog("Image URL", url);
            if (img_profile != null)
                Glide.with(context).load(url).placeholder(defaultIcon).error(defaultIcon).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(img_profile);
        } catch (Exception exception) {
            Logger.log(Logger.LOG_TYPE_ERROR, exception.getMessage() == null ? "" : exception.getMessage());
        }
    }

    public static void loadImageWithException(Activity activity, int defaultIcon, ImageView
            img_profile, String url, OnStringFetched callback) {
        try {
            if (defaultIcon == 0) defaultIcon = R.drawable.ic_image_add;
            CommonMethod.INSTANCE.makeLog("Image URL", url);
            if (img_profile != null)
                Glide.with(activity).load(url).placeholder(defaultIcon).diskCacheStrategy(DiskCacheStrategy.RESOURCE).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, @androidx.annotation.Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        callback.onFetched("0");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(img_profile);
        } catch (Exception exception) {
            Logger.log(Logger.LOG_TYPE_ERROR, exception.getMessage() == null ? "" : exception.getMessage());
        }
    }

    public static void showImageSlider(Activity activity, ArrayList<String> images,
                                       int startPosition) {
        View overlayView = activity.getLayoutInflater().inflate(R.layout.include_image_hover_view, null);
        final StfalconImageViewer<String> stfalconViewer = new StfalconImageViewer.Builder<>(activity, images, (imageView, imageUrl) -> loadImage(activity, R.mipmap.ic_launcher, imageView, imageUrl)).allowZooming(true).withBackgroundColor(ContextCompat.getColor(activity, R.color.color_bg)).withHiddenStatusBar(true).withStartPosition(startPosition).withOverlayView(overlayView).build();
        overlayView.findViewById(R.id.img_topbar_back).setOnClickListener(view1 -> stfalconViewer.dismiss());
        stfalconViewer.show();
    }

    public static void clearImageCache(Activity activity) {
        long lastClearTime = FlatShareApplication.Companion.getDbInstance().userDao().getLong(UserDao.LAST_GLIDE_CACHE_CLEAR);
        long currentTime = System.currentTimeMillis();
        if (lastClearTime > 0L) {
            if ((currentTime - lastClearTime) < DateUtils.HOUR_IN_MILLIS * 24) return;
        }
        AsyncTask.execute(() -> Glide.get(activity).clearDiskCache());
        Glide.get(activity).clearMemory();
        FlatShareApplication.Companion.getDbInstance().userDao().insert(UserDao.LAST_GLIDE_CACHE_CLEAR, currentTime);
    }

    public static void pickImageFromCamera(ComponentActivity activity, float cropWidth, float cropHeight) {
        ImagePicker.Builder builder = ImagePicker.Companion.with(activity);
        builder.galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"}).cameraOnly().compress(1024);
        if (cropWidth > 0 && cropHeight > 0) {
            builder.crop(cropWidth, cropHeight);
        }
        builder.start(REQUEST_CODE_PICK_IMAGE);
    }

    public static void pickImageFromCamera(ComponentActivity activity, float cropWidth, float cropHeight,
                                           int requestCode) {
        ImagePicker.Builder builder = ImagePicker.Companion.with(activity);
        builder.galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"}).cameraOnly().compress(500);
        if (cropWidth > 0 && cropHeight > 0) {
            builder.crop(cropWidth, cropHeight);
        }
        builder.start(requestCode);
    }

    public static void pickImageFromGallery(ComponentActivity activity, float cropWidth, float cropHeight) {
        ImagePicker.Builder builder = ImagePicker.Companion.with(activity);
        builder.galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"}).galleryOnly().compress(1024);
        if (cropWidth > 0 && cropHeight > 0) {
            builder.crop(cropWidth, cropHeight);
        }
        builder.start(REQUEST_CODE_PICK_IMAGE);
    }

    public static void pickImageFromGallery(ComponentActivity activity, float cropWidth, float cropHeight,
                                            int requestCode) {
        ImagePicker.Builder builder = ImagePicker.Companion.with(activity);
        builder.galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"}).galleryOnly().compress(500);
        if (cropWidth > 0 && cropHeight > 0) {
            builder.crop(cropWidth, cropHeight);
        }
        builder.start(requestCode);
    }

    public static File copyFile(BaseActivity activity, Uri uri) {
        File tempFile = null;
        try {
            tempFile = new File(activity.getCacheDir().getAbsolutePath(), System.currentTimeMillis() + ".jpg");
            if (tempFile.exists())
                tempFile.delete();
            tempFile.createNewFile();
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            OutputStream output = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.close();
            inputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return tempFile;
    }

    public static void deleteOldProfileImage() {
        assert AppConstants.loggedInUser != null;
        String oldDp = AppConstants.loggedInUser.getDp();
        if (oldDp != null && !oldDp.isEmpty()) new AmazonDeleteFile().delete(oldDp, null);
    }
}