package com.thnki.classroom.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thnki.classroom.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StudentResultViewHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.userImage)
    public ImageView mImageView;

    @Bind(R.id.fullName)
    public TextView mFullName;


    public View mItemView;

    public StudentResultViewHolder(View itemView)
    {
        super(itemView);
        mItemView = itemView;
        ButterKnife.bind(this, itemView);
    }
}