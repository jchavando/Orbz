package com.ruppal.orbz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ruppal on 7/19/17.
 */

public class ViewHolderSong extends RecyclerView.ViewHolder {
    public TextView tvSongName;
    public TextView tvArtistName;
    public ImageView ivAlbumCover;
    public TextView tvService;
    public ImageButton ibAddToPlaylist;
    public ImageButton ibAddComment;
    ComplexRecyclerViewAdapter complexRecyclerViewAdapter;

    public ViewHolderSong(View itemView, final ComplexRecyclerViewAdapter.SongAdapterListener mListener, final Context context) {
        super(itemView);
        //perform findViewById lookups
        tvSongName= (TextView) itemView.findViewById(R.id.tvSongName);
        tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
        ivAlbumCover = (ImageView) itemView.findViewById(R.id.ivAlbumCover);
        ibAddToPlaylist = (ImageButton) itemView.findViewById(R.id.ibAddToPlaylist);
        ibAddComment = (ImageButton) itemView.findViewById(R.id.ibAddComment);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
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

        ibAddToPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Drawable playButton = context.getResources().getDrawable(R.drawable.exo_controls_play);
                //((ImageButton) v).setImageDrawable(playButton);
                int position = getAdapterPosition();
                mListener.onAddPlaylistSongClicked(v, position);
            }
        });

//        if(getQueued()) { //its queued
//            ibAddComment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    mListener.onAddCommentClicked(v, position); //.onAddPlaylistClicked
//                }
//            });
//        }



        tvService = (TextView) itemView.findViewById(R.id.tvService);
    }
}