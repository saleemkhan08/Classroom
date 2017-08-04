package com.thnki.classroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thnki.classroom.adapters.LoginSectionsPagerAdapter;
import com.thnki.classroom.utils.ConnectivityUtil;

public class LoginActivity extends AppCompatActivity implements OnCompleteListener<AuthResult>
{
    public static final String LOGIN_STATUS = "loginStatus";
    public static final String FIREBASE_USER_ID = "firebaseUserId";
    public static final String LOGIN_USER_ID = "loginUserId";
    private static final String TAG = "LoginActivity";

    private EditText mUserIdEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private boolean mIsRunning;
    private SharedPreferences mSharedPreferences;
    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        LoginSectionsPagerAdapter sectionsPagerAdapter = new LoginSectionsPagerAdapter(getSupportFragmentManager());

        TextView appName = (TextView) findViewById(R.id.title);
        appName.setTypeface(Typeface.createFromAsset(getAssets(), "Gabriola.ttf"));

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        if (mSharedPreferences.getBoolean(LOGIN_STATUS, false))
        {
            launchMainActivity();
        }
        else
        {
            mAuth = FirebaseAuth.getInstance();
            View credentialsContainer = findViewById(R.id.loginContainer);
            credentialsContainer.setVisibility(View.VISIBLE);
            mUserIdEditText = (EditText) findViewById(R.id.userId);
            mPasswordEditText = (EditText) findViewById(R.id.password);
            final View loginBtn = findViewById(R.id.fab);
            loginBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View button)
                {
                    closeTheKeyBoard();
                    if (ConnectivityUtil.isConnected(LoginActivity.this))
                    {
                        login();
                    }
                    else
                    {
                        snack("No Internet ...");
                    }
                }
            });
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mIsRunning = true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        hideProgressDialog();
        mIsRunning = false;
    }

    private void closeTheKeyBoard()
    {
        View view = getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void login()
    {
        mUserId = mUserIdEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        if (mUserId.length() < 5)
        {
            snack("Please Enter a valid user ID");
        }
        else if (password.length() < 6)
        {
            snack("Please Enter a valid password");
        }
        else
        {
            showProgressDialog("Signing in ...");
            mAuth.signInWithEmailAndPassword(mUserId + "@clsroom.com", password)
                    .addOnCompleteListener(this);
        }
    }

    private void snack(String msg)
    {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        layout.setBackgroundResource(R.color.colorPrimary);
        snackbar.show();
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task)
    {
        if (task.isSuccessful())
        {
            if (mAuth.getCurrentUser() != null)
            {
                mSharedPreferences.edit()
                        .putBoolean(LOGIN_STATUS, true)
                        .putString(FIREBASE_USER_ID, mAuth.getCurrentUser().getUid())
                        .putString(LOGIN_USER_ID, mUserId)
                        .apply();
                launchMainActivity();
                return;
            }
        }
        loginFailed();
    }

    private void loginFailed()
    {
        mSharedPreferences.edit()
                .putBoolean(LOGIN_STATUS, false)
                .apply();
        hideProgressDialog();
        snack("Login Failed!");
    }

    private void showProgressDialog(String msg)
    {
        Log.d(TAG, "showProgressDialog");

        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(this);
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

    private void launchMainActivity()
    {
        Log.d(TAG, "Launching MainActivity : through Handler");
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mIsRunning)
                {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 1000);
    }
}
