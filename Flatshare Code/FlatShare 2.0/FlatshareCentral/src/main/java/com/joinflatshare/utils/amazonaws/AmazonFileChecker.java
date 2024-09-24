package com.joinflatshare.utils.amazonaws;

import static com.joinflatshare.utils.amazonaws.AmazonUploadFile.REQUEST_CODE_FAILURE;
import static com.joinflatshare.utils.amazonaws.AmazonUploadFile.REQUEST_CODE_SUCCESS;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.constants.AwsConstants;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.utils.logger.Logger;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

public class AmazonFileChecker {
    private final Activity activity;

    public AmazonFileChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkObject(String object, OnUiEventClick onUiEventClick) {
        if (TextUtils.isEmpty(object)) {
            onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
            return;
        }
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxErrorRetry(3) // 3 retries
                .withConnectionTimeout(120000) // 120,000 ms
                .withSocketTimeout(120000); // 120,000 ms

        AwsConstants.INSTANCE.initialiseAwsSdk(text -> {
            if (text.equals("1")) {
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AwsConstants.INSTANCE.getAWS_ACCESS_KEY(),
                        AwsConstants.INSTANCE.getAWS_SECRET_KEY());
                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1), clientConfiguration);
                Thread thread = new Thread(() -> {
                    if (!AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME().isEmpty()) {
                        try {
                            boolean check = s3Client.doesObjectExist(AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(), object);
                            activity.runOnUiThread(() -> {
                                if (!check)
                                    onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                                else {
                                    onUiEventClick.onClick(null, REQUEST_CODE_SUCCESS);
                                }
                            });
                        } catch (Exception exception) {
                            onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                        }
                    } else {
                        String userId = FlatShareApplication.Companion.getDbInstance().userDao().getUser().getId();
                        MixpanelUtils.INSTANCE.logError("Bucket name is empty for user " + userId, Logger.LOG_TYPE_ERROR);
                        onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                    }
                });
                thread.start();
            } else onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
        });
    }

    public void getUrl(String object, OnUiEventClick onUiEventClick) {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxErrorRetry(3) // 3 retries
                .withConnectionTimeout(120000) // 120,000 ms
                .withSocketTimeout(120000); // 120,000 ms

        AwsConstants.INSTANCE.initialiseAwsSdk(text -> {
            if (text.equals("1")) {
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AwsConstants.INSTANCE.getAWS_ACCESS_KEY(),
                        AwsConstants.INSTANCE.getAWS_SECRET_KEY());
                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1), clientConfiguration);
                Thread thread = new Thread(() -> {
                    String url = s3Client.getResourceUrl(AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(), object);
                    activity.runOnUiThread(() -> {
                        if (url == null || url.isEmpty())
                            onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                        else {
                            final Intent intnt = new Intent();
                            intnt.putExtra("url", url);
                            onUiEventClick.onClick(intnt, REQUEST_CODE_SUCCESS);
                        }
                    });
                });
                thread.start();
            } else onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
        });
    }
}
