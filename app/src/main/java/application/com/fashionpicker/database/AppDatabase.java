package application.com.fashionpicker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import application.com.fashionpicker.database.dao.ImageHolderDao;
import application.com.fashionpicker.database.models.ImageHolderModel;

@Database(entities = {ImageHolderModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImageHolderDao imageHolderDao();
}
