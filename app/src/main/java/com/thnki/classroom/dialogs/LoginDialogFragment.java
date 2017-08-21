package com.thnki.classroom.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thnki.classroom.R;
import com.thnki.classroom.listeners.OnDismissListener;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.ConnectivityUtil;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginDialogFragment extends DialogFragment
{
    public static final String TAG = "LoginDialogFragment";
    public static final String FIREBASE_USER_ID = "firebaseUserId";
    private static final int MIN_LENGTH_OF_USER_ID = 3;
    private static final int MIN_LENGTH_OF_PASSWORD = 6;

    @Bind(R.id.userId)
    EditText mUserIdEditText;

    @Bind(R.id.password)
    EditText mPasswordEditText;

    private FirebaseAuth mAuth;
    public String mUserId;
    private OnDismissListener mOnDismissListener;

    public static LoginDialogFragment getInstance()
    {
        return new LoginDialogFragment();
    }

    public LoginDialogFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Window window = getDialog().getWindow();
        if (window != null)
        {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        mAuth = FirebaseAuth.getInstance();
        View parentView = inflater.inflate(R.layout.fragment_loing_credentials, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.login_bg);
        return parentView;
    }

    @OnClick(R.id.loginButton)
    public void onClick(View button)
    {
        closeTheKeyBoard();
        if (ConnectivityUtil.isConnected(getActivity()))
        {
            login();
        }
        else
        {
            ToastMsg.show(R.string.noInternet);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        Otto.unregister(this);
        if (mOnDismissListener != null)
        {
            mOnDismissListener.onDismiss();
        }
    }

    private void login()
    {
        mUserId = mUserIdEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        if (mUserId.length() < MIN_LENGTH_OF_USER_ID)
        {
            ToastMsg.show(R.string.validUserIdErrMsg);
        }
        else if (password.length() < MIN_LENGTH_OF_PASSWORD)
        {
            ToastMsg.show(R.string.validPasswordErrMsg);
        }
        else
        {
            Progress.show(R.string.signing_in);
            mAuth.signInWithEmailAndPassword(mUserId + "@clsroom.com", password)
                    .addOnCompleteListener((OnCompleteListener<AuthResult>) getActivity());
            dismiss();
        }
    }

    private void closeTheKeyBoard()
    {
        View view = getActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener)
    {
        mOnDismissListener = onDismissListener;
    }
}