package com.ruppal.orbz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.LastFMClient;
import com.ruppal.orbz.models.Song;


/**
 * Created by jchavando on 7/31/17.
 */

public class AddCommentDialogFragment extends DialogFragment {

    EditText etNewComment;
    Song song;
    ImageButton commentButton;

    LastFMClient lastFMClient;
    Context context;
    ComplexRecyclerViewAdapter.AddCommentAdapterListener commentListener;

    public interface AddCommentListener{
        //void onFinishDialog(String newComment);
    }

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private AdapterView.OnItemSelectedListener listener;

    public AddCommentDialogFragment(Song song, ComplexRecyclerViewAdapter.AddCommentAdapterListener listener){
        //required empty constructor
        commentListener = listener;
        this.song = song;
    }

    public static AddCommentDialogFragment newInstance(String title, Song song, ComplexRecyclerViewAdapter.AddCommentAdapterListener listener) { //what is title, for Twitter used Tweet
        AddCommentDialogFragment fragment = new AddCommentDialogFragment(song, listener);
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragments_add_comment, container); //,false
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etNewComment = (EditText) view.findViewById(R.id.etNewComment);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter username");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etNewComment.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Button btAddComment = (Button) view.findViewById(R.id.btAddComment);
        btAddComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //communicate back to QueueFragment
                if (commentListener!=null) {
                    commentListener.addComment(etNewComment.getText().toString(), song);
//                    Toast.makeText(getContext(), "added comment", Toast.LENGTH_SHORT).show();
                    //go back
                    dismiss();
                }
            }
        });
    }
}