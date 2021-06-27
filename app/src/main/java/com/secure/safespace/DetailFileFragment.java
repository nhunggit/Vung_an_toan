package com.secure.safespace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.secure.util.Path;

import java.io.File;
import java.util.List;

public class DetailFileFragment extends Fragment {
    private int possistion;
    private List<Path> list;
    SecureActivity.callbackListener callbackListener;

    public DetailFileFragment(int possistion, List<Path>list, SecureActivity.callbackListener callbackListener) {
        this.possistion = possistion;
        this.list=list;
        this.callbackListener= callbackListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        callbackListener.updateMenu(true);
        View view= inflater.inflate(R.layout.external_file,container,false);
        ImageView imageView=(ImageView)view.findViewById(R.id.image_view);
        VideoView mVideoView= (VideoView)view.findViewById(R.id.video_view);

        Path path= list.get(possistion);
        if((new File(path.getDecrypt())).getName().endsWith(".mp4")) {
            imageView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path.getDecrypt(), MediaStore.Video.Thumbnails.MINI_KIND);
            Drawable drawable=new BitmapDrawable(getResources(), bitmap);
            mVideoView.setHorizontalScrollbarThumbDrawable(drawable);
            mVideoView.setVideoPath(path.getDecrypt());
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);
        }else {
            mVideoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(path.getDecrypt());
            imageView.setImageBitmap(bitmap);
        }
        return view;
    }
}
