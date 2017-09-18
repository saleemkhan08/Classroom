package com.thnki.classroom.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thnki.classroom.LoginActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.StaffFirebaseListAdapter;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.Otto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        Leaves leave = new Leaves();

        leave.setReason(mReason.getText().toString());
        leave.setFromDate(mFromDate.getText().toString());
        leave.setToDate(mToDate.getText().toString());
        leave.setApprover(mApprover.getText().toString());

        if (leave.validate())
        {
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String userId = preference.getString(LoginActivity.LOGIN_USER_ID, "");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Leaves.LEAVES)
                    .child(userId);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            try
            {
                calendar.setTime(dateFormat.parse(leave.getFromDate()));
                for (int i = 0; i <= leave.numDaysBetweenDates(); i++)
                {
                    String date = getDbKeyDate(calendar);
                    reference.child(date).setValue(leave);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            ToastMsg.show(R.string.submitted);
            Otto.post(leave);
            dismiss();
        }
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