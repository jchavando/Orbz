package com.ruppal.orbz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ruppal on 7/19/17.
 */

public class ViewHolderSong extends RecyclerView.ViewHolder {
    public TextView tvSongName;
    public TextView tvArtistName;
    public ImageView ivAlbumCover;
    public ImageView ivPause;
    public TextView tvService;

    public ViewHolderSong(View itemView, final ComplexRecyclerViewAdapter.SongAdapterListener mListener, final Context context) {
        super(itemView);
        //perform findViewById lookups
        tvSongName= (TextView) itemView.findViewById(R.id.tvSongName);
        tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
        ivAlbumCover = (ImageView) itemView.findViewById(R.id.ivAlbumCover);
        ivPause = (ImageView) itemView.findViewById(R.id.ivPause);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Toast.makeText(context, "clicked a song", Toast.LENGTH_LONG).show();
                mListener.onItemSelected(v, position);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                mListener.onItemLongSelected(v, position);
                return true;
            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable playButton = context.getResources().getDrawable(R.drawable.exo_controls_play);
                ((ImageView) v).setImageDrawable(playButton);
                int position = getAdapterPosition();
                mListener.onPauseButtonClicked(v, position);
            }
        });

        tvService = (TextView) itemView.findViewById(R.id.tvService);
    }
}