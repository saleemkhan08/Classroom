package com.thnki.classroom.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.LoginActivity;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.dialogs.AddLeavesDialogFragment;
import com.thnki.classroom.dialogs.LeavesDetailDialogFragment;
import com.thnki.classroom.dialogs.MonthYearPickerDialog;
import com.thnki.classroom.dialogs.SelectStaffDialogFragment;
import com.thnki.classroom.dialogs.SelectStudentDialogFragment;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.LeavesDecorator;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thnki.classroom.LoginActivity.LOGIN_USER_ID;
import static com.thnki.classroom.R.string.leaves;

public class LeavesFragment extends Fragment implements EventsListener, ValueEventListener, OnDateSelectedListener
{
    public static final String TAG = "LeavesFragment";

    @Bind(R.id.leavesList)
    MaterialCalendarView mLeavesCalender;

    @Bind(R.id.profileName)
    TextView mProfileName;

    @Bind(R.id.profileId)
    TextView mProfileId;

    @Bind(R.id.profileImg)
    ImageView mProfileImg;

    @Bind(R.id.leavesProgress)
    View mProgress;

    private DatabaseReference mLeavesRef;
    private Query mLeavesQuery;
    private DatabaseReference mLeavesRootRef;
    private String mDateStartText;
    private String mDateEndText;

    @Bind(R.id.selectMonth)
    Button mMonthButton;
    private ArrayList<CalendarDay> mLeavesList;
    private DatabaseReference mRootRef;
    private String mCurrentUserId;

    public LeavesFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_leaves, container, false);
        ButterKnife.bind(this, parentView);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCurrentUserId = preferences.getString(LOGIN_USER_ID, "");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mLeavesRootRef = FirebaseDatabase.getInstance().getReference()
                .child(Leaves.LEAVES);

        mLeavesCalender.setOnDateChangedListener(this);
        mLeavesCalender.setPagingEnabled(false);
        mLeavesCalender.setAllowClickDaysOutsideCurrentMonth(false);


        Calendar calendar = Calendar.getInstance();
        setStartEndDateText(calendar);
        setMonthButtonTxt(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        showCurrentUserLeaves();

        return parentView;
    }

    private void setMonthButtonTxt(int month, int year)
    {
        mMonthButton.setText(MonthYearPickerDialog.MONTH_ARRAY[month] + "-" + year);
    }

    private void setStartEndDateText(Calendar calendar)
    {
        mDateStartText = Leaves.getDbKeyStartDate(calendar);
        mDateEndText = Leaves.getDbKeyEndDate(calendar);
    }

    private void showCurrentUserLeaves()
    {
        switch (mCurrentUserId.charAt(0))
        {
            case 'a':
            case 's':
                mRootRef.child(Staff.STAFF).child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Staff staff = dataSnapshot.getValue(Staff.class);
                        showUserDetails(staff.getPhotoUrl(), staff.getUserId(), staff.getFullName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
                break;
            default:
                mRootRef.child(Students.STUDENTS).child(mCurrentUserId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Students students = dataSnapshot.getValue(Students.class);
                        showUserDetails(students.getPhotoUrl(), students.getUserId(), students.getFullName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
                break;
        }
    }

    private void showUserDetails(String url, String uid, String name)
    {
        ImageUtil.loadCircularImg(getActivity(), url, mProfileImg);
        mProfileId.setText(uid);
        mProfileName.setText(name);

        mLeavesRef = mLeavesRootRef.child(uid);
        showLeaves();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Otto.register(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).setToolBarTitle(getString(leaves));
            ((MainActivity) activity).updateEventsListener(this);
            Otto.post(ActionBarUtil.SHOW_ADMIN_LEAVES_MENU);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Otto.unregister(this);
    }

    @Override
    public boolean onBackPressed()
    {
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.admin_leaves;
    }

    @Override
    public String getTagName()
    {
        return NavigationDrawerUtil.LEAVES_LIST_FRAGMENT;
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        switch (itemId)
        {
            case R.id.myLeaves:
                showCurrentUserLeaves();
                break;
            case R.id.requestedLeaves:
                break;
            case R.id.staffLeaves:
                SelectStaffDialogFragment.getInstance()
                        .show(manager, SelectStaffDialogFragment.TAG);
                break;
            case R.id.studentLeaves:
                SelectStudentDialogFragment.getInstance()
                        .show(manager, SelectStudentDialogFragment.TAG);
                break;
        }
    }

    private void showLeaves()
    {
        mLeavesQuery = mLeavesRef.orderByKey()
                .startAt(mDateStartText).endAt(mDateEndText);
        mLeavesQuery.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        ToastMsg.show(dataSnapshot.getChildrenCount() + "");
        mLeavesCalender.removeDecorators();
        Calendar calendar = Calendar.getInstance();
        mLeavesCalender.addDecorator(new LeavesDecorator(dataSnapshot, getActivity()));
        if (mLeavesList == null)
        {
            mLeavesList = new ArrayList<>();
        }
        else
        {
            mLeavesList.clear();
        }
        for (DataSnapshot snapshot : dataSnapshot.getChildren())
        {
            try
            {
                String leaveDate = snapshot.getKey();
                SimpleDateFormat format = new SimpleDateFormat(Leaves.DB_DATE_FORMAT, Locale.ENGLISH);
                calendar.setTime(format.parse(leaveDate));
                mLeavesList.add(CalendarDay.from(calendar));
            }
            catch (ParseException e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
        mLeavesQuery.removeEventListener(this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
        mLeavesQuery.removeEventListener(this);
    }

    @OnClick(R.id.addLeaves)
    public void addLeaves(View view)
    {
        AddLeavesDialogFragment.getInstance().show(getActivity()
                .getSupportFragmentManager(), AddLeavesDialogFragment.TAG);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
    {
        if (mLeavesList != null && mLeavesList.contains(date))
        {
            ToastMsg.show(date.getDay() + "/" + date.getMonth() + "/" + date.getYear());
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String userId = preference.getString(LoginActivity.LOGIN_USER_ID, "");
            LeavesDetailDialogFragment.getInstance(userId, Leaves.getDbKeyDate(date))
                    .show(getFragmentManager(), LeavesDetailDialogFragment.TAG);
        }
    }

    @OnClick(R.id.selectMonth)
    public void selectMonth()
    {
        MonthYearPickerDialog pd = MonthYearPickerDialog.getInstance(mLeavesCalender.getCurrentDate());
        pd.setListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int i2)
            {
                mLeavesCalender.setVisibility(View.INVISIBLE);
                final Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, 1);
                mLeavesCalender.setCurrentDate(calendar);
                setMonthButtonTxt(month, year);
                mProgress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mDateStartText = Leaves.getDbKeyStartDate(calendar);
                        mDateEndText = Leaves.getDbKeyEndDate(calendar);
                        showLeaves();
                        mLeavesCalender.setVisibility(View.VISIBLE);
                        mProgress.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });
        pd.show(getFragmentManager(), "MonthYearPickerDialog");
    }

    @Subscribe
    public void showStaffLeaves(Staff staff)
    {
        showUserDetails(staff.getPhotoUrl(), staff.getUserId(), staff.getFullName());
    }

    @Subscribe
    public void showStudentLeaves(Students students)
    {
        showUserDetails(students.getPhotoUrl(), students.getUserId(), students.getFullName());
    }

    @Subscribe
    public void showCurrentUserLeaves(Leaves leave)
    {
        setStartEndDateText(Leaves.getCalendar(leave.getFromDate()));
        showCurrentUserLeaves();
    }
}