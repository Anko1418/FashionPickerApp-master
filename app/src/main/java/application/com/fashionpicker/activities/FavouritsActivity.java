package application.com.fashionpicker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import application.com.fashionpicker.R;
import application.com.fashionpicker.database.models.FavModel;
import application.com.fashionpicker.database.models.ImageHolderModel;
import application.com.fashionpicker.utilities.DatabaseHelper;

public class FavouritsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private ArrayList<FavModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourits);
        init();
    }

    private void init() {

        databaseHelper = DatabaseHelper.getInstance(this);
        initData();
    }

    private void initData() {

        AsyncTask.execute(() -> {

            models =  databaseHelper.getAllFav();
            FavouritsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    initRecyclerView();
                }
            });
        });
    }

    private void initRecyclerView() {

        recyclerView = findViewById(R.id.recyc_fav);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CustomFavAdapter());
    }

    class CustomFavAdapter extends RecyclerView.Adapter<CustomFavAdapter.MyFavHolder> {

        @NonNull
        @Override
        public MyFavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyFavHolder(LayoutInflater.from(FavouritsActivity.this).inflate(R.layout.fav_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyFavHolder holder, int position) {

            FavModel favModel = models.get(position);
            Glide.with(FavouritsActivity.this).load(new File(favModel.getTop().getFilePath())).into(holder.img_top_hav);
            Glide.with(FavouritsActivity.this).load(new File(favModel.getBottom().getFilePath())).into(holder.img_bottom_hav);
            holder.imgBtn_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    favModel.getTop().getFavTag().remove(favModel.getBottom().getFilePath());
                    favModel.getBottom().getFavTag().remove(favModel.getTop().getFilePath());
                    ArrayList<ImageHolderModel> innerModels = new ArrayList<>();
                    innerModels.add(favModel.getTop());
                    innerModels.add(favModel.getBottom());
                    databaseHelper.updateAll(innerModels, true);
                    models.remove(favModel);
                    notifyItemRemoved(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        class MyFavHolder extends RecyclerView.ViewHolder {

            private ImageButton imgBtn_fav;
            private ImageView img_top_hav;
            private ImageView img_bottom_hav;

            public MyFavHolder(View itemView) {
                super(itemView);
                imgBtn_fav = itemView.findViewById(R.id.imgBtn_fav);
                img_top_hav = itemView.findViewById(R.id.img_top_hav);
                img_bottom_hav = itemView.findViewById(R.id.img_bottom_fav);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
