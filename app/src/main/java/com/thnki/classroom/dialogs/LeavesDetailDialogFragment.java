package com.thnki.classroom.dialogs;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.utils.NavigationDrawerUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.thnki.classroom.model.Leaves.MY_LEAVES;
import static com.thnki.classroom.model.Leaves.REQUESTED_LEAVES;

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

    @Bind(R.id.status)
    TextView mStatus;

    String mUserId;
    String mDate;
    private DatabaseReference mMyLeavesDbRef;
    private Leaves mLeave;
    private DatabaseReference mLeavesRootDbRef;

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
        mLeavesRootDbRef = FirebaseDatabase.getInstance().getReference().child(Leaves.LEAVES);
        mMyLeavesDbRef = mLeavesRootDbRef.child(mUserId).child(MY_LEAVES).child(mDate);
        mMyLeavesDbRef.addListenerForSingleValueEvent(this);
        if (mUserId.equals(NavigationDrawerUtil.mCurrentUser.getUserId()))
        {
            setSubmitBtnTxt(R.string.delete);
            setSubmitBtnImg(R.mipmap.cancel_all_button);
        }
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
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        mLeave = dataSnapshot.getValue(Leaves.class);
        if (mLeave != null)
        {
            mReason.setText(mLeave.getReason());
            mFromDate.setText(mLeave.getFromDate());
            mToDate.setText(mLeave.getToDate());
            mApprover.setText(mLeave.getApproverId());
            mStatus.setText(getString(mLeave.statusText()));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void submit(View view)
    {
        super.submit(view);
        Progress.show(R.string.deleting);
        mLeave.setStatus(Leaves.STATUS_CANCELLED);
        NotificationDialogFragment.getInstance(mLeave).sendLeavesRelatedNotification(getActivity());

        DatabaseReference requestedLeavesDbRef = mLeavesRootDbRef.child(mLeave.getApproverId()).child(REQUESTED_LEAVES)
                .child(mLeave.requestedLeaveKey());
        requestedLeavesDbRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    mMyLeavesDbRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Progress.hide();
                                dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}