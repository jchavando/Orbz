package com.ruppal.orbz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ruppal on 7/19/17.
 */

public class ViewHolderPlaylist extends RecyclerView.ViewHolder{
    ImageView ivPlayListArt;
    TextView tvPlaylistName;
    TextView tvOwnerName;

    public ViewHolderPlaylist(View itemView) {
        super(itemView);
        ivPlayListArt = (ImageView) itemView.findViewById(R.id.ivPlaylistArt);
        tvPlaylistName = (TextView) itemView.findViewById(R.id.tvPlaylistName);
        tvOwnerName = (TextView) itemView.findViewById(R.id.tvOwnerName);
    }
}