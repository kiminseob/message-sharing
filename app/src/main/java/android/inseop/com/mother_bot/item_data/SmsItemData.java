package android.inseop.com.mother_bot.item_data;

/**
 * Created by 인섭 on 2019-01-23.
 */

public class SmsItemData {
    private String strMessage;
    private String strDate;

    public SmsItemData(String strMessage, String strDate) {
        this.strMessage = strMessage;
        this.strDate = strDate;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }
}
