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

import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.LastFMClient;

/**
 * Created by jchavando on 7/31/17.
 */

public class ViewCommentDialogFragment extends DialogFragment {

    EditText etNewComment;

    LastFMClient lastFMClient;
    Context context;
    AddCommentListener commentListener;

    public interface AddCommentListener{
        void onFinishDialog(String newComment);
    }

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private AdapterView.OnItemSelectedListener listener;

    public ViewCommentDialogFragment(AddCommentListener addPlaylistListener){
        //required empty constructor
        commentListener = addPlaylistListener;
    }

    public static ViewCommentDialogFragment newInstance(String title, AddCommentListener addCommentListener ) { //what is title, for Twitter used Tweet
        ViewCommentDialogFragment fragment = new ViewCommentDialogFragment(addCommentListener ); //TODO fix
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
                //communicate back to PlaylistFragment
                commentListener.onFinishDialog(etNewComment.getText().toString());
                //login
                //go back
                dismiss();
            }
        });
    }
}