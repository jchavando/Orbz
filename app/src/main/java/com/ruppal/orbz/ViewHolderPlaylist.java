package com.ruppal.orbz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ruppal on 7/19/17.
 */

public class ViewHolderPlaylist extends RecyclerView.ViewHolder{
    ImageView ivPlayListArt;
    TextView tvPlaylistName;
    TextView tvOwnerName;

    public ViewHolderPlaylist(View itemView, final ComplexRecyclerViewAdapter.PlaylistAdapterListener mPlaylistListener, final Context context) {
        super(itemView);
        ivPlayListArt = (ImageView) itemView.findViewById(R.id.ivPlaylistArt);
        tvPlaylistName = (TextView) itemView.findViewById(R.id.tvPlaylistName);
        tvOwnerName = (TextView) itemView.findViewById(R.id.tvOwnerName);
        //listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Toast.makeText(context, "clicked playlist", Toast.LENGTH_LONG).show();
                mPlaylistListener.onPlaylistItemSelected(v, position);

            }
        });
    }
}