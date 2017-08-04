package com.thnki.classroom.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.thnki.classroom.LoginActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.fragments.StudentListFragment;

import static android.content.ContentValues.TAG;

public class NavigationDrawerUtil implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String STUDENTS_LIST_FRAGMENT = "studentsListFragment";
    private AppCompatActivity mActivity;
    private DrawerLayout mDrawer;
    private SharedPreferences mSharedPrefs;
    private ProgressDialog mProgressDialog;
    private FragmentManager mFragmentManager;
    private String mCurrentFragment = "";

    public NavigationDrawerUtil(AppCompatActivity activity)
    {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (mSharedPrefs.getString(LoginActivity.LOGIN_USER_ID, "c").charAt(0))
        {
            case 'a':
                navigationView.inflateMenu(R.menu.admin_drawer);
                loadStudentListFragment(false);
                navigationView.setCheckedItem(R.id.admin_students);
                break;
            case 's':
                navigationView.inflateMenu(R.menu.staff_drawer);
                break;
            default:
                navigationView.inflateMenu(R.menu.student_drawer);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.admin_students:
                loadStudentListFragment(true);
                break;
            case R.id.nav_notifications:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_logout:
                logout();
        }
        closeDrawer();
        return true;
    }

    private void loadStudentListFragment(boolean addToBackStack)
    {
        String tag = STUDENTS_LIST_FRAGMENT;
        Fragment fragment = getFragment(tag);
        if (fragment == null)
        {
            fragment = StudentListFragment.getInstance();
        }
        loadFragment(fragment, tag, addToBackStack);
    }

    private Fragment getFragment(String tag)
    {
        return mFragmentManager.findFragmentByTag(tag);
    }

    private void loadFragment(Fragment fragment, String tag, boolean addToBackStack)
    {
        if (!mCurrentFragment.equals(tag))
        {
            boolean isPopped = mFragmentManager.popBackStackImmediate(tag, 0);
            if (!isPopped)
            {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.replace(R.id.content_main, fragment, tag);
                if (addToBackStack)
                {
                    transaction.addToBackStack(tag);
                }
                transaction.commit();
            }
            mCurrentFragment = tag;
        }
    }

    public boolean isDrawerOpen()
    {
        return mDrawer.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer()
    {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    private void showProgressDialog(String msg)
    {
        Log.d(TAG, "showProgressDialog");

        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        Log.d(TAG, "hideProgressDialog");
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }

    private void logout()
    {
        showProgressDialog("Logging out ...");
        mSharedPrefs.edit().putBoolean(LoginActivity.LOGIN_STATUS, false).apply();

        Log.d(TAG, "Launching MainActivity : through Handler");
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                hideProgressDialog();
                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
            }
        }, 1000);
    }
}
