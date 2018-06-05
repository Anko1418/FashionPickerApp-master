package application.com.fashionpicker.database.models;

public class FavModel {

    private ImageHolderModel top;
    private ImageHolderModel bottom;

    public ImageHolderModel getTop() {
        return top;
    }

    public void setTop(ImageHolderModel top) {
        this.top = top;
    }

    public ImageHolderModel getBottom() {
        return bottom;
    }

    public void setBottom(ImageHolderModel bottom) {
        this.bottom = bottom;
    }
}
