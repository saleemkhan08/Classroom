package com.thnki.classroom.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.otto.Subscribe;
import com.thnki.classroom.R;
import com.thnki.classroom.dialogs.AddOrEditSubjectsDialogFragment;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.Subjects;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.ActionBarUtil;
import com.thnki.classroom.utils.ImageUtil;
import com.thnki.classroom.utils.Otto;
import com.thnki.classroom.viewholders.SubjectViewHolder;

import java.util.ArrayList;

import static com.thnki.classroom.utils.ActionBarUtil.SHOW_INDEPENDENT_SUBJECT_MENU;

public class SubjectsAdapter extends FirebaseRecyclerAdapter<Subjects, SubjectViewHolder>
{
    private static final String TAG = "SubjectsAdapter";
    private AppCompatActivity mActivity;
    public static boolean isSelectionEnabled;
    public ArrayList<String> mSelectedSubjects;
    public DatabaseReference mSubjectsRef;

    private boolean isSelectAll;

    public static SubjectsAdapter getInstance(DatabaseReference reference, AppCompatActivity activity)
    {
        Log.d(TAG, "SubjectsAdapter getInstance: reference : " + reference);
        SubjectsAdapter fragment = new SubjectsAdapter(Subjects.class,
                R.layout.subject_list_row, SubjectViewHolder.class, reference, activity);
        fragment.mSubjectsRef = reference;
        return fragment;
    }

    private SubjectsAdapter(Class<Subjects> modelClass, int modelLayout, Class<SubjectViewHolder> viewHolderClass,
                            Query ref, AppCompatActivity activity)
    {
        super(modelClass, modelLayout, viewHolderClass, ref);
        Log.d(TAG, "SubjectsAdapter Constructor");
        mActivity = activity;
    }

    @Override
    protected void populateViewHolder(final SubjectViewHolder viewHolder, final Subjects model, int position)
    {
        Log.d(TAG, "populateViewHolder : " + position);
        String imageUrl = model.getTeacherImgUrl();
        ImageUtil.loadCircularImg(viewHolder.itemView.getContext(), imageUrl, viewHolder.mTeacherImage);

        viewHolder.mSubjectName.setText(model.getSubjectName());
        viewHolder.mClassTeacherName.setText(model.getTeacherName());
        Log.d(TAG, this + " : isSelectionEnabled : " + isSelectionEnabled);
        viewHolder.mCheckBox.setVisibility(isSelectionEnabled ? View.VISIBLE : View.GONE);
        viewHolder.mOptionsIconContainer.setVisibility(isSelectionEnabled ? View.GONE : View.VISIBLE);
        viewHolder.mCheckBox.setChecked(isSelectAll);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                Log.d(TAG, "onCheckedChanged : " + isChecked);
                if (isChecked)
                {
                    mSelectedSubjects.add(model.getSubjectCode());
                }
                else
                {
                    mSelectedSubjects.remove(model.getSubjectCode());
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
                Otto.post(ActionBarUtil.SHOW_MULTIPLE_SUBJECT_MENU);
                enableSelection();
                return true;
            }
        });
        configureOptions(viewHolder, model);
    }

    private void enableSelection()
    {
        isSelectionEnabled = true;
        mSelectedSubjects = new ArrayList<>();
        Otto.register(SubjectsAdapter.this);
        notifyDataSetChanged();
    }

    @Subscribe
    public void reload(String str)
    {
        Log.d(TAG, "reload : " + str);
        if (str.equals(SHOW_INDEPENDENT_SUBJECT_MENU))
        {
            notifyDataSetChanged();
            Otto.unregister(this);
        }
    }

    private void configureOptions(final SubjectViewHolder holder, final Subjects subject)
    {
        holder.mOptionsIconContainer.setOnClickListener(new View.OnClickListener()
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
                                editClasses(subject);
                                break;
                            case R.id.action_delete:
                                confirmDelete(subject);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    public void setSelectAll()
    {
        isSelectAll = !isSelectAll;
        notifyDataSetChanged();
    }

    private void confirmDelete(Subjects subject)
    {
        Progress.show(R.string.deleting);
        mSubjectsRef.child(subject.getSubjectCode()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
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

    private void editClasses(Subjects subject)
    {
        AddOrEditSubjectsDialogFragment fragment = AddOrEditSubjectsDialogFragment.getInstance(subject);
        fragment.show(mActivity.getSupportFragmentManager(), AddOrEditSubjectsDialogFragment.TAG);
    }
}