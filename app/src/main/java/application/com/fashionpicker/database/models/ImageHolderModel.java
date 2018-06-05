package application.com.fashionpicker.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;

import application.com.fashionpicker.utilities.StringToArraylistConverter;

@Entity
public class ImageHolderModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String filePath;
    private boolean top;
    @TypeConverters(StringToArraylistConverter.class)
    private ArrayList<String> favTag = new ArrayList<>();

    public ImageHolderModel() {
    }

    public ArrayList<String> getFavTag() {
        return favTag;
    }

    public void setFavTag(ArrayList<String> favTag) {
        this.favTag = favTag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int hashCode() {

        return this.id;
    }

    @Override
    public boolean equals(Object obj) {

        return ((ImageHolderModel) obj).filePath.equalsIgnoreCase(filePath);
    }
}
