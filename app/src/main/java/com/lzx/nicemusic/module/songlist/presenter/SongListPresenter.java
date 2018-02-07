package com.lzx.nicemusic.module.songlist.presenter;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/2/5
 */

public class SongListPresenter extends BasePresenter<SongListContract.View> implements SongListContract.Presenter<SongListContract.View> {

    public int size = 10;
    private int offset = 0;
    private boolean isMore;

    @Override
    public void requestSongList(String title) {
        Disposable subscriber = getSongList(title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    isMore = list.size() >= size;
                    mView.onGetSongListSuccess(list);
                }, throwable -> {
                    LogUtil.i("error = " + throwable.getMessage());
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMoreSongList(String title) {
        if (isMore) {
            offset += 10;
            Disposable subscriber = getSongList(title)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        isMore = list.size() >= size;
                        mView.loadMoreSongListSuccess(list);
                    }, throwable -> {
                        LogUtil.i("error = " + throwable.getMessage());
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }

    private Observable<List<MusicInfo>> getSongList(String title) {
        int type = getListType(title);
        return RetrofitHelper.getMusicApi().requestMusicList(type, size, offset)
                .map(responseBody -> {
                    List<MusicInfo> list = DataHelper.fetchJSONFromUrl(responseBody);
                    for (MusicInfo info : list) {
                        RetrofitHelper.getMusicApi().playMusic(info.musicId)
                                .map(responseUrlBody -> {
                                    String json = responseUrlBody.string();
                                    json = json.substring(1, json.length() - 2);
                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONObject bitrate = jsonObject.getJSONObject("bitrate");
                                    return bitrate.getString("file_link");
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(url -> {
                                    info.musicUrl = url;
                                    list.add(info);
                                }, throwable -> {
                                    LogUtil.i("1error = " + throwable.getMessage());
                                });
                    }
                    return list;
                });
    }


    private Integer[] songCoverArray = new Integer[]{
            R.drawable.image_song_list,
            R.drawable.image_new_song,
            R.drawable.image_hot_song,
            R.drawable.image_rock,
            R.drawable.image_jazz,
            R.drawable.image_popular,
            R.drawable.image_europe,
            R.drawable.image_classic,
            R.drawable.image_love_song,
            R.drawable.image_television,
            R.drawable.image_internet
    };

    @Override
    public int getAlbumCover(String title) {
        return songCoverArray[getListTypeIndex(title)];
    }

    private int getListTypeIndex(String title) {
        int index = 0;
        switch (title) {
            case "我的歌单":
                index = 0;
                break;
            case "新歌榜":
                index = 1;
                break;
            case "热歌榜":
                index = 2;
                break;
            case "摇滚榜":
                index = 3;
                break;
            case "爵士":
                index = 4;
                break;
            case "流行":
                index = 5;
                break;
            case "欧美金曲榜":
                index = 6;
                break;
            case "经典老歌榜":
                index = 7;
                break;
            case "情歌对唱榜":
                index = 8;
                break;
            case "影视金曲榜":
                index = 9;
                break;
            case "网络歌曲榜":
                index = 10;
                break;
        }
        return index;
    }

    private int getListType(String title) {
        int type = 0;
        switch (title) {
            case "新歌榜":
                type = 1;
                break;
            case "热歌榜":
                type = 2;
                break;
            case "摇滚榜":
                type = 11;
                break;
            case "爵士":
                type = 12;
                break;
            case "流行":
                type = 16;
                break;
            case "欧美金曲榜":
                type = 21;
                break;
            case "经典老歌榜":
                type = 22;
                break;
            case "情歌对唱榜":
                type = 23;
                break;
            case "影视金曲榜":
                type = 24;
                break;
            case "网络歌曲榜":
                type = 25;
                break;
        }
        return type;
    }
}
