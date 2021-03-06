package com.thnki.classroom.adapters;

import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Staff;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.viewholders.StaffViewHolder;

public class SimpleStaffAdapter extends FirebaseRecyclerAdapter<Staff, StaffViewHolder>
{
    private static final String TAG = "SimpleStaffAdapter";

    public static SimpleStaffAdapter getInstance(DatabaseReference reference)
    {
        SimpleStaffAdapter adapter = new SimpleStaffAdapter(Staff.class,
                R.layout.staff_list_row, StaffViewHolder.class, reference.orderByChild(Staff.USER_ID));
        return adapter;
    }

    private SimpleStaffAdapter(Class<Staff> modelClass, int modelLayout, Class<StaffViewHolder> viewHolderClass,
                               Query ref)
    {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(final StaffViewHolder viewHolder, final Staff model, int position)
    {
        String imageUrl = model.getPhotoUrl();
        ImageUtil.loadCircularImg(viewHolder.itemView.getContext(), imageUrl, viewHolder.mImageView);

        viewHolder.mFullName.setText(model.getFullName());
        viewHolder.mUserId.setText(model.getUserId());
        viewHolder.mDesignation.setText(model.getDesignation());
        viewHolder.optionsIconContainer.setVisibility(View.GONE);
        viewHolder.mAdminImageView.setVisibility(model.getIsAdmin() ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Otto.post(model);
                Toast.makeText(view.getContext(), "Test : "+model.getUserId(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
