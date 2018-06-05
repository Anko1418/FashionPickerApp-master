package application.com.fashionpicker.utilities;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import application.com.fashionpicker.database.AppDatabase;
import application.com.fashionpicker.database.models.FavModel;
import application.com.fashionpicker.database.models.ImageHolderModel;

public class DatabaseHelper {

    private static DatabaseHelper databaseHelper;
    private AppDatabase appDatabase;

    private DatabaseHelper(Context context) {

        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "fashion-database").build();
    }

    public static DatabaseHelper getInstance(Context context) {

        if (databaseHelper == null) {

            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    public void insertAll(final ArrayList<ImageHolderModel> topHolderModels, ArrayList<ImageHolderModel> bottomHolderModels, boolean onWorkerThread) {

        if (onWorkerThread) {

            AsyncTask.execute(() -> {

                appDatabase.imageHolderDao().deleteAll();
                ArrayList<ImageHolderModel> holderModels = new ArrayList<>();
                holderModels.addAll(topHolderModels);
                holderModels.addAll(bottomHolderModels);
                appDatabase.imageHolderDao().insertAll(holderModels);
            });
        } else {

            appDatabase.imageHolderDao().insertAll(topHolderModels);
        }
    }

    public void insertAll(final ArrayList<ImageHolderModel> models, boolean onWorkerThread) {

        if (onWorkerThread) {

            AsyncTask.execute(() -> {

                appDatabase.imageHolderDao().insertAll(models);
            });
        } else {

            appDatabase.imageHolderDao().insertAll(models);
        }
    }

    public void updateAll(final ArrayList<ImageHolderModel> models, boolean onWorkerThread) {

        if (onWorkerThread) {

            AsyncTask.execute(() -> {

                appDatabase.imageHolderDao().update(models);
                //for (int i=0; i<models.size(); i++) {

                  //  appDatabase.imageHolderDao().update(models.get(i));
                //}
            });
        } else {

            for (int i=0; i<models.size(); i++) {

                appDatabase.imageHolderDao().update(models);
            }
        }
    }

    public void deleteAll(boolean onWorkerThread) {

        if (onWorkerThread) {

            AsyncTask.execute(() -> appDatabase.imageHolderDao().deleteAll());
        } else {

            appDatabase.imageHolderDao().deleteAll();
        }
    }

    public void update(ImageHolderModel imageHolderModel) {

        appDatabase.imageHolderDao().insert(imageHolderModel);
    }

    public ArrayList<ImageHolderModel> getAll() {

        return new ArrayList<>(appDatabase.imageHolderDao().getAll());
    }

    public ArrayList<FavModel> getAllFav() {

        ArrayList<ImageHolderModel> models = new ArrayList<>(appDatabase.imageHolderDao().getAll());
        for (int i=0; i<models.size(); i++) {

            ImageHolderModel imageHolderModel = models.get(i);
            if (imageHolderModel.getFavTag().size() <= 0) {

                models.remove(i);
                i--;
            }
        }
        ArrayList<FavModel> favModels = new ArrayList<>();
        for (int i=0; i<models.size(); i++) {

            ImageHolderModel topModel = models.get(i);
            for (int j= i+1; j<models.size(); j++) {

                ImageHolderModel bottomModel = models.get(j);
                if (topModel.getFavTag().contains(bottomModel.getFilePath()) && bottomModel.getFavTag().contains(topModel.getFilePath())) {

                    FavModel favModel = new FavModel();
                    favModel.setTop(topModel);
                    favModel.setBottom(bottomModel);
                    favModels.add(favModel);
                }
            }
        }
        return favModels;
    }
}
