package com.thnki.classroom.dialogs;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.StaffFirebaseListAdapter;
import com.thnki.classroom.model.NotesClassifier;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddOrEditNotesDialogFragment extends CustomDialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnTouchListener
{
    public static final String TAG = "AddOrEditNotesDialogFragment";

    @Bind(R.id.notesApproverSpinner)
    Spinner mNotesApproverSpinner;

    @Bind(R.id.notesReviewer)
    EditText mNotesApprover;

    @Bind(R.id.imagesRecyclerView)
    RecyclerView mImagesRecyclerView;

    NotesClassifier mCurrentNotesClassifier;
    private StaffFirebaseListAdapter mStaffAdapter;
    private Handler mHandler;


    public static AddOrEditNotesDialogFragment getInstance(NotesClassifier resultClassifier)
    {
        AddOrEditNotesDialogFragment fragment = new AddOrEditNotesDialogFragment();
        fragment.mCurrentNotesClassifier = resultClassifier;
        fragment.mHandler = new Handler();
        return fragment;
    }

    public AddOrEditNotesDialogFragment()
    {

    }

    @Override
    public void onCreateView(View parentView)
    {
        ButterKnife.bind(this, parentView);
        setDialogFabImg(R.mipmap.ic_photo_camera_white_48dp);
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        return R.layout.fragment_add_notes;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Staff.STAFF);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            mNotesApproverSpinner.setSelection(findPosition());
                        }
                        catch (Exception e)
                        {
                            mNotesApproverSpinner.setSelection(0);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        mStaffAdapter = new StaffFirebaseListAdapter(getActivity(),
                Staff.class, android.R.layout.simple_list_item_1, dbRef);
        mNotesApproverSpinner.setAdapter(mStaffAdapter);
        mNotesApproverSpinner.setOnItemSelectedListener(this);
        mNotesApprover.setOnTouchListener(this);
        setDialogTitle(R.string.addNotes);
        setSubTitle(mCurrentNotesClassifier.getSubjectName());

        if (mCurrentNotesClassifier.isEdit())
        {
        }
        setSubmitBtnTxt(R.string.add);
        setSubmitBtnImg(R.mipmap.plus);
    }

    private int findPosition()
    {
        for (int i = 0; i < mStaffAdapter.getCount(); i++)
        {
            if ((mStaffAdapter.getItem(i).getUserId().equals(mCurrentNotesClassifier.getTeacherId())))
            {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void submit(View view)
    {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        mNotesApprover.setText(((TextView) view).getText().toString());
        mNotesApprover.setTag(view.getTag());
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
            mNotesApprover.requestFocus();
            mNotesApprover.setCursorVisible(false);
            closeTheKeyBoard();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mNotesApproverSpinner.performClick();
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