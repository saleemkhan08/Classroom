package com.thnki.classroom.fragments;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.MainActivity;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.NotesAdapter;
import com.thnki.classroom.adapters.TimeTableAdapter;
import com.thnki.classroom.listeners.EventsListener;
import com.thnki.classroom.model.Classes;
import com.thnki.classroom.model.Notes;
import com.thnki.classroom.model.NotesClassifier;
import com.thnki.classroom.model.Subjects;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.utils.TransitionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NotesFragment extends ClassTabFragment implements EventsListener
{
    private static final String TAG = "NotesFragment";

    @Bind(R.id.notesRecyclerView)
    RecyclerView mNotesRecyclerView;

    @Bind(R.id.recyclerProgress)
    View mProgress;

    @Bind(R.id.errorMsg)
    View mErrorMsg;

    @Bind(R.id.fabContainer)
    ViewGroup mFabContainer;

    @Bind(R.id.subjectsTab)
    TabLayout mSubjectsTab;


    private DatabaseReference mRootRef;
    private Classes mCurrentClass;
    private DatabaseReference mNotesDbRef;
    private String mCurrentSubjectCode;
    private DatabaseReference mSubjectDbRef;
    private NotesClassifier mCurrentNotesClassifier;
    private boolean areSubjectsAvailable;
    private NotesAdapter mAdapter;

    public NotesFragment()
    {
        Log.d(TAG, "NotesFragment");
    }

    @Override
    public void onCreateView(View parentView)
    {
        Log.d(TAG, "onCreateView2");
        ButterKnife.bind(this, parentView);
        mCurrentNotesClassifier = new NotesClassifier();
        ((MainActivity) getActivity()).setToolBarTitle(getString(R.string.notes));
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setUpSubjectsTabsListener()
    {
        mSubjectsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                Subjects subject = (Subjects) tab.getTag();
                mCurrentSubjectCode = subject.getSubjectCode();
                mCurrentNotesClassifier.setSubjectId(mCurrentSubjectCode);
                mCurrentNotesClassifier.setTeacherId(subject.getTeacherCode());
                mCurrentNotesClassifier.setSubjectName(subject.getSubjectName());
                if (mCurrentClass != null)
                {
                    setUpRecyclerView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Otto.register(this);
        Log.d(TAG, "onStart");
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        Log.d(TAG, "getContentViewLayoutRes");
        return R.layout.fragment_notes;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).updateEventsListener(this);
        }
    }


    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
        Otto.unregister(this);
    }

    private void setUpRecyclerView()
    {
        Log.d(TAG, "setUpRecyclerView");


        if (mCurrentNotesClassifier.isReviewedNotesShown())
        {
            if (!NavigationDrawerUtil.isStudent)
            {
                Otto.post(ActionBarUtil.SHOW_PENDING_NOTES_FRAGMENT_MENU);
            }
            mNotesDbRef = mRootRef.child(Notes.NOTES).child(mCurrentClass.getCode()).child(mCurrentSubjectCode);
        }
        else
        {
            if (!NavigationDrawerUtil.isStudent)
            {
                Otto.post(ActionBarUtil.SHOW_NOTES_FRAGMENT_MENU);
            }
            mNotesDbRef = mRootRef.child(Notes.NOTES).child(Notes.REVIEW).child(mCurrentClass.getCode()).child(mCurrentSubjectCode);
        }

        mAdapter = NotesAdapter.getInstance(mCurrentNotesClassifier, mNotesDbRef, (AppCompatActivity) getActivity());
        mNotesRecyclerView.setAdapter(mAdapter);
        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNotesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 50 && mFabContainer.isShown())
                {
                    TransitionUtil.slideTransition(mFabContainer);
                    mFabContainer.setVisibility(View.GONE);
                }
                else if (dy < 0 && !mFabContainer.isShown())
                {
                    TransitionUtil.slideTransition(mFabContainer);
                    mFabContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mNotesDbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mProgress.setVisibility(View.GONE);
                if (dataSnapshot.getChildrenCount() <= 0)
                {
                    mErrorMsg.setVisibility(View.VISIBLE);
                }
                else
                {
                    mErrorMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "databaseError : " + databaseError);
            }
        });
    }

    @OnClick(R.id.addNotes)
    public void addNotes(View view)
    {
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
        {
            ((MainActivity) activity).showFragment(AddOrEditNotesFragment.getInstance(mCurrentNotesClassifier),
                    true, AddOrEditNotesFragment.TAG);
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (TimeTableAdapter.isSelectionEnabled)
        {
            TimeTableAdapter.isSelectionEnabled = false;
            mAdapter.notifyDataSetChanged();
            Otto.post(ActionBarUtil.SHOW_INDEPENDENT_TIME_TABLE_MENU);
            return false;
        }
        return true;
    }

    @Override
    public int getMenuItemId()
    {
        return R.id.launch_notes_fragment;
    }

    @Override
    public String getTagName()
    {
        return NavigationDrawerUtil.NOTES_FRAGMENT;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        mCurrentClass = (Classes) tab.getTag();
        mCurrentNotesClassifier.setClassId(mCurrentClass.getCode());
        mCurrentNotesClassifier.setClassName(mCurrentClass.getName());
        updateSubjectsAvailability();
        setUpSubjectsTabsListener();
    }


    private void updateSubjectsAvailability()
    {
        mSubjectDbRef = FirebaseDatabase.getInstance().getReference()
                .child(Subjects.SUBJECTS).child(mCurrentClass.getCode());
        mSubjectDbRef.addListenerForSingleValueEvent(new ValueEventListener()
                                                     {
                                                         @Override
                                                         public void onDataChange(DataSnapshot dataSnapshot)
                                                         {
                                                             areSubjectsAvailable = (dataSnapshot.getChildrenCount() > 0);
                                                             mSubjectsTab.removeAllTabs();
                                                             for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                                             {
                                                                 Subjects subject = snapshot.getValue(Subjects.class);
                                                                 TabLayout.Tab tab = mSubjectsTab.newTab();
                                                                 tab.setText(subject.getSubjectName());
                                                                 tab.setTag(subject);
                                                                 mSubjectsTab.addTab(tab);
                                                             }
                                                             if (mSubjectsTab.getTabAt(0) != null)
                                                             {
                                                                 mSubjectsTab.getTabAt(0).select();
                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(DatabaseError databaseError)
                                                         {

                                                         }
                                                     }

                                                    );
    }

    @Subscribe
    public void onOptionItemClicked(Integer itemId)
    {
        switch (itemId)
        {
            case R.id.reviewed:
                mCurrentNotesClassifier.setReviewedNotesShown(true);
                setUpRecyclerView();
                break;
            case R.id.pending:
                mCurrentNotesClassifier.setReviewedNotesShown(false);
                setUpRecyclerView();
                break;

        }
    }
}
