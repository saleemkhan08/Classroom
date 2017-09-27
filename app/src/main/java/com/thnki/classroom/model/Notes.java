package com.thnki.classroom.model;

import java.util.ArrayList;

public class Notes
{
    public static final String NOTES = "notes";
    public static final String DATE = "date";
    private String notesTitle;
    private String notesDescription;
    private ArrayList<NotesImage> notesImages;
    private String reviewerId;
    private String submitterId;
    private String submitterName;
    private String submitterPhotoUrl;
    private String date;

    public Notes()
    {

    }

    public String getNotesTitle()
    {
        return notesTitle;
    }

    public void setNotesTitle(String notesTitle)
    {
        this.notesTitle = notesTitle;
    }

    public String getNotesDescription()
    {
        return notesDescription;
    }

    public void setNotesDescription(String notesDescription)
    {
        this.notesDescription = notesDescription;
    }

    public ArrayList<NotesImage> getNotesImages()
    {
        return notesImages;
    }

    public void setNotesImages(ArrayList<NotesImage> notesImages)
    {
        this.notesImages = notesImages;
    }

    public String getReviewerId()
    {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId)
    {
        this.reviewerId = reviewerId;
    }

    public String getSubmitterId()
    {
        return submitterId;
    }

    public void setSubmitterId(String submitterId)
    {
        this.submitterId = submitterId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getSubmitterName()
    {
        return submitterName;
    }

    public void setSubmitterName(String submitterName)
    {
        this.submitterName = submitterName;
    }

    public String getSubmitterPhotoUrl()
    {
        return submitterPhotoUrl;
    }

    public void setSubmitterPhotoUrl(String submitterPhotoUrl)
    {
        this.submitterPhotoUrl = submitterPhotoUrl;
    }
}
