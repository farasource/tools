package com.android.bahaar.DBnote;

/**
 * on 07/10/2018.
 */

public class DatabaseModel {

    private long ID;
    private String Note;
    private String Time;
    private String Type;

    public String getID() {
        return String.valueOf(ID);
    }

    public void setID(long id) {
        this.ID = id;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        this.Note = note;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

}
