package com.thnki.classroom.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.R;
import com.thnki.classroom.dialogs.AddOrEditStaffDialogFragment;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.viewholders.StaffViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.thnki.classroom.utils.ActionBarUtil.SHOW_INDEPENDENT_STAFF_MENU;

public class StaffAdapter extends FirebaseRecyclerAdapter<Staff, StaffViewHolder>
{
    private static final String TAG = "StaffAdapter";
    private AppCompatActivity mActivity;
    private DatabaseReference mStaffDbReference;
    public static boolean isSelectionEnabled;
    private ArrayList<String> mSelectedStaff;
    public Map<String, Staff> mUnSelectedStaff;
    private boolean isSelectAll;

    public static StaffAdapter getInstance(DatabaseReference reference, AppCompatActivity activity)
    {
        StaffAdapter adapter = new StaffAdapter(Staff.class,
                R.layout.staff_list_row, StaffViewHolder.class, reference.orderByChild(Staff.USER_ID), activity);
        adapter.mStaffDbReference = reference;
        return adapter;
    }

    private StaffAdapter(Class<Staff> modelClass, int modelLayout, Class<StaffViewHolder> viewHolderClass,
                         Query ref, AppCompatActivity activity)
    {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mActivity = activity;
    }

    @Override
    protected void populateViewHolder(final StaffViewHolder viewHolder, final Staff model, int position)
    {
        String imageUrl = model.getPhotoUrl();
        Glide.with(mActivity).load(imageUrl)
                .asBitmap().placeholder(R.mipmap.user_icon_accent)
                .centerCrop().into(viewHolder.mImageView);

        viewHolder.mFullName.setText(model.getFullName());
        viewHolder.mUserId.setText(model.getUserId());
        viewHolder.mDesignation.setText(model.getDesignation());
        viewHolder.mAdminImageView.setVisibility(model.getIsAdmin() ? View.VISIBLE : View.GONE);
        if (isSelectionEnabled)
        {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.optionsIconContainer.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.mCheckBox.setVisibility(View.GONE);
            viewHolder.optionsIconContainer.setVisibility(View.VISIBLE);
        }
        viewHolder.mCheckBox.setChecked(isSelectAll);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                Log.d(TAG, "onCheckedChanged : " + isChecked);
                if (isChecked)
                {
                    mSelectedStaff.add(model.getUserId());
                    mUnSelectedStaff.remove(model.getUserId());
                }
                else
                {
                    mSelectedStaff.remove(model.getUserId());
                    mUnSelectedStaff.put(model.getUserId(), model);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick");
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                Log.d(TAG, this + ", onLongClick, isSelectionEnabled : " + isSelectionEnabled);
                Otto.post(ActionBarUtil.SHOW_MULTIPLE_STAFF_MENU);
                enableSelection();
                return true;
            }
        });
        configureOptions(viewHolder, model);
    }

    private void configureOptions(final StaffViewHolder holder, final Staff staff)
    {
        holder.optionsIconContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popup = new PopupMenu(mActivity, v);
                popup.getMenuInflater()
                        .inflate(R.menu.classes_options, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.action_edit:
                                editClasses(staff);
                                break;
                            case R.id.action_delete:
                                confirmDelete(staff);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void confirmDelete(Staff staff)
    {
        Progress.show(R.string.deleting);
        mStaffDbReference.child(staff.getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Progress.hide();
                if (task.isSuccessful())
                {
                    ToastMsg.show(R.string.deleted);
                }
                else
                {
                    ToastMsg.show(R.string.please_try_again);
                }
            }
        });
    }

    private void editClasses(Staff staff)
    {
        FragmentManager manager = mActivity.getSupportFragmentManager();
        AddOrEditStaffDialogFragment fragment = AddOrEditStaffDialogFragment.getInstance(staff);
        fragment.show(manager, AddOrEditStaffDialogFragment.TAG);
    }

    private void enableSelection()
    {
        isSelectionEnabled = true;
        mSelectedStaff = new ArrayList<>();
        mUnSelectedStaff = new HashMap<>();
        Otto.register(this);
        notifyDataSetChanged();
    }

    @Subscribe
    public void reload(String str)
    {
        Log.d(TAG, "reload : " + str);
        if (str.equals(SHOW_INDEPENDENT_STAFF_MENU))
        {
            notifyDataSetChanged();
            Otto.unregister(this);
        }
    }

    public void enableAttendance()
    {
        isSelectAll = true;
        Otto.post(ActionBarUtil.SHOW_ATTENDANCE_MENU);
        enableSelection();

    }

    public void setSelectAll()
    {
        isSelectAll = !isSelectAll;
        notifyDataSetChanged();
    }

}
