package com.aboutfuture.bakingapp.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;
    @SerializedName("steps")
    private ArrayList<Step> steps;
    @SerializedName("servings")
    private int servings;
    @SerializedName("image")
    private String imagePath;

    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String imagePath) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imagePath = imagePath;
    }

    public Recipe(int id, String name, int servings, String imagePath) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.imagePath = imagePath;
    }

    public Recipe() { }

    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Ingredient> getIngredients() { return ingredients; }
    public ArrayList<Step> getSteps() { return steps; }
    public int getServings() { return servings; }
    public String getImagePath() { return imagePath; }

    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = new ArrayList<>();
        in.readTypedList(ingredients, Ingredient.CREATOR);
        steps = new ArrayList<>();
        in.readTypedList(steps, Step.CREATOR);
        servings = in.readInt();
        imagePath = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
        parcel.writeInt(servings);
        parcel.writeString(imagePath);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) { return new Recipe[size]; }
    };
}
