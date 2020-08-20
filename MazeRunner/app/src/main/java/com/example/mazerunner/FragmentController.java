package com.example.mazerunner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentController extends Fragment{
    View view;
    ImageButton buttonUp;

    public FragmentController(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.controller_fragment,container,false);
        buttonUp = (ImageButton) view.findViewById(R.id.buttonUp);
        return view;
    }


}