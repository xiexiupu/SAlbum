package com.sharry.lib.album;

import androidx.annotation.NonNull;

/**
 * 拍照回调
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-08-30 09:08
 */
public interface TakerCallback {

    /**
     * 拍照完成的回调
     *
     * @param newMeta 照片输出路径
     */
    void onCameraTakeComplete(@NonNull MediaMeta newMeta);

}
