package com.a531tracker.DetailFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.a531tracker.R;

import org.jetbrains.annotations.NotNull;

public class SubmitAmrap extends Fragment {

    private TextView lastWeekView;
    private EditText thisWeekView;

    private Button cancelButton;
    private Button submitButton;

    private AllClicks listener;

    public static SubmitAmrap newInstance(String lastWeek){
        SubmitAmrap submitAmrap = new SubmitAmrap();
        Bundle bundle = new Bundle();
        bundle.putString("LastWeek", lastWeek);
        submitAmrap.setArguments(bundle);
        return submitAmrap;
    }

    public interface AllClicks {
        void closeAmrapFragment();
        void submitAmrapButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.amrap_fragment, container, false);
        setViews(view);
        if(getArguments() != null){
            cancelClickListener();
            submitClickListener();
            lastWeekView.setText(getArguments().getString("LastWeek"));
        }
        return view;
    }

    private void setViews(View v){
        lastWeekView = v.findViewById(R.id.fragment_amrap_last_week_number);
        thisWeekView = v.findViewById(R.id.fragment_amrap_input);
        cancelButton = v.findViewById(R.id.fragment_amrap_cancel_button);
        submitButton = v.findViewById(R.id.fragment_amrap_submit_button);
    }


    private void cancelClickListener(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.closeAmrapFragment();
            }
        });
    }


    private void submitClickListener(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.submitAmrapButton();
            }
        });
    }

    public int getAmrapNumber(){
        return Integer.parseInt(String.valueOf(thisWeekView.getText()));
    }


    @Override
    public void onAttach(@NotNull Context context){
        super.onAttach(context);
        if(context instanceof AllClicks)
            listener = (AllClicks) context;
        else
            throw new RuntimeException(context.toString());
    }


    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        setToNull();
    }


    private void setToNull(){
        lastWeekView = null;
        thisWeekView = null;
        cancelButton = null;
        submitButton = null;
    }
}
