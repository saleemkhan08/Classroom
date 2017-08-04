package com.thnki.classroom.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.classroom.R;


public class ProductsFragment extends Fragment
{
    public ProductsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    public static ProductsFragment getInstance()
    {
        return new ProductsFragment();
    }
}
