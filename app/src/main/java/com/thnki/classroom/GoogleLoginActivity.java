package com.thnki.classroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.thnki.classroom.model.Accounts;
import com.thnki.classroom.utils.ConnectivityUtil;
import com.thnki.classroom.utils.UserUtil;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    public static final String LOGIN_STATUS = "login_status";

    private static final String TAG = GoogleLoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GET_TOKEN = 199;
    private static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 198;


    private FirebaseAuth mAuth;
    private boolean mIsRunning;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

        TextView appName = (TextView) findViewById(R.id.title);
        appName.setTypeface(Typeface.createFromAsset(getAssets(), "Gabriola.ttf"));

        checkGooglePlayServices();
        mAuth = FirebaseAuth.getInstance();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult");
        switch (requestCode)
        {
            case REQUEST_CODE_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK)
                {
                    if (mPreference.getBoolean(LOGIN_STATUS, false))
                    {
                        launchMainActivity();
                    }
                    else
                    {
                        setupLogin();
                    }
                }
                break;
            case REQUEST_CODE_GET_TOKEN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "REQUEST_CODE_GET_TOKEN result : " + result);
                Log.d(TAG, "Status : " + result.getStatus());
                Log.d(TAG, "Data : " + data);
                if (result.isSuccess())
                {
                    firebaseAuthWithGoogle(result.getSignInAccount());
                }
                else
                {
                    loginFailed();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkGooglePlayServices()
    {
        Log.d(TAG, "checkGooglePlayServices");
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS)
        {
            onActivityResult(REQUEST_CODE_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        }
        else if (api.isUserResolvableError(code))
        {
            api.showErrorDialogFragment(this, code, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
        }
        else
        {
            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupLogin()
    {
        Log.d(TAG, "setUpLogin : " + getString(R.string.default_web_client_id));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.VISIBLE);
        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ConnectivityUtil.isConnected(GoogleLoginActivity.this))
                {
                    signIn();
                }
                else
                {
                    snack(R.string.noInternet);
                }
            }
        });
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
    }

    private void signIn()
    {
        revokeAccess();
        signOut();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GET_TOKEN);
        showProgressDialog(getString(R.string.signing_in));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        loginFailed();
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

    private void loginFailed()
    {
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
        signOut();
        revokeAccess();
    }

    private void signOut()
    {
        if (mGoogleApiClient.isConnected())
        {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>()
                    {
                        @Override
                        public void onResult(@NonNull Status status)
                        {
                            Log.d(TAG, "signOut:onResult:" + status);
                        }
                    });
        }
    }

    private void revokeAccess()
    {
        if (mGoogleApiClient.isConnected())
        {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>()
                    {
                        @Override
                        public void onResult(@NonNull Status status)
                        {
                            Log.d(TAG, "revokeAccess:onResult:" + status);
                        }
                    });
        }
    }

    public void snack(Integer resId)
    {
        Log.d("Otto", "snack : " + resId);
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), resId, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setGravity(CENTER_HORIZONTAL);
        textView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        if (Build.VERSION.SDK_INT > 16)
        {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        snackbar.show();
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount user)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + user.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(user.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful())
                        {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            snack(R.string.authenticationFailed);
                        }
                        else
                        {
                            launchMainActivity();
                            saveUserInSharedPrefs(user);
                            UserUtil.getInstance().saveUser(user);
                        }
                    }
                });
    }

    private void saveUserInSharedPrefs(GoogleSignInAccount acct)
    {
        Uri photo_url = acct.getPhotoUrl();
        mPreference.edit()
                .putString(Accounts.NAME, acct.getDisplayName())
                .putBoolean(LOGIN_STATUS, true)
                .putString(Accounts.EMAIL, acct.getEmail())
                .putString(Accounts.PHOTO_URL, photo_url != null ? photo_url.toString() : "")
                .putString(Accounts.GOOGLE_ID, acct.getId())
                .apply();
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
                    Intent intent = new Intent(GoogleLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 500);
    }
}
