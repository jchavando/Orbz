package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.models.Song;

/**
 * Created by jchavando on 7/31/17.
 */

public class ViewCommentDialogFragment extends DialogFragment {

    TextView tvComment;
    Song song;
    ComplexRecyclerViewAdapter.AddCommentAdapterListener commentShownListener;

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private AdapterView.OnItemSelectedListener listener;

    public ViewCommentDialogFragment(Song song, ComplexRecyclerViewAdapter.AddCommentAdapterListener listener){
        //required empty constructor
        commentShownListener = listener;
        this.song = song;
    }

    public static ViewCommentDialogFragment newInstance(String title, Song song, ComplexRecyclerViewAdapter.AddCommentAdapterListener listener) { //what is title, for Twitter used Tweet
        ViewCommentDialogFragment fragment = new ViewCommentDialogFragment(song, listener);
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragments_view_comment, container); //,false
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvComment = (TextView) view.findViewById(R.id.tvComment);
        tvComment.setText(song.getComment());
        String title = getArguments().getString("title", "Enter username");
        getDialog().setTitle(title);
        tvComment.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
