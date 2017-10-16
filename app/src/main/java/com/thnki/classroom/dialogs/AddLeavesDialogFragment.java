package com.thnki.classroom.dialogs;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
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
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.DateTimeUtil;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.thnki.classroom.R.id.reason;
import static com.thnki.classroom.model.Leaves.getDbKeyDate;

public class AddLeavesDialogFragment extends CustomDialogFragment implements AdapterView.OnItemSelectedListener, View.OnTouchListener
{
    public static final String TAG = "AddLeavesDialogFragment";


    @Bind(R.id.approverSpinner)
    Spinner mApproverSpinner;

    @Bind(reason)
    EditText mReason;

    @Bind(R.id.fromDate)
    EditText mFromDate;

    @Bind(R.id.toDate)
    EditText mToDate;

    @Bind(R.id.approver)
    EditText mApprover;

    public static AddLeavesDialogFragment getInstance()
    {
        return new AddLeavesDialogFragment();
    }

    public AddLeavesDialogFragment()
    {

    }

    @Override
    public void onCreateView(View parentView)
    {
        ButterKnife.bind(this, parentView);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Staff.STAFF);
        StaffFirebaseListAdapter adapter = new StaffFirebaseListAdapter(getActivity(),
                Staff.class, android.R.layout.simple_list_item_1, dbRef);
        mApproverSpinner.setAdapter(adapter);
        mApproverSpinner.setOnItemSelectedListener(this);
        mApprover.setOnTouchListener(this);
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        return R.layout.fragment_add_leaves;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setDialogTitle(R.string.applyLeave);
        setSubmitBtnTxt(R.string.submit);
        setSubmitBtnImg(R.mipmap.submit);
    }

    @Override
    public void submit(View view)
    {
        super.submit(view);
        Progress.show(R.string.submitting);
        final Leaves leave = new Leaves();
        leave.setReason(mReason.getText().toString());
        leave.setFromDate(mFromDate.getText().toString());
        leave.setToDate(mToDate.getText().toString());
        Staff staff = (Staff) mApprover.getTag();
        leave.setStatus(Leaves.STATUS_APPLIED);
        leave.setApproverId(staff.getUserId());
        leave.setRequestedLeaveKey(DateTimeUtil.getKey());
        leave.setRequesterId(NavigationDrawerUtil.mCurrentUser.getUserId());


        if (leave.validate())
        {
            DatabaseReference leaveRootRef = FirebaseDatabase.getInstance().getReference().child(Leaves.LEAVES);
            DatabaseReference myLeaveRef = leaveRootRef.child(leave.getRequesterId()).child(Leaves.MY_LEAVES);
            final DatabaseReference requestedLeaveRef = leaveRootRef.child(leave.getApproverId()).child(Leaves.REQUESTED_LEAVES);

            myLeaveRef.child(getDbKeyDate(leave.getFromDate())).setValue(leave).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {

                    requestedLeaveRef.child(leave.getRequestedLeaveKey()).setValue(leave)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    Progress.hide();
                                    ToastMsg.show(R.string.submitted);
                                    Otto.post(leave);
                                    NotificationDialogFragment.getInstance(leave).sendLeavesRelatedNotification(getActivity());
                                    dismiss();
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        mApprover.setText(((TextView) view).getText().toString());
        mApprover.setTag(view.getTag());
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
            mApprover.requestFocus();
            mApprover.setCursorVisible(false);
            closeTheKeyBoard();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mApproverSpinner.performClick();
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