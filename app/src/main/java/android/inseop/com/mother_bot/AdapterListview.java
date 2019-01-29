package android.inseop.com.mother_bot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inseop.com.mother_bot.adapter.MmsListAdapter;
import android.inseop.com.mother_bot.adapter.SmsListAdapter;
import android.inseop.com.mother_bot.item_data.MmsItemData;
import android.inseop.com.mother_bot.item_data.SmsItemData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import static android.content.ContentValues.TAG;

/**
 * Created by 인섭 on 2019-01-23.
 */

public class AdapterListview extends AppCompatActivity{

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSON=1;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.KOREA);
    
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<SmsItemData> smsData = new ArrayList<>();
    private ArrayList<MmsItemData> mmsData = new ArrayList<>();

    private ContentResolver cr;
    private MmsListAdapter mmsAdapter;

    private int all_cnt;
    private int complet_cnt;
    private String phone;
    private String select_type;
    private boolean runFlag = true;

    ListView listview;
    TextView phoneTx;
    TextView imageDownTx;
    Button shareBtn;
    Button downBtn;

    private final MyListviewHandler myListviewHandler = new MyListviewHandler(this);
    private final MyImgDownStateHandler myImgDownStateHandler = new MyImgDownStateHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smslist);
        SmsListAdapter smsAdapter;
        cr = getContentResolver();
        runFlag = true;

        phoneTx = (TextView) findViewById(R.id.phone_number);
        downBtn = (Button)findViewById(R.id.down_button);
        shareBtn = (Button)findViewById(R.id.share_button);
        imageDownTx = (TextView)findViewById(R.id.image_down_state);
        listview = (ListView)findViewById(R.id.listView);
        
        Intent intent = getIntent();

        phone = intent.getExtras().getString("phone");
        select_type = intent.getExtras().getString("select");

        downBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                int permissonCheck= ContextCompat.checkSelfPermission(AdapterListview.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(permissonCheck == PackageManager.PERMISSION_GRANTED) {
                    if (listview.getCheckedItemCount() == 0) {
                        Toast.makeText(getApplicationContext(), "이미지를 선택해주세요.", Toast.LENGTH_LONG).show();
                    } else {
                        imageDownSelect_dialog();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "사진 저장/읽기 권한 없음", Toast.LENGTH_SHORT).show();
                    if(ActivityCompat.shouldShowRequestPermissionRationale(AdapterListview.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(), "사진 저장/읽기 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AdapterListview.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSON);
                    }else{
                        ActivityCompat.requestPermissions(AdapterListview.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSON);
                    }
                }
            }
        });
        
        shareBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                    if(select_type.equals("MMS 사진")){
                    if(mmsData.size()==0){
                        Toast.makeText(getApplicationContext(),"공유할 이미지가 없습니다.",Toast.LENGTH_LONG).show();
                    }
                    if(mmsData.size()>0){
                        if(listview.getCheckedItemCount()==0){
                            Toast.makeText(getApplicationContext(),"공유할 이미지를 선택해주세요.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            SparseBooleanArray boolArr = listview.getCheckedItemPositions();
                            int select_allExist=1;
                            for (int i = 0; i < mmsData.size(); i++) {
                                if(boolArr.get(i)) {
                                    if(mmsData.get(i).getExist()==0){
                                        select_allExist=0;
                                    }
                                }
                            }
                            if(select_allExist==0){
                                Toast.makeText(getApplicationContext(),"다운로드 되지 않은 사진은 공유할 수 없습니다.",Toast.LENGTH_LONG).show();
                            }else{
                                startShareIntent();
                            }
                        }
                    }
                }
                else if(select_type.equals("SMS 메시지")) {
                    if(smsData.size()==0){
                        Toast.makeText(getApplicationContext(),"공유할 메시지가 없습니다.",Toast.LENGTH_LONG).show();
                    }
                    if(smsData.size()>0){
                        if(listview.getCheckedItemCount()==0){
                            Toast.makeText(getApplicationContext(),"공유할 메시지를 선택해주세요.",Toast.LENGTH_LONG).show();
                        }else {
                             Log.d(TAG,"체크박스 체크 위치 : "+listview.getCheckedItemPositions());
                            SparseBooleanArray boolArr = listview.getCheckedItemPositions();

                            String message = "❤ [" + phone + "]에게 받은 문자입니다. ❤\n\n";
                            for (int i = 0; i < smsData.size(); i++) {
                                if(boolArr.get(i)) {
                                    message += "📧 ."+smsData.get(i).getStrMessage() + "\n" + smsData.get(i).getStrDate() + "\n\n";
                                }
                            }
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, message);
                            intent.setPackage("com.kakao.talk");
                            startActivity(intent);
                        }
                    }
                }
                else{
                    if(mmsData.size()==0){
                        Toast.makeText(getApplicationContext(),"공유할 메시지가 없습니다.",Toast.LENGTH_LONG).show();
                    }
                    if(mmsData.size()>0) {
                        if(listview.getCheckedItemCount()==0){
                            Toast.makeText(getApplicationContext(),"공유할 메시지를 선택해주세요.",Toast.LENGTH_LONG).show();
                        }else {
                            Log.d(TAG,"체크박스 체크 위치 : "+listview.getCheckedItemPositions());
                            SparseBooleanArray boolArr = listview.getCheckedItemPositions();

                            String message = "❤ [" + phone + "]에게 받은 문자입니다. ❤\n\n";
                            for (int i = 0; i < mmsData.size(); i++) {
                                if(boolArr.get(i)) {
                                    message +="📧 ."+mmsData.get(i).getBody() + "\n\n";
                                }
                            }
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, message);
                            intent.setPackage("com.kakao.talk");
                            startActivity(intent);
                        }
                    }
                }
            }
        });
        

        if(select_type.equals("MMS 사진") || select_type.equals("MMS 메시지")) {
            setIdList();
            all_cnt = idList.size();
            //mms생성
            mmsThread.start();
        }
        else if(select_type.equals("SMS 메시지")) {
            //sms생성
            getSmsList();
            phoneTx .setText("전화번호: "+phone+"\n총 "+ all_cnt+"개 중, "+complet_cnt+"개 검색");
            smsAdapter = new SmsListAdapter(smsData);
            listview.setAdapter(smsAdapter);
            for(int i=0; i<smsAdapter.getCount(); i++){
                listview.setItemChecked(i,true);
            }
            shareBtn.setVisibility(View.VISIBLE);
        }

    }
    void imageDownSelect_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 저장");
        builder.setMessage("선택된 이미지를 다운하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Thread imageDownTread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "mother";
                                    SparseBooleanArray boolArr = listview.getCheckedItemPositions();
                                    Log.d(TAG,"path:"+path);
                                    int cnt=0;
                                    Message msg;
                                    ArrayList<MmsItemData> tempmmsData = new ArrayList<>();
                                    tempmmsData.addAll(mmsData);
                                    for(int i=0; i<tempmmsData.size();i++){
                                        if(!runFlag){break;}
                                        if( boolArr.get(i)){
                                            cnt++;
                                            msg = myImgDownStateHandler.obtainMessage();
                                            msg.arg1 = cnt;
                                            SaveBitmapToFileCache(tempmmsData.get(i).getBitmap(),path, tempmmsData.get(i).getId());
                                            tempmmsData.get(i).setExist(1);
                                            myImgDownStateHandler.sendMessage(msg);
                                            listview.deferNotifyDataSetChanged();
                                        }
                                    }

                                }catch (Exception e){
                                    Log.e("smslist","error UI thread",e);
                                }
                            }
                        });

                        imageDownTread.start();
                        downBtn.setVisibility(View.INVISIBLE);
                        shareBtn.setVisibility(View.INVISIBLE);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    public void startShareIntent() {
        SparseBooleanArray boolArr = listview.getCheckedItemPositions();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "mother";
        File directory;
        ArrayList<Uri> files = new ArrayList<>();

        for(int i=0;i<listview.getCheckedItemCount();i++) {
            if(boolArr.get(i)){
                directory = new File(path,phone+"_"+mmsData.get(i).getId()+".png");
                // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
                files.add( FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, directory) );
            }
        }

        final Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        // set flag to give temporary permission to external app to use your FileProvider
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, files);

        shareIntent.setType("image/*");

        // validate that the device can open your File!
        PackageManager pm = getApplicationContext().getPackageManager();
        if (shareIntent.resolveActivity(pm) != null) {
            startActivity(shareIntent);
        }
    }
    private void SaveBitmapToFileCache(Bitmap bitmap, String path, String id) {

        // First Create Directory
        File dir = new File(path);
        Boolean dirExist = dir.exists();
        if(!dirExist){ dirExist = dir.mkdir(); }
        if(dirExist) {
            File outputFile = new File(path, phone + "_" + id + ".png");
            FileOutputStream out = null;

            try {
                //outputFile.createNewFile();
                out = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyListviewHandler extends Handler{

        AdapterListview activity;

        private MyListviewHandler(AdapterListview activity){
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg){
            activity.listviewHandler(msg);
        }

    }
    private static class MyImgDownStateHandler extends Handler{

        AdapterListview activity;

        private MyImgDownStateHandler(AdapterListview activity){
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg){
            activity.imgDownStateHandler(msg);
        }

    }

    private void listviewHandler(Message msg){

            phoneTx .setText("전화번호: "+phone+"\n총 "+ all_cnt+"개 중, "+msg.arg1+"개 검색\n"+"검색 결과: "+mmsData.size()+"개");

            //mms 사진 존재하면
            if(msg.arg2==1){
                mmsAdapter = new MmsListAdapter(mmsData);
                mmsAdapter.notifyDataSetChanged();
                listview.setAdapter(mmsAdapter);
             }
             //mms 메시지 존재하면
            else if(msg.arg2==2){
                mmsAdapter = new MmsListAdapter(mmsData);
                mmsAdapter.notifyDataSetChanged();
                listview.setAdapter(mmsAdapter);
            }

             if(mmsAdapter!=null  && all_cnt==msg.arg1){
                Log.d(TAG,"어댑터 사이즈 : "+mmsAdapter.getCount());
                 for(int i=0; i<mmsAdapter.getCount(); i++) {
                    listview.setItemChecked(i, true);

                 }
                 shareBtn.setVisibility(View.VISIBLE);
                 if(select_type.equals("MMS 사진")) downBtn.setVisibility(View.VISIBLE);
             }
    }

    Thread mmsThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try{
                int cnt=0;
                String mmsId;
                Message msg;

                //mms 메시지 아이디가 존재한다면,
                if(all_cnt>0){
                    while(cnt<all_cnt && runFlag){
                        mmsId = idList.get(cnt);
                        cnt++;
                        complet_cnt = cnt;

                        int exist = getMmsList(mmsId,select_type);
                        msg = myListviewHandler .obtainMessage();
                        msg.arg1 = complet_cnt;
                        msg.arg2 = exist;
                        myListviewHandler .sendMessage(msg);

                    }
                }
             }catch (Exception e){
                Log.e("smslist","error UI thread",e);
            }
        }
    });
   
    private void imgDownStateHandler(Message msg){

        imageDownTx.setText("선택된 "+ listview.getCheckedItemCount()+"개 중, "+msg.arg1+"개 다운");
        mmsAdapter.notifyDataSetChanged();

        if(listview.getCheckedItemCount()==msg.arg1){
            downBtn.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.VISIBLE);
        }

    }


    public int getMmsList(String mmsId, String select_type){
        try {
            Bitmap bitmap = null;
            int text_exist=0;
            MmsItemData mmsItemData;
            String mmsPhoneNumber = getMmsPhoneNumber(mmsId);

            if (mmsPhoneNumber.equals(phone)) {
                Log.d(TAG, "getmmsphone number = " + mmsPhoneNumber);
                Uri partUri = Uri.parse("content://mms/part");
                Cursor cur2 = cr.query(partUri, null, "mid=?", new String[]{mmsId}, null);

                if (cur2.moveToFirst()) {
                    do {
                        String type = cur2.getString(cur2.getColumnIndexOrThrow("ct"));
                        String partId = cur2.getString(cur2.getColumnIndexOrThrow("_id"));
                        if(select_type.equals("MMS 메시지")) {
                            if (type.equals("text/plain")) {
                                Log.d(TAG, "MMS 메시지 존재 " + type);
                                String body = getMmsBody(cur2);
                                mmsItemData = new MmsItemData(mmsPhoneNumber, body, null, partId, 0);
                                mmsData.add(mmsItemData);
                                text_exist = 2;
                            }
                        }else if(select_type.equals("MMS 사진")) {

                            if ("image/jpeg".equals(type) || "image/bmp".equals(type) ||
                                    "image/gif".equals(type) || "image/jpg".equals(type) || "image/png".equals(type)) {
                                bitmap = getMmsImage(partId);
                                Log.d(TAG, "bitmap : " + bitmap + ", id : " + partId + ", type : " + type);
                                // First Create Directory
                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "mother";
                                File dir = new File(path, phone + "_" + partId + ".png");
                                if (dir.exists()) {
                                    Log.d(TAG, "이미지 존재 : " + dir.getPath());
                                    mmsItemData = new MmsItemData(mmsPhoneNumber, null, bitmap, partId, 1);
                                } else {
                                    mmsItemData = new MmsItemData(mmsPhoneNumber, null, bitmap, partId, 0);
                                }
                                mmsData.add(mmsItemData);
                            }
                        }
                    } while (cur2.moveToNext());
                }
                cur2.close();
             }
            if(select_type.equals("MMS 사진")) {
                if (bitmap != null) {
                    return 1;
                } else {
                    return 0;
                }
            }else{
                if(text_exist==2){
                    return 2;
                }else{
                    return 0;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    private Bitmap getMmsImage(String _id) {
        Uri partURI = Uri.parse("content://mms/part/" + _id);
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getContentResolver().openInputStream(partURI);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) { Log.d(TAG,"bitmap getmmsimage error",e); }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) { Log.d(TAG,"bitmap getmmsimage error",e); }
            }
        }
        return bitmap;
    }

    public void setIdList(){
        try {
            String[] projection = new String[]{"_id"};
            Uri allMessage = Uri.parse("content://mms/inbox");
            Cursor cur = cr.query(allMessage, projection, null, null, null);
            int count = cur.getCount();

            Log.d( TAG , "MMS count = " + count);
            if (cur.moveToFirst()) {
                do {
                    String mmsId = cur.getString(cur.getColumnIndexOrThrow("_id"));
                    idList.add(mmsId);
                } while (cur.moveToNext());
            }
            cur.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getMmsPhoneNumber(String id){
        String uriStr = MessageFormat.format("content://mms/{0}/addr", id);

        Uri phoneNumUri = Uri.parse(uriStr);
        Cursor cur = cr.query(phoneNumUri , null, "msg_id=?", new String[]{id}, null);
        String phoneNumber = null;

        if(cur.moveToFirst()){
            do {

                String address = cur.getString(cur.getColumnIndexOrThrow("address"));
                Log.d(TAG,"adress = " + address);
                if (address != null) {
                    phoneNumber = address.replace("-", "");
                }
            } while (cur.moveToNext());
        }
        cur.close();
        return phoneNumber;
    }
    public String getMmsBody(Cursor cur){
        try {
            StringBuilder sb = new StringBuilder();
            String partId = cur.getString(cur.getColumnIndexOrThrow("_id"));
            String data = cur.getString(cur.getColumnIndexOrThrow("_data"));
            if (data != null) {
                Uri partUri = Uri.parse("content://mms/part/" + partId);
                InputStream inputStream = cr.openInputStream(partUri);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String temp = bufferedReader.readLine();

                    while (temp != null) {
                        sb.append(temp);
                        temp = bufferedReader.readLine();
                    }
                    inputStream.close();
                }
                return sb.toString();
            } else {
                return cur.getString(cur.getColumnIndexOrThrow("text"));
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void getSmsList() {
        try {
            Uri allMessage;
            Cursor cur;
            SmsItemData oItem;

            if(Build.MANUFACTURER.equals("samsung")) {
                allMessage = Uri.parse("content://sms/inbox");
                cur = cr.query(allMessage, null, null, null, null);
                //Cursor c = cr.query(allMessage, new String[] { "_id", "thread_id", "address", "person", "date", "body" }, null, null, "date DESC");
            }else{
                allMessage = Uri.parse("content://sms/inbox");
                cur = cr.query(allMessage, null, "address=?", new String[]{phone}, null);
            }

            if (cur.moveToFirst()){
                all_cnt = cur.getCount();
                int cnt=0;
                Log.d(TAG, "SMS count = " + all_cnt);
                do{
                    cnt++;
                    String row = cur.getString(cur.getColumnIndex("address"));
                    String msg = cur.getString(cur.getColumnIndex("body"));
                    long long_date = cur.getLong(cur.getColumnIndex("date"));
                    String date = formatter.format(new Date(long_date));

                    if(Build.MANUFACTURER.equals("samsung")) {
                        if(row.equals(phone)) {
                            oItem = new SmsItemData(msg, date);
                            oItem.setStrMessage(msg);
                            oItem.setStrDate(date);
                            smsData.add(oItem);
                        }
                    }else{
                        oItem = new SmsItemData(msg, date);
                        oItem.setStrMessage(msg);
                        oItem.setStrDate(date);
                        smsData.add(oItem);
                    }

                    Log.d(TAG, "SMS Phone: " + row + " / Mesg: " + msg +" / Date: " + date);
                    complet_cnt=cnt;
                }
                while (cur.moveToNext()) ;
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(this, "메인으로 이동합니다", Toast.LENGTH_SHORT).show();
        runFlag = false;
        smsData.clear();
        mmsData.clear();
        idList.clear();

        super.onBackPressed();
    }

}
