package com.joinflatshare.utils.amazonaws;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.joinflatshare.constants.AwsConstants;
import com.joinflatshare.interfaces.OnUiEventClick;

public class AmazonDeleteFile {
    public static final int REQUEST_CODE_SUCCESS = 200;
    public static final int REQUEST_CODE_FAILURE = 201;

    public void delete(String path,@Nullable  OnUiEventClick onUiEventClick) {
        DeleteThread deleteThread = new DeleteThread(onUiEventClick, path);
        deleteThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class DeleteThread extends AsyncTask<Void, Void, Void> {
        boolean success = true;
        OnUiEventClick onUiEventClick;
        String path;

        DeleteThread(@Nullable OnUiEventClick onUiEventClick, String path) {
            this.onUiEventClick = onUiEventClick;
            this.path = path;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                initiateTransaction();
            } catch (Exception ex) {
                success = false;
            }
            return null;
        }

        private void initiateTransaction() {
            AwsConstants.INSTANCE.initialiseAwsSdk(text -> {
                if (text.equals("1")) {
                    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AwsConstants.INSTANCE.getAWS_ACCESS_KEY(),
                            AwsConstants.INSTANCE.getAWS_SECRET_KEY());
                    AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);
                    for (S3ObjectSummary file : s3Client.listObjects(AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(), path).getObjectSummaries()) {
                        s3Client.deleteObject(AwsConstants.INSTANCE.getAMAZON_S3_BUCKET_NAME(), file.getKey());
                    }
                    success = true;
                } else success = false;
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (onUiEventClick != null)
                onUiEventClick.onClick(null, success ? REQUEST_CODE_SUCCESS : REQUEST_CODE_FAILURE);
        }
    }
}
