package com.thnki.classroom.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.thnki.classroom.R;

public class ImageUtil
{
    public static void loadCircularImg(final Context context, String url, final ImageView imageView)
    {
        Glide.with(context).load(url).asBitmap().placeholder(R.mipmap.user_icon_accent)
                .centerCrop().into(new BitmapImageViewTarget(imageView)
        {
            @Override
            protected void setResource(Bitmap resource)
            {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadCircularImg(String url, final ImageView imageView)
    {
        Glide.with(imageView.getContext()).load(url).asBitmap().placeholder(R.mipmap.user_icon_accent)
                .centerCrop().into(new BitmapImageViewTarget(imageView)
        {
            @Override
            protected void setResource(Bitmap resource)
            {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadImg(final Context context, Uri url, final ImageView imageView)
    {
        Glide.with(context).load(url)
                .asBitmap().placeholder(R.mipmap.notes)
                .centerCrop().into(imageView);
    }

    public static void loadImg(final Context context, String url, final ImageView imageView)
    {
        Glide.with(context).load(url)
                .asBitmap().placeholder(R.mipmap.notes)
                .centerCrop().into(imageView);
    }

    public static void loadSquareImg(String url, final ImageView imageView)
    {
        Glide.with(imageView.getContext()).load(url)
                .asBitmap().placeholder(R.mipmap.user_icon_accent)
                .centerCrop().into(imageView);
    }


}
