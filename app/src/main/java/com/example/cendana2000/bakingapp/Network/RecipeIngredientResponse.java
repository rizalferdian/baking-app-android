package com.example.cendana2000.bakingapp.Network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cendana2000 on 17-Sep-17.
 */
public class RecipeIngredientResponse implements Parcelable
{

    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("ingredient")
    @Expose
    private String ingredient;
    public final static Parcelable.Creator<RecipeIngredientResponse> CREATOR = new Creator<RecipeIngredientResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RecipeIngredientResponse createFromParcel(Parcel in) {
            RecipeIngredientResponse instance = new RecipeIngredientResponse();
            instance.quantity = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.measure = ((String) in.readValue((String.class.getClassLoader())));
            instance.ingredient = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RecipeIngredientResponse[] newArray(int size) {
            return (new RecipeIngredientResponse[size]);
        }

    }
            ;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(ingredient);
    }

    public int describeContents() {
        return 0;
    }

}
