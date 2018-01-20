package com.lzx.nicemusic.lib.playback;

import android.support.v4.media.session.MediaSessionCompat;

import com.lzx.nicemusic.lib.bean.MusicInfo;

/**
 * Created by xian on 2018/1/20.
 */

public interface Playback {

    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     *
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    /**
     * Set the latest playback state as determined by the caller.
     */
    void setState(int state);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getState();

    /**
     * @return boolean that indicates that this is ready to be used.
     */
    boolean isConnected();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return 获取当前播放流的进度位置
     */
    long getCurrentStreamPosition();

    /**
     * Queries the underlying stream and update the internal last known stream position.
     */
    void updateLastKnownStreamPosition();

    void play(MusicInfo info);

    void pause();

    void seekTo(long position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();


    interface Callback {
        /**
         * 当前音乐播放完毕
         */
        void onPlayCompletion();

        /**
         * 在播放状态改变时，可以实现这个回调来更新媒体会话的播放状态。
         *
         * @param state
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);

}
