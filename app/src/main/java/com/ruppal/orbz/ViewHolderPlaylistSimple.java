package com.ruppal.orbz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by ruppal on 7/22/17.
 */


public class ViewHolderPlaylistSimple extends RecyclerView.ViewHolder{
    TextView tvPlaylistName;
    RadioButton rbSelectPlaylist;

    public ViewHolderPlaylistSimple(final View itemView, final ComplexRecyclerViewAdapter.PlaylistSimpleAdapterListener mPlaylistSimpleListener, final Context context) {
        super(itemView);
        tvPlaylistName = (TextView) itemView.findViewById(R.id.tvPlaylistName);
        rbSelectPlaylist = (RadioButton) itemView.findViewById(R.id.rbSelectPlaylist);
        rbSelectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
//                Toast.makeText(context, "clicked simple playlist " + tvPlaylistName.getText(), Toast.LENGTH_SHORT).show();
                mPlaylistSimpleListener.onPlaylistSimpleItemSelected(itemView, position);
            }
        });
    }
}