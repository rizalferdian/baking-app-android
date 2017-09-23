package com.example.cendana2000.bakingapp.Network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cendana2000 on 17-Sep-17.
 */

public class RecipeStepResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;
    public final static Parcelable.Creator<RecipeStepResponse> CREATOR = new Creator<RecipeStepResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RecipeStepResponse createFromParcel(Parcel in) {
            RecipeStepResponse instance = new RecipeStepResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.shortDescription = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.videoURL = ((String) in.readValue((String.class.getClassLoader())));
            instance.thumbnailURL = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RecipeStepResponse[] newArray(int size) {
            return (new RecipeStepResponse[size]);
        }

    }
            ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(shortDescription);
        dest.writeValue(description);
        dest.writeValue(videoURL);
        dest.writeValue(thumbnailURL);
    }

    public int describeContents() {
        return 0;
    }

}