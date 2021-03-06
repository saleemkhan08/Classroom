package com.thnki.classroom.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thnki.classroom.R;
import com.thnki.classroom.listeners.ImageClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesViewHolder extends RecyclerView.ViewHolder
{
    private ImageClickListener mListener;

    @Bind(R.id.singleImage)
    public ImageView singleImage;

    @Bind(R.id.dualImageContainer)
    public View dualImageContainer;

    @Bind(R.id.dualImage1)
    public ImageView dualImage1;

    @Bind(R.id.dualImage2)
    public ImageView dualImage2;

    @Bind(R.id.tripleImageContainer)
    public View tripleImageContainer;

    @Bind(R.id.tripleImage1)
    public ImageView tripleImage1;

    @Bind(R.id.tripleImage2)
    public ImageView tripleImage2;

    @Bind(R.id.tripleImage3)
    public ImageView tripleImage3;

    @Bind(R.id.quadImageContainer)
    public View quadImageContainer;

    @Bind(R.id.quadImage1)
    public ImageView quadImage1;

    @Bind(R.id.quadImage2)
    public ImageView quadImage2;

    @Bind(R.id.quadImage3)
    public ImageView quadImage3;

    @Bind(R.id.quadImage4)
    public ImageView quadImage4;

    @Bind(R.id.createrImage)
    public ImageView createrImage;

    @Bind(R.id.notesTitle)
    public TextView notesTitle;

    @Bind(R.id.createrName)
    public TextView createrName;

    @Bind(R.id.optionsIconContainer)
    public ImageView optionsIconContainer;

    @Bind(R.id.additionalImageCount)
    public TextView additionalImageCount;

    @Bind(R.id.notesDescription)
    public TextView notesDescription;

    @Bind(R.id.dateTime)
    public TextView dateTime;

    @Bind(R.id.reviewButtonsContainer)
    public View reviewButtonsContainer;

    @Bind(R.id.approveBtn)
    public View approveBtn;

    @Bind(R.id.rejectButton)
    public View rejectButton;

    public View mItemView;

    @Bind(R.id.rejectionText)
    public View rejectionText;

    public NotesViewHolder(View itemView)
    {
        super(itemView);
        mItemView = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void setClickListener(ImageClickListener listener)
    {
        mListener = listener;
    }

    @OnClick({R.id.singleImage, R.id.dualImage1, R.id.tripleImage1, R.id.quadImage1})
    public void onFirstImageClick()
    {
        mListener.onImageClick(0);
    }

    @OnClick({R.id.dualImage2, R.id.tripleImage2, R.id.quadImage2})
    public void onSecondImageClick()
    {
        mListener.onImageClick(1);
    }

    @OnClick({R.id.tripleImage3, R.id.quadImage3})
    public void onThirdImageClick()
    {
        mListener.onImageClick(2);
    }

    @OnClick(R.id.quadImage4)
    public void onFourthImageClick()
    {
        mListener.onImageClick(3);
    }

}