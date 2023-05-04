package com.example.teamfind;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class StoryReceiver implements Parcelable {
    String title;
    String content;
    String authorId;
    String genre;
    String imgUrl;
    public StoryReceiver(String title, String content, String authorId, String genre, String imgUrl) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.genre = genre;
        this.imgUrl = imgUrl;
    }

    public StoryReceiver(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.authorId = in.readString();
        this.genre = in.readString();
        this.imgUrl = in.readString();
    }



    public StoryReceiver() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(authorId);
        parcel.writeString(genre);
        parcel.writeString(imgUrl);
    }
    public static final Parcelable.Creator<StoryReceiver> CREATOR = new Parcelable.Creator<StoryReceiver>() {
        public StoryReceiver createFromParcel(Parcel in) {
            return new StoryReceiver(in);
        }

        public StoryReceiver[] newArray(int size) {
            return new StoryReceiver[size];
        }
    };
}
