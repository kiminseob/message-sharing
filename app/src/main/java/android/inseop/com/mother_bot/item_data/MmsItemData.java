package android.inseop.com.mother_bot.item_data;

import android.graphics.Bitmap;

/**
 * Created by 인섭 on 2019-01-23.
 */

public class MmsItemData {
    private String phoneNumber;
    private String body;
    private Bitmap bitmap;
    private String id;
    private int exist;

    public MmsItemData(String phoneNumber, String body , Bitmap bitmap, String id, int exist) {
        this.phoneNumber = phoneNumber;
        this.body = body;
        this.bitmap = bitmap;
        this.id = id;
        this.exist = exist;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }
}
