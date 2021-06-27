package com.secure.safespace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.secure.util.Path;
import com.secure.util.SecureAdapter;

import java.util.List;

public class AllFileFragment extends Fragment implements AdapterView.OnItemClickListener{
    public List<Path> list;
    SecureAdapter secureAdapter;
    SecureActivity.callbackListener callbackListener;

    public AllFileFragment(SecureAdapter secureAdapter, SecureActivity.callbackListener callbackListener) {
        this.secureAdapter = secureAdapter;
        this.callbackListener = callbackListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_file_fragment, container, false);
        GridView recyclerView = (GridView) view.findViewById(R.id.recycle_view);
        recyclerView.setAdapter(secureAdapter);
        recyclerView.setOnItemClickListener(this);
        callbackListener.updateMenu(false);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == (R.id.recycle_view)) {
            Log.d("nhungltk", "onItemClick: ");
            callbackListener.callback(position);
        }
    }
}
