package com.joinflatshare.api.retrofit;

/**
 * Created by debopam on 18/08/23
 */
public interface OnResponseCallback<T> {
    void oncallBack(T response);

    default void onError(Throwable throwable) {

    }
}

