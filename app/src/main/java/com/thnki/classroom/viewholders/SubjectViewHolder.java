package com.thnki.classroom.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thnki.classroom.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubjectViewHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.classTeacherImage)
    public ImageView mTeacherImage;

    @Bind(R.id.subjectName)
    public TextView mSubjectName;

    @Bind(R.id.classTeacherName)
    public TextView mClassTeacherName;

    @Bind(R.id.checkbox)
    public CheckBox mCheckBox;

    @Bind(R.id.optionsIconContainer)
    public ImageView optionsIconContainer;

    public View mItemView;

    public SubjectViewHolder(View itemView)
    {
        super(itemView);
        mItemView = itemView;
        ButterKnife.bind(this, itemView);
    }
}