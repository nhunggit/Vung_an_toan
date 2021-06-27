package com.secure.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.VideoView;

import com.secure.safespace.R;
import com.secure.safespace.SecureActivity;

import java.io.File;
import java.util.List;

import com.secure.util.Process;

public class SecureAdapter extends BaseAdapter {
    List<Path> list;
    Context context;
    Path path;
    Process process;
    SecureActivity.callbackListener callbackListener;
    public ImageView imageButton;
    public VideoView mVideoView;

    public SecureAdapter(List<Path> list, SecureActivity.callbackListener callbackListener) {
        this.list = list;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_view, parent, false);
        }

        path = list.get(position);
        imageButton = (ImageView) convertView.findViewById(R.id.image);

        Bitmap bitmap;
        File file = new File(path.getDecrypt());
        if (file.getName().endsWith(".mp4")) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path.getDecrypt(), MediaStore.Video.Thumbnails.MINI_KIND);
        } else {
//            mVideoView.setVisibility(View.GONE);
//            imageButton.setVisibility(View.VISIBLE);
            bitmap = BitmapFactory.decodeFile(path.getDecrypt());
        }
        imageButton.setImageBitmap(bitmap);
        convertView.setClickable(false);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackListener.callback(position);
            }
        });
        return convertView;
    }
}
