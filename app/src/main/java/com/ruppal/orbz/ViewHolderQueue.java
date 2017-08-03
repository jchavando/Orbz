package com.ruppal.orbz;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by jchavando on 8/1/17.
 */

public class ViewHolderQueue extends ViewHolderSong  {

    public ImageButton ibAddComment;

    public ViewHolderQueue(View itemView, final ComplexRecyclerViewAdapter.SongAdapterListener mListener, Context context) {
        super(itemView, mListener, context);
        ibAddComment = (ImageButton) itemView.findViewById(R.id.ibAddComment);

        ibAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onAddCommentClicked(v, position); //.onAddPlaylistClicked
                    Log.d("view holder queue", String.valueOf(this));
                    hasComment = true;
                    //set the picture differently
                }
            });
        }

}


