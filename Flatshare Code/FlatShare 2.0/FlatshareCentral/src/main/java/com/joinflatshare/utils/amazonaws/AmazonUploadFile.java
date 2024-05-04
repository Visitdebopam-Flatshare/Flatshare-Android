package com.joinflatshare.utils.amazonaws;

import android.content.Intent;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.AwsConstants;
import com.joinflatshare.constants.UrlConstants;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.utils.helper.CommonMethod;

import java.io.File;

public class AmazonUploadFile {
    public static final String AWS_TYPE_PROFILE_IMAGE = "AWS_PROFILE_IMAGE";
    public static final String AWS_TYPE_USER_IMAGE = "AWS_USER_IMAGE";
    public static final String AWS_TYPE_FLAT_IMAGE = "AWS_FLAT_IMAGE";
    public static final String AWS_TYPE_FLAT_VIDEO = "AWS_FLAT_VIDEO";
    public static final String AWS_TYPE_FLAT_DP = "AWS_FLAT_DP";
    public static final int REQUEST_CODE_SUCCESS = 200;
    protected static final int REQUEST_CODE_FAILURE = 201;
    protected static final int REQUEST_CODE_COMPLETE_PERCENT = 203;

    public void upload(File uploadFile, String type, OnUiEventClick onUiEventClick) {
        if (uploadFile == null || type.isEmpty()) {
            onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
            return;
        }
        initiateTransaction(uploadFile, type, onUiEventClick);
    }

    private void initiateTransaction(File uploadFile, String type, OnUiEventClick onUiEventClick) {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxErrorRetry(3) // 3 retries
                .withConnectionTimeout(120000) // 120,000 ms
                .withSocketTimeout(120000); // 120,000 ms

        AwsConstants.INSTANCE.initialiseAwsSdk(text -> {
            if (text.equals("1")) {
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AwsConstants.INSTANCE.getAWS_ACCESS_KEY(),
                        AwsConstants.INSTANCE.getAWS_SECRET_KEY());
                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1), clientConfiguration);
                TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(FlatShareApplication.Companion.getInstance())
                                .s3Client(s3Client)
                                .build();
                final String path = getPath(type);
                if (path.isEmpty()) {
                    onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                    return;
                }
                final TransferObserver observer = transferUtility.upload(
                        AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(),  //this is the bucket name on S3
                        path, //this is the path and name
                        uploadFile, //path to the file locally
                        CannedAccessControlList.PublicRead //to make the file public
                );
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        CommonMethod.INSTANCE.makeLog("Transfer state", state.toString());
                        if (state.equals(TransferState.COMPLETED)) {
                            String serverPath = s3Client.getResourceUrl(AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(), path);
                            Intent intent = new Intent();
                            intent.putExtra("id", id);
                            intent.putExtra("localpath", path);
                            intent.putExtra("serverpath", serverPath);
                            onUiEventClick.onClick(intent, REQUEST_CODE_SUCCESS);
                        } else if (state.equals(TransferState.FAILED)) {
                            onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        Intent intent = new Intent();
                        intent.putExtra("complete", percentDone);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
                    }
                });
            } else {
                onUiEventClick.onClick(null, REQUEST_CODE_FAILURE);
            }
        });
    }

    private String getPath(String type) {
        String userId = AppConstants.loggedInUser.getId();
        String path = "";
        switch (type) {
            case AWS_TYPE_PROFILE_IMAGE:
                path = userId + "/dp_" + System.currentTimeMillis() + ".jpg";
                break;
            case AWS_TYPE_FLAT_IMAGE:
                String id = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData().getMongoId();
                if (id.isEmpty())
                    return "";
                path = id + "/Flat/" + System.currentTimeMillis() + ".jpg";
                break;
            case AWS_TYPE_USER_IMAGE:
                path = userId + "/User/" + System.currentTimeMillis() + ".jpg";
                break;
            case AWS_TYPE_FLAT_VIDEO:
                id = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData().getMongoId();
                if (id.isEmpty())
                    return "";
                path = id + "/Verification.mp4";
                break;
            case AWS_TYPE_FLAT_DP:
                id = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData().getMongoId();
                if (id.isEmpty())
                    return "";
                path = id + "/flat.jpg";
                break;
            default:
                path = userId + "/" + System.currentTimeMillis() + ".jpg";
        }
        return UrlConstants.INSTANCE.getUSER_IMAGE_URL() + path;
    }
}
