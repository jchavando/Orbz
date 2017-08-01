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
import android.widget.EditText;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;

/**
 * Created by jchavando on 7/31/17.
 */

public class ViewCommentDialogFragment extends DialogFragment {

    EditText tvComment;

    Context context;
    ComplexRecyclerViewAdapter.AddCommentAdapterListener commentShownListener;

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private AdapterView.OnItemSelectedListener listener;

    public ViewCommentDialogFragment(ComplexRecyclerViewAdapter.AddCommentAdapterListener listener){
        //required empty constructor
        commentShownListener = listener;
    }

    public static ViewCommentDialogFragment newInstance(String title, ComplexRecyclerViewAdapter.AddCommentAdapterListener listener) { //what is title, for Twitter used Tweet
        ViewCommentDialogFragment fragment = new ViewCommentDialogFragment(listener); //TODO fix
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
        // Get field from view
        tvComment = (EditText) view.findViewById(R.id.tvComment);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter username");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        tvComment.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        Button btAddComment = (Button) view.findViewById(R.id.btAddComment);
//        btAddComment.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //communicate back to PlaylistFragment
//                commentShownListener.addComment(etNewComment.getText().toString());
//                Toast.makeText(getContext(), "clicked on comment", Toast.LENGTH_SHORT).show();
//                //login
//                //go back
//                dismiss();
//            }
//        });
    }
}
