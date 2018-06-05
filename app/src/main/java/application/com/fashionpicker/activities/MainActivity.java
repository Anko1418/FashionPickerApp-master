package application.com.fashionpicker.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import application.com.fashionpicker.R;
import application.com.fashionpicker.adapters.CustomPagerAdapter;
import application.com.fashionpicker.database.models.ImageHolderModel;
import application.com.fashionpicker.utilities.Constants;
import application.com.fashionpicker.utilities.DatabaseHelper;

import static application.com.fashionpicker.utilities.InternalStorageHelper.getPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ImageHolderModel> bottomHolderModels = new ArrayList<>();
    private ArrayList<ImageHolderModel> topHolderModels = new ArrayList<>();
    private CustomPagerAdapter topPagerAdapter, bottomPagerAdapter;
    private ViewPager viewPagerTop, viewPagerBottom;
    private DatabaseHelper databaseHelper;
    private boolean isTopClicked;
    private ImageView imgFavourite;

    //static
    private static boolean isFirstLaunch = true;
    //private static boolean isConfigurationChanged;
    private static int currentTopIndex = 0;
    private static int currentBottomIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        if (isFirstLaunch) {

            isFirstLaunch = false;
            //databaseHelper.deleteAll(true);
            currentTopIndex = 0;
            currentBottomIndex = 0;
        }/* else {

            //isConfigurationChanged = true;
        }*/
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_main_potrait);
        } else {

            setContentView(R.layout.activity_main_landscape);
        }
        init();
        //if (isConfigurationChanged) {

            AsyncTask.execute(() -> setHoldersData(databaseHelper.getAll()));
        //}
    }

    private void init() {

        initResources();
        initData();
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                dialog.dismiss();
                checkForCameraPermission();
            } else if (items[item].equals("Choose from Library")) {
                dialog.dismiss();
                checkForGalleryPermission();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkForGalleryPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "You need to allow permission to access this feature", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.GALLERY_INTENT_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.GALLERY_INTENT_CODE);
            }
        } else {

            galleryIntent();
        }
    }

    private void checkForCameraPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(this, "You need to allow permission to access this feature", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.CAMERA_INTENT_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.CAMERA_INTENT_CODE);
            }
        } else {

            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CAMERA_INTENT_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                cameraIntent();
            } else {

                Toast.makeText(this, "You need to allow permission to access this feature", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == Constants.GALLERY_INTENT_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                galleryIntent();
            } else {

                Toast.makeText(this, "You need to allow permission to access this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.GALLERY_INTENT_CODE);
    }

    private void cameraIntent() {

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, Constants.CAMERA_INTENT_CODE);
    }

    private void initData() {

        topPagerAdapter = new CustomPagerAdapter(this);
        bottomPagerAdapter = new CustomPagerAdapter(this);
        topPagerAdapter.setImageHolderModels(topHolderModels);
        bottomPagerAdapter.setImageHolderModels(bottomHolderModels);
        viewPagerTop.setAdapter(topPagerAdapter);
        viewPagerBottom.setAdapter(bottomPagerAdapter);
        setViewPagerListeners();
    }

    private void setViewPagerListeners() {

        viewPagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentTopIndex = position;
                ImageHolderModel topModel = topHolderModels.get(currentTopIndex);
                ImageHolderModel bottomModel = bottomHolderModels.get(currentBottomIndex);
                if(topModel.getFavTag().contains(bottomModel.getFilePath()) && bottomModel.getFavTag().contains(topModel.getFilePath())) {

                    toggleFavIcon(true);
                } else {

                    toggleFavIcon(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerBottom.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentBottomIndex = position;
                ImageHolderModel topModel = topHolderModels.get(currentTopIndex);
                ImageHolderModel bottomModel = bottomHolderModels.get(currentBottomIndex);
                if(topModel.getFavTag().contains(bottomModel.getFilePath()) && bottomModel.getFavTag().contains(topModel.getFilePath())) {

                    toggleFavIcon(true);
                } else {

                    toggleFavIcon(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initResources() {

        viewPagerTop = findViewById(R.id.vp_top);
        viewPagerBottom = findViewById(R.id.vp_bottom);
        imgFavourite = findViewById(R.id.imgBtn_fav);
        imgFavourite.setOnClickListener(this);
        findViewById(R.id.imgBtn_add_top).setOnClickListener(this);
        findViewById(R.id.imgBtn_add_bottom).setOnClickListener(this);
        findViewById(R.id.imgBtn_shuffle).setOnClickListener(this);
        findViewById(R.id.btn_favs).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgBtn_add_bottom:
                isTopClicked = false;
                selectImage();
                break;

            case R.id.imgBtn_add_top:
                isTopClicked = true;
                selectImage();
                break;

            case R.id.imgBtn_shuffle:
                shuffle();
                break;

            case R.id.imgBtn_fav:
                favouriteToggle();
                break;

            case R.id.btn_favs:
                openFavActivity();
        }
    }

    private void openFavActivity() {

        Intent intent = new Intent(this, FavouritsActivity.class);
        startActivity(intent);
        finish();
    }

    private void favouriteToggle() {

        if (topHolderModels.size() > 0 && bottomHolderModels.size() > 0) {

            int topIndex= viewPagerTop.getCurrentItem();
            int bottomIndex = viewPagerBottom.getCurrentItem();
            ImageHolderModel topModel = topHolderModels.get(topIndex);
            ImageHolderModel bottomModel = bottomHolderModels.get(bottomIndex);
            boolean flag;
            if(!topModel.getFavTag().contains(bottomModel.getFilePath()) && !bottomModel.getFavTag().contains(topModel.getFilePath())) {

                topModel.getFavTag().add(bottomModel.getFilePath());
                bottomModel.getFavTag().add(topModel.getFilePath());
                flag = true;
            } else {

                topModel.getFavTag().remove(bottomModel.getFilePath());
                bottomModel.getFavTag().remove(topModel.getFilePath());
                flag = false;
            }
            ArrayList<ImageHolderModel> models = new ArrayList<>();
            models.add(topModel);
            models.add(bottomModel);
            databaseHelper.updateAll(models, true);
            topPagerAdapter.notifyDataSetChanged();
            bottomPagerAdapter.notifyDataSetChanged();
            toggleFavIcon(flag);
        } else {

            Toast.makeText(this, "Can't find any combination", Toast.LENGTH_SHORT).show();
        }
    }

    private void shuffle() {

        if (topHolderModels.size() > 0) {

            viewPagerTop.setCurrentItem(randomNumberInRange(0, topHolderModels.size() - 1), true);
        }
        if (bottomHolderModels.size() > 0) {

            viewPagerBottom.setCurrentItem(randomNumberInRange(0, bottomHolderModels.size() - 1), true);
        }
    }

    public static int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == Constants.CAMERA_INTENT_CODE && resultCode == RESULT_OK) {

                if (data.getExtras() != null && data.getExtras().containsKey("data")) {

                    addImage(getImageUri((Bitmap) data.getExtras().get("data")), true);
                }
                databaseHelper.insertAll(topHolderModels, bottomHolderModels, true);
            } else if (requestCode == Constants.GALLERY_INTENT_CODE && resultCode == RESULT_OK) {

                if (data.getData() != null) {

                    addImage(data.getData(), true);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            addImage(mClipData.getItemAt(i).getUri(), false);
                        }
                    }
                }
                databaseHelper.insertAll(topHolderModels, bottomHolderModels, true);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void addImage(Uri data, boolean isSingle) {

        ImageHolderModel imageHolderModel = new ImageHolderModel();
        imageHolderModel.setFilePath(getPath(this, data));
        imageHolderModel.setTop(isTopClicked);
        if (isTopClicked) {

            if (!topHolderModels.contains(imageHolderModel)) {

                topHolderModels.add(imageHolderModel);
                topPagerAdapter.notifyDataSetChanged();
                if (isSingle) {

                    viewPagerTop.setCurrentItem(topPagerAdapter.getCount() - 1, false);
                }
            } else {

                Toast.makeText(this, "Duplicates not allowed", Toast.LENGTH_SHORT).show();
            }
        } else {

            if (!bottomHolderModels.contains(imageHolderModel)) {

                bottomHolderModels.add(imageHolderModel);
                bottomPagerAdapter.notifyDataSetChanged();
                if (isSingle) {

                    viewPagerBottom.setCurrentItem(topPagerAdapter.getCount() - 1, false);
                }
            } else {

                Toast.makeText(this, "Duplicates not allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setHoldersData(ArrayList<ImageHolderModel> holderModels) {

        if (holderModels != null && holderModels.size() > 0) {

            MainActivity.this.runOnUiThread(() -> {

                int size = holderModels.size();
                for (int i = 0; i < size; i++) {

                    ImageHolderModel imageHolderModel = holderModels.get(i);
                    if (imageHolderModel.isTop()) {

                        if (!topHolderModels.contains(imageHolderModel)) {

                            topHolderModels.add(imageHolderModel);
                        }
                    } else {

                        if (!bottomHolderModels.contains(imageHolderModel)) {

                            bottomHolderModels.add(imageHolderModel);
                        }
                    }
                }
                topPagerAdapter.setImageHolderModels(topHolderModels);
                bottomPagerAdapter.setImageHolderModels(bottomHolderModels);
                bottomPagerAdapter.notifyDataSetChanged();
                topPagerAdapter.notifyDataSetChanged();
                viewPagerTop.post(() -> viewPagerTop.setCurrentItem(currentTopIndex, false));
                viewPagerBottom.post(() -> viewPagerBottom.setCurrentItem(currentBottomIndex, false));
                ImageHolderModel topModel = topHolderModels.get(currentTopIndex);
                ImageHolderModel bottomModel = bottomHolderModels.get(currentBottomIndex);
                if(topModel.getFavTag().contains(bottomModel.getFilePath()) && bottomModel.getFavTag().contains(topModel.getFilePath())) {

                    toggleFavIcon(true);
                } else {

                    toggleFavIcon(false);
                }
            });
        }
    }

    public void toggleFavIcon(boolean isFav) {

        if (isFav) {

            imgFavourite.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_favorite_filled_white_24dp));
        } else {

            imgFavourite.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_favorite_border_black_24dp));
        }
    }
}
