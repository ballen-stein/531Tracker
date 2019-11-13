package com.a531tracker.DetailFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.a531tracker.R;

import org.jetbrains.annotations.NotNull;

public class InformationFragment extends Fragment {

    private TextView descriptionHeader;
    private TextView descriptionInfo;
    private FrameLayout cancelFrame;

    private InformationFragmentListener clickListener;

    public static InformationFragment newInstance(String header, String desc){
        InformationFragment fragment = new InformationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Header", header);
        bundle.putString("Description", desc);
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface InformationFragmentListener {
        void closeInformation();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.information_fragment, container, false);
        setViews(view);
        if(getArguments() != null){
            setCancelButton();
            String headerText = getArguments().getString("Header");
            descriptionHeader.setText(headerText);
            String descText = getArguments().getString("Description");
            descriptionInfo.setText(descText);
        }
        return view;
    }


    private void setViews(View v){
        descriptionHeader = v.findViewById(R.id.fragment_settings_description_header);
        descriptionInfo = v.findViewById(R.id.fragment_settings_description_text);
        cancelFrame = v.findViewById(R.id.fragment_cancel_frame);
    }


    private void setCancelButton(){
        cancelFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.closeInformation();
            }
        });
    }


    @Override
    public void onAttach(@NotNull Context context){
        super.onAttach(context);
        clickListener = (InformationFragmentListener) context;
    }


    @Override
    public void onDetach(){
        super.onDetach();
        clickListener = null;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        setToNull();
    }


    private void setToNull(){
        descriptionHeader = null;
        descriptionInfo = null;
        cancelFrame = null;
    }
}