package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.classroom.R;
import com.thnki.classroom.adapters.RequestedLeavesAdapter;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.NavigationDrawerUtil;
import com.thnki.classroom.utils.Otto;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RequestedLeavesDialogFragment extends CustomDialogFragment implements ValueEventListener
{
    public static final String TAG = "RequestedLeavesDialogFragment";

    @Bind(R.id.recyclerView)
    RecyclerView mRequestedLeavesRecyclerView;

    @Bind(R.id.errorMsg)
    TextView mErrorMsg;

    @Bind(R.id.recyclerProgress)
    View mProgress;

    private DatabaseReference mRequestedLeavesDbRef;
    private Leaves mLeave;
    private RequestedLeavesAdapter adapter;

    public RequestedLeavesDialogFragment()
    {

    }

    public static RequestedLeavesDialogFragment getInstance()
    {
        return new RequestedLeavesDialogFragment();
    }

    @Override
    public void onCreateView(View parentView)
    {
        ButterKnife.bind(this, parentView);
        mRequestedLeavesDbRef = FirebaseDatabase.getInstance().getReference()
                .child(Leaves.LEAVES).child(NavigationDrawerUtil.mCurrentUser.getUserId())
                .child(Leaves.REQUESTED_LEAVES);
        mRequestedLeavesDbRef.addValueEventListener(this);
        Otto.register(this);
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        return R.layout.fragment_list;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setDialogTitle(R.string.requested_leaves);
        mErrorMsg.setText(R.string.noLeavesFound);
        adapter = RequestedLeavesAdapter.getInstance(mRequestedLeavesDbRef);
        mRequestedLeavesRecyclerView.setAdapter(adapter);
        mRequestedLeavesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

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
            if (mLeave != null)
            {
                int index = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if (snapshot.getValue(Leaves.class).equals(mLeave))
                    {
                        mRequestedLeavesRecyclerView.scrollToPosition(index);
                        break;
                    }
                    index++;
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
        mProgress.setVisibility(View.GONE);
        ToastMsg.show(R.string.couldntLoadTheList);
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        Otto.unregister(this);
    }

    public static RequestedLeavesDialogFragment getInstance(Leaves mLeave)
    {
        RequestedLeavesDialogFragment fragment = getInstance();
        fragment.mLeave = mLeave;
        return fragment;
    }
}