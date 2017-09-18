package com.thnki.classroom.dialogs;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Leaves;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LeavesDetailDialogFragment extends CustomDialogFragment implements ValueEventListener
{
    public static final String TAG = "LeavesDetailDialogFragment";


    @Bind(R.id.reason)
    TextView mReason;

    @Bind(R.id.fromDate)
    TextView mFromDate;

    @Bind(R.id.toDate)
    TextView mToDate;

    @Bind(R.id.approver)
    TextView mApprover;

    String mUserId;
    String mDate;

    public static LeavesDetailDialogFragment getInstance(String userId, String date)
    {
        LeavesDetailDialogFragment fragment = new LeavesDetailDialogFragment();
        fragment.mUserId = userId;
        fragment.mDate = date;
        return fragment;
    }

    public LeavesDetailDialogFragment()
    {

    }

    @Override
    public void onCreateView(View parentView)
    {
        ButterKnife.bind(this, parentView);
        FirebaseDatabase.getInstance().getReference().child(Leaves.LEAVES)
                .child(mUserId).child(mDate).addValueEventListener(this);
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        return R.layout.fragment_leave_details;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setDialogTitle(R.string.leaveDetails);
        hideSubmitBtn();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        Leaves leave = dataSnapshot.getValue(Leaves.class);
        mReason.setText(leave.getReason());
        mFromDate.setText(leave.getFromDate());
        mToDate.setText(leave.getToDate());
        mApprover.setText(leave.getApprover());
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }
}