package currency.home.com.atm;

import android.widget.ImageView;

public class Function {
    String name;
    int imageViewID;

    public Function(String name, int imageViewID) {
        this.name = name;
        this.imageViewID = imageViewID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageViewID() {
        return imageViewID;
    }

    public void setImageView(int imageViewID) {
        this.imageViewID = imageViewID;
    }
}
