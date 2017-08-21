package com.thnki.classroom.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.StaffFirebaseListAdapter;
import com.thnki.classroom.model.Classes;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditClassDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnTouchListener
{
    public static final String TAG = "AddOrEditClassDialogFragment";

    @Bind(R.id.classCode)
    EditText mClassCode;

    @Bind(R.id.className)
    EditText mClassName;

    @Bind(R.id.classTeacherSpinner)
    Spinner mClassTeacherSpinner;

    @Bind(R.id.classTeacher)
    EditText mClassTeacher;

    @Bind(R.id.dialogTitle)
    TextView mDialogTitle;

    Classes mCurrentClass;

    public static AddOrEditClassDialogFragment getInstance(Classes classes)
    {
        AddOrEditClassDialogFragment fragment = new AddOrEditClassDialogFragment();
        fragment.mCurrentClass = classes;
        return fragment;
    }

    public AddOrEditClassDialogFragment()
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

        View parentView = inflater.inflate(R.layout.fragment_add_class, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        return parentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Staff.STAFF);
        StaffFirebaseListAdapter adapter = new StaffFirebaseListAdapter(getActivity(),
                Staff.class, android.R.layout.simple_list_item_1, dbRef);
        mClassTeacherSpinner.setAdapter(adapter);
        mClassTeacherSpinner.setOnItemSelectedListener(this);
        mClassTeacher.setOnTouchListener(this);
        if (mCurrentClass != null)
        {
            mClassTeacher.setText(mCurrentClass.getClassTeacherName());
            mClassTeacher.setTag(mCurrentClass.getClassTeacherId());
            mClassCode.setText(mCurrentClass.getCode());
            mClassCode.setEnabled(false);
            mClassName.setText(mCurrentClass.getName());
            mDialogTitle.setText(R.string.editClass);
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
    }

    @OnClick(R.id.closeDialog)
    public void close()
    {
        dismiss();
    }

    @OnClick(R.id.saveClassButton)
    public void save()
    {
        String className = mClassName.getText().toString();
        String classCode = mClassCode.getText().toString();
        String classTeacherId = (String) mClassTeacher.getTag();
        if (TextUtils.isEmpty(className))
        {
            ToastMsg.show(R.string.please_enter_class_name);
        }
        else if (TextUtils.isEmpty(classCode))
        {
            ToastMsg.show(R.string.please_enter_class_code);
        }
        else
        {
            if (mCurrentClass == null)
            {
                mCurrentClass = new Classes();
            }
            mCurrentClass.setCode(classCode);
            mCurrentClass.setName(className);
            mCurrentClass.setClassTeacherId(classTeacherId);
            mCurrentClass.setClassTeacherName(mClassTeacher.getText().toString());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Classes.CLASSES);
            Progress.show(R.string.saving);
            reference.child(classCode).setValue(mCurrentClass).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Progress.hide();
                    if (task.isSuccessful())
                    {
                        ToastMsg.show(R.string.saved);
                        dismiss();
                    }
                    else
                    {
                        ToastMsg.show(R.string.please_try_again);
                    }
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        mClassTeacher.setText(((TextView) view).getText().toString());
        mClassTeacher.setTag(view.getTag());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        ToastMsg.show(R.string.pleaseSelectClassTeacher);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            mClassTeacher.requestFocus();
            mClassTeacher.setCursorVisible(false);
            closeTheKeyBoard();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mClassTeacherSpinner.performClick();
                }
            }, 100);
        }
        return true;
    }

    private void closeTheKeyBoard()
    {
        View view = getView();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}