package com.thnki.classroom.adapters;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.thnki.classroom.R;
import com.thnki.classroom.model.Students;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.viewholders.StudentViewHolder;

public class LeavesAdapter extends FirebaseRecyclerAdapter<Students, StudentViewHolder>
{
    private static final String TAG = "LeavesAdapter";

    public static LeavesAdapter getInstance(DatabaseReference reference)
    {
        Log.d(TAG, "LeavesAdapter getInstance: reference : " + reference);
        return new LeavesAdapter(Students.class,
                R.layout.student_list_row, StudentViewHolder.class, reference);
    }

    private LeavesAdapter(Class<Students> modelClass, int modelLayout, Class<StudentViewHolder> viewHolderClass,
                          Query ref)
    {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(final StudentViewHolder viewHolder, final Students model, int position)
    {
        Log.d(TAG, "populateViewHolder : " + position);
        String imageUrl = model.getPhotoUrl();
        ImageUtil.loadCircularImg(viewHolder.itemView.getContext(), imageUrl, viewHolder.mImageView);

        viewHolder.mFullName.setText(model.getFullName());
        viewHolder.mUserId.setText(model.getUserId());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Otto.post(model);
                Toast.makeText(view.getContext(), model.getUserId(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}