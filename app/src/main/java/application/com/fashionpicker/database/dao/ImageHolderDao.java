package application.com.fashionpicker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import application.com.fashionpicker.database.models.ImageHolderModel;

@Dao
public interface ImageHolderDao {

    @Query("DELETE FROM ImageHolderModel")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ImageHolderModel imageHolderModel);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ArrayList<ImageHolderModel> model);

    @Query("SELECT * FROM ImageHolderModel WHERE filePath = :filePath")
    ImageHolderModel findImageByFilePath(String filePath);

    @Insert
    void insertAll(ArrayList<ImageHolderModel> imageHolderModel);

    @Query("SELECT * FROM ImageHolderModel")
    List<ImageHolderModel> getAll();
}
