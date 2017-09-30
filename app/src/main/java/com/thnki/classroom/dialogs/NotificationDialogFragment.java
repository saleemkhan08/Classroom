package com.thnki.classroom.dialogs;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thnki.classroom.R;
import com.thnki.classroom.listeners.OnDismissListener;
import com.thnki.classroom.model.Leaves;
import com.thnki.classroom.model.Notes;
import com.thnki.classroom.model.Notifications;
import com.thnki.classroom.model.Progress;
import com.thnki.classroom.model.ToastMsg;
import com.thnki.classroom.utils.NavigationDrawerUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationDialogFragment extends CustomDialogFragment
{
    public static final String TAG = "NotificationDialogFragment";

    @Bind(R.id.message)
    EditText mMessage;

    DatabaseReference mNotificationDbRef;
    Notes notes;
    Notifications mCurrentNotification;
    OnDismissListener listener;

    public static NotificationDialogFragment getInstance(Notes notes, OnDismissListener listener)
    {
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        fragment.listener = listener;
        fragment.mCurrentNotification = new Notifications();
        fragment.mCurrentNotification.setSenderId(NavigationDrawerUtil.mCurrentUser.getUserId());
        fragment.mCurrentNotification.setSenderName(NavigationDrawerUtil.mCurrentUser.getFullName());
        fragment.mCurrentNotification.setSenderPhotoUrl(NavigationDrawerUtil.mCurrentUser.getPhotoUrl());

        fragment.notes = notes;

        return fragment;
    }

    public NotificationDialogFragment()
    {

    }

    @Override
    public void onCreateView(View parentView)
    {
        ButterKnife.bind(this, parentView);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Notifications.NOTIFICATIONS);
        switch (notes.getNotesStatus())
        {
            case Notes.REJECTED:
                mCurrentNotification.setMessage(getNotesRejectionMsg(notes));
                mNotificationDbRef = ref.child(notes.getSubmitterId());
                break;

            case Notes.APPROVED:
                mCurrentNotification.setMessage(getNotesApprovalMsg(notes));
                mNotificationDbRef = ref.child(notes.getSubmitterId());
                break;

            case Notes.REVIEW:
                mCurrentNotification.setMessage(getNotesReviewMsg(notes));
                mNotificationDbRef = ref.child(notes.getReviewerId());
                break;
        }
    }

    private String getNotesReviewMsg(Notes notes)
    {
        return notes.getSubmitterName() + " " + getString(R.string.has_submitted_following_notes_for_review)
                + "\n\n\"" + notes.getNotesTitle() + "\"\n"
                + getString(R.string.uploaded_at) + " : " + notes.displayDate()
                + "\n\n" + getString(R.string.reviewerComment)
                + " : ";
    }

    private String getNotesApprovalMsg(Notes notes)
    {
        return getString(R.string.following_notes_has_been_approved)
                + "\n\n\"" + notes.getNotesTitle() + "\"\n"
                + getString(R.string.uploaded_at) + " : " + notes.displayDate()
                + "\n\n" + getString(R.string.reviewerComment)
                + " : ";
    }

    @Override
    protected int getContentViewLayoutRes()
    {
        return R.layout.fragment_notification;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setDialogTitle(R.string.reviewerComment);
        setSubmitBtnTxt(R.string.send);
        setSubmitBtnImg(R.mipmap.submit);
    }

    @Override
    public void submit(View view)
    {
        String message = mMessage.getText().toString();
        if (TextUtils.isEmpty(message))
        {
            ToastMsg.show(R.string.please_enter_a_valid_reviewer_comment);
        }
        else
        {
            Progress.show(R.string.saving);
            message = mCurrentNotification.getMessage() + message;
            String key = Leaves.getDbKeyDateTime(Calendar.getInstance());
            mCurrentNotification.setMessage(message);
            mCurrentNotification.setDateTime(-Long.parseLong(key));

            mNotificationDbRef.child(key).setValue(mCurrentNotification).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    dismiss();
                    Progress.hide();
                    if (task.isSuccessful())
                    {
                        ToastMsg.show(R.string.saved);
                    }
                    else
                    {
                        ToastMsg.show(R.string.please_try_again);
                    }
                }
            });
        }
    }

    String getNotesRejectionMsg(Notes notes)
    {
        return getString(R.string.following_notes_has_been_rejected)
                + "\n\n\"" + notes.getNotesTitle() + "\"\n"
                + getString(R.string.uploaded_at) + " : " + notes.displayDate()
                + "\n\n" + getString(R.string.reviewerComment)
                + " : ";
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        listener.onDismiss();
    }
}