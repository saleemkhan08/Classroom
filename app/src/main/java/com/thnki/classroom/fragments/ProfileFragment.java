package com.thnki.classroom.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.dialogs.ChangePasswordDialogFragment;
import com.thnki.classroom.dialogs.EditNameDialogFragment;
import com.thnki.classroom.dialogs.EditUserDetailsDialogFragment;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.model.User;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.thnki.classroom.utils.NavigationDrawerUtil.PROFILE_FRAGMENT;


public class ProfileFragment extends Fragment implements EventsListener, ValueEventListener
{
    private static final int PICK_PROFILE_IMAGE = 55;

    @Bind(R.id.profilePicture)
    ImageView mProfileImgView;

    @Bind(R.id.fullName)
    TextView mUserFullName;

    @Bind(R.id.designation)
    TextView mDesignation;

    @Bind(R.id.dobValue)
    TextView dobValue;

    @Bind(R.id.emailVlaue)
    TextView emailValue;

    @Bind(R.id.phoneNoVlaue)
    TextView phoneNoValue;

    @Bind(R.id.addressValue)
    TextView addressValue;

    private User mCurrentUser;
    private DatabaseReference mUserDbRef;


    public static ProfileFragment getInstance(User user)
    {
        ProfileFragment fragment = new ProfileFragment();
        fragment.mCurrentUser = user;
        return fragment;
    }

    public ProfileFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, parentView);
        if (mCurrentUser instanceof Students)
        {
            Students student = (Students) mCurrentUser;
            mUserDbRef = FirebaseDatabase.getInstance().getReference()
                    .child(User.STUDENTS).child(student.classId())
                    .child(student.getUserId());
        }
        else
        {
            mUserDbRef = FirebaseDatabase.getInstance().getReference()
                    .child(User.STAFF).child(mCurrentUser.getUserId());
        }

        mUserDbRef.addValueEventListener(this);
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).setToolBarTitle(getString(R.string.profile));
            ((MainActivity) activity).updateEventsListener(this);
            Otto.post(ActionBarUtil.SHOW_PROFILE_MENU);
        }
        Otto.register(this);
        return parentView;
    }

    private void updateProfileInfo(Students student)
    {
        mCurrentUser = student;
        mDesignation.setText(student.getClassName());
        updateCommonInfo(student);
    }

    private void updateCommonInfo(User user)
    {
        mUserFullName.setText(user.getFullName());
        ImageUtil.loadCircularImg(getActivity(), user.getPhotoUrl(), mProfileImgView);
        phoneNoValue.setText(user.getPhone());
        addressValue.setText(user.getAddress());
        emailValue.setText(user.getEmail());
        dobValue.setText(user.getDob());
    }

    private void updateProfileInfo(Staff staff)
    {
        mCurrentUser = staff;
        mDesignation.setText(staff.getDesignation());
        updateCommonInfo(staff);
    }

    @Override
    public boolean onBackPressed()
    {
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.nav_settings;
    }

    @Override
    public String getTagName()
    {
        return PROFILE_FRAGMENT;
    }

    @OnClick(R.id.editName)
    public void editName()
    {
        EditNameDialogFragment.getInstance(mUserDbRef, mCurrentUser.getFullName()).show(getFragmentManager(), EditNameDialogFragment.TAG);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        if (dataSnapshot.hasChild(Staff.IS_ADMIN))
        {
            updateProfileInfo(dataSnapshot.getValue(Staff.class));
        }
        else
        {
            updateProfileInfo(dataSnapshot.getValue(Students.class));
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    @OnClick(R.id.uploadProfileImg)
    public void addImages(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PROFILE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if (requestCode == PICK_PROFILE_IMAGE && resultCode == RESULT_OK && null != data)
            {
                if (data.getData() != null)
                {
                    Uri mImageUri = data.getData();
                    StorageReference rootRef = FirebaseStorage.getInstance().getReference();
                    StorageReference userImageRef;
                    if (mCurrentUser instanceof Staff)
                    {
                        Log.d("UploadIssue", "Staff");
                        Progress.show(R.string.uploading);
                        userImageRef = rootRef.child(User.STAFF).child(mCurrentUser.getUserId() + ".jpg");
                    }
                    else if (mCurrentUser instanceof Students)
                    {
                        Log.d("UploadIssue", "Students");
                        Progress.show(R.string.uploading);
                        Students student = (Students) mCurrentUser;
                        userImageRef = rootRef.child(User.STUDENTS).child(student.classId())
                                .child(student.getUserId() + ".jpg");
                    }
                    else
                    {
                        ToastMsg.show(R.string.something_went_wrong);
                        return;
                    }
                    Log.d("UploadIssue", "userImageRef : " + userImageRef);
                    Log.d("UploadIssue", "mImageUri : " + mImageUri);

                    userImageRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Log.d("UploadIssue", "onSuccess : " + taskSnapshot.getDownloadUrl());

                            mCurrentUser.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
                            mUserDbRef.setValue(mCurrentUser);
                            Progress.hide();
                            ToastMsg.show(R.string.uploaded);
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d("UploadIssue", "onFailure");
                            Progress.hide();
                            ToastMsg.show(R.string.please_try_again);
                        }
                    });
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fabContainer)
    public void editProfileInfo()
    {
        EditUserDetailsDialogFragment.getInstance(mUserDbRef, mCurrentUser)
                .show(getFragmentManager(), EditUserDetailsDialogFragment.TAG);
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        switch (itemId)
        {
            case R.id.changePassword:
                showPasswordChangeDialog();
                break;
        }
    }

    private void showPasswordChangeDialog()
    {
        ChangePasswordDialogFragment.getInstance(mUserDbRef, mCurrentUser.getPassword())
                .show(getFragmentManager(), ChangePasswordDialogFragment.TAG);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Otto.unregister(this);
    }
}
