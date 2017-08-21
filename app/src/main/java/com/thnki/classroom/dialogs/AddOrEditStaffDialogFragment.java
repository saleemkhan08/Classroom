package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrEditStaffDialogFragment extends DialogFragment
{
    public static final String TAG = "AddOrEditStaffDialogFragment";

    @Bind(R.id.staffName)
    EditText mStaffName;

    @Bind(R.id.staffCode)
    EditText mStaffUserId;

    @Bind(R.id.designation)
    EditText mDesignation;

    @Bind(R.id.qualification)
    EditText mQualification;

    @Bind(R.id.isAdmin)
    Switch mIsAdmin;

    @Bind(R.id.dialogTitle)
    TextView mDialogTitle;

    Staff mCurrentStaff;

    public static AddOrEditStaffDialogFragment getInstance(Staff classes)
    {
        AddOrEditStaffDialogFragment fragment = new AddOrEditStaffDialogFragment();
        fragment.mCurrentStaff = classes;
        return fragment;
    }

    public AddOrEditStaffDialogFragment()
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

        View parentView = inflater.inflate(R.layout.fragment_add_staffs, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        return parentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mCurrentStaff != null)
        {
            mDialogTitle.setText(R.string.editStaff);
            mDesignation.setText(mCurrentStaff.getDesignation());
            mIsAdmin.setChecked(mCurrentStaff.getIsAdmin());

            mStaffUserId.setText(mCurrentStaff.getUserId());
            mStaffUserId.setEnabled(false);

            mStaffName.setText(mCurrentStaff.getFullName());
            mQualification.setText(mCurrentStaff.getQualification());
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
        String staffName = mStaffName.getText().toString();
        String staffUserId = mStaffUserId.getText().toString();
        String qualification = mQualification.getText().toString();
        String designation = mDesignation.getText().toString();
        boolean isAdmin = mIsAdmin.isChecked();

        if (TextUtils.isEmpty(staffName))
        {
            ToastMsg.show(R.string.please_enter_staff_name);
        }
        else if (TextUtils.isEmpty(staffUserId))
        {
            ToastMsg.show(R.string.please_enter_staff_id);
        }
        else if (TextUtils.isEmpty(qualification))
        {
            ToastMsg.show(R.string.please_enter_staff_qualification);
        }
        else if (TextUtils.isEmpty(designation))
        {
            ToastMsg.show(R.string.please_enter_staff_designation);
        }
        else
        {
            if (mCurrentStaff == null)
            {
                mCurrentStaff = new Staff();
            }
            mCurrentStaff.setAdmin(isAdmin);
            mCurrentStaff.setDesignation(designation);
            mCurrentStaff.setFullName(staffName);
            mCurrentStaff.setUserId(staffUserId);
            mCurrentStaff.setQualification(qualification);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Staff.STAFF);
            Progress.show(R.string.saving);
            reference.child(staffUserId).setValue(mCurrentStaff).addOnCompleteListener(new OnCompleteListener<Void>()
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
}