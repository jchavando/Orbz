package com.ruppal.orbz.fragments;

//import android.app.DialogFragment;

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
 * Created by jchavando on 7/17/17.
 */

public class AddPlaylistDialogFragment extends DialogFragment {
    EditText etNewPlaylist;

    LastFMClient lastFMClient;
    Context context;

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private AdapterView.OnItemSelectedListener listener;

    public AddPlaylistDialogFragment(){
        //required empty constructor
    }


    public static AddPlaylistDialogFragment newInstance(String title) { //what is title, for Twitter used Tweet
        AddPlaylistDialogFragment fragment = new AddPlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //inResponseToTweet = Parcels.unwrap(getArguments().getParcelable("responseToTweet"));
//        context = getActivity();
//        lastFMClient = new LastFMClient(context);
//        Log.i("fragment", "in on create. in response to");
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragments_add_playlist, container); //,false
        return view;
    }


//    public interface OnItemSelectedListener {
//  // This can be any number of events to be sent to the activity
//        void enteredLoginInfo(String username, String password);
//   }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etNewPlaylist = (EditText) view.findViewById(R.id.etNewPlaylist);
        // Fetch arguments from bundle and set title

        String title = getArguments().getString("title", "Enter username");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etNewPlaylist.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Button btAddPlaylist = (Button) view.findViewById(R.id.btAddPlaylist);
        btAddPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //communicate back to PlaylistFragment
                AddPlaylistListener listener = (AddPlaylistListener) getContext();

                listener.onFinishDialog(etNewPlaylist.getText().toString());
                //login
                //go back
                dismiss();
            }
        });
    }
    public interface AddPlaylistListener{
        void onFinishDialog(String newPlaylist);
    }


//    @Override
//    public void onAttach(Context context) {
//      super.onAttach(context);
//       if (context instanceof OnItemSelectedListener) {
//           //listener = (OnItemSelectedListener) context;
//       } else {
//           throw new ClassCastException(context.toString() + " must implement MyListFragment.OnItemSelectedListener");
//       }
//    }

//    public void onDetach(){
//        super.onDetach();
//        this.listener = null;
//    }
}


