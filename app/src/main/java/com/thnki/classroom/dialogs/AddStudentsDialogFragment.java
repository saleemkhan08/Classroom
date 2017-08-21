package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Classes;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.Otto;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddStudentsDialogFragment extends DialogFragment
{
    public static final String TAG = "AddStudentsDF";

    @Bind(R.id.studentCount)
    EditText mStudentCount;

    DatabaseReference mClassesDbRef;
    private DatabaseReference mRootRef;
    Classes mCurrentClass;
    private DatabaseReference mStudentDbRef;

    public static AddStudentsDialogFragment getInstance(Classes currentClass)
    {
        AddStudentsDialogFragment fragment = new AddStudentsDialogFragment();
        fragment.mCurrentClass = currentClass;
        return fragment;
    }

    public AddStudentsDialogFragment()
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

        View parentView = inflater.inflate(R.layout.fragment_add_student, container, false);
        ButterKnife.bind(this, parentView);
        Otto.register(this);
        return parentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mClassesDbRef = mRootRef.child(Classes.CLASSES);
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
        String studentCountText = mStudentCount.getText().toString();
        if (TextUtils.isEmpty(studentCountText))
        {
            ToastMsg.show(R.string.please_enter_no_of_students);
        }
        else
        {
            final int studentCount = Integer.parseInt(studentCountText);
            mStudentDbRef = mRootRef.child(Students.STUDENTS).child(mCurrentClass.getCode());
            Progress.show(R.string.saving);
            mStudentDbRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Progress.hide();
                    ArrayList<String> keyList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        keyList.add(snapshot.getKey());
                    }
                    addStudent(keyList, studentCount);
                    mStudentDbRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Progress.hide();
                }
            });
        }
    }

    private void addStudent(ArrayList<String> codes, int studentCount)
    {
        Progress.show(R.string.adding);
        for (int i = 1, j = 1; i <= studentCount; i++, j++)
        {
            String studentCode = mCurrentClass.getCode() + getStudentCountStr(j);
            while (codes.contains(studentCode))
            {
                j++;
                studentCode = mCurrentClass.getCode() + getStudentCountStr(j);
            }

            Students student = new Students();
            student.setUserId(studentCode);
            mStudentDbRef.child(studentCode).setValue(student);
        }

        mStudentDbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Progress.hide();
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                mCurrentClass.setStudentCount(childrenCount);
                mClassesDbRef.child(mCurrentClass.getCode()).setValue(mCurrentClass);
                mStudentDbRef.removeEventListener(this);
                ToastMsg.show(R.string.added);
                dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Progress.hide();
                ToastMsg.show(R.string.couldntAddStudents);
            }
        });
    }

    private String getStudentCountStr(int currentStudentCount)
    {
        if (currentStudentCount < 10)
        {
            return "00" + currentStudentCount;
        }
        else if (currentStudentCount < 100)
        {
            return "0" + currentStudentCount;
        }
        else
        {
            return "" + currentStudentCount;
        }
    }
}