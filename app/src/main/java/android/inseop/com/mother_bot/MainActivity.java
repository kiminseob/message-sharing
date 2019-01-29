package android.inseop.com.mother_bot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_READ_PERMISSON=1;
    private static final String TAG = "PhoneState";

    private int permissonCheck;
    private String userProfile;
    private Bitmap bitmap;

    EditText phoneTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SMS 읽기 권한이 부여되어 있는지 확인
        permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        //  제조사, 모델명 출력
        Log.i(TAG, "MANUFACTURER = " + Build.MANUFACTURER);
        Log.i(TAG, "MODEL = " + Build.MODEL);

        phoneTx = (EditText) findViewById(R.id.phone_number);
        Button button = (Button) findViewById(R.id.button);
        ImageView imageV = (ImageView)findViewById(R.id.profile);
        TextView textV = (TextView)findViewById(R.id.name);

        final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);

        try {
            Intent intent = getIntent();
            String userName = intent.getExtras().getString("nickname");
            userProfile = intent.getExtras().getString("url");
            mThread.start();
            mThread.join();
            imageV.setImageBitmap(bitmap);
            Log.d("username",userName);
            Log.d("userProfile",userProfile);
            textV.setText(userName + " 님, 안녕하세요.");
        }catch (Exception e){
            e.printStackTrace();
        }
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                // SMS 읽기 권한이 부여되어 있는지 확인
                permissonCheck= ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
                if(permissonCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_SMS}, SMS_READ_PERMISSON);
                }
                if(permissonCheck == PackageManager.PERMISSION_GRANTED){
                    int id = rg.getCheckedRadioButtonId();
                    //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                    RadioButton rb = (RadioButton) findViewById(id);

                    Intent intent = new Intent(getApplicationContext(), AdapterListview.class);
                    /*
                        FLAG_ACTIVITY_NEW_TASK : activity 밖에서 start액티비티 호출하는 경우 필요
                        FLAG_ACTIVITY_SINGLE_TOP : 기존 액티비티를 재활용. (전화번호 입력 유지시키기 위함.)
                        FLAG_ACTIVITY_CLEAR_TOP : 스택에 쌓여있는 기존에 사용하던 액티비티를 전부 제거한다.
                     */
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("phone", phoneTx.getText().toString());
                    intent.putExtra("select", rb.getText().toString());
                    getApplicationContext().startActivity(intent);
                }
            }
        });
    }

    Thread mThread = new Thread(){
        @Override
        public void run(){
            try{
                URL url = new URL(userProfile);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true); //서버로 부터 응답 수신
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap  = BitmapFactory.decodeStream(is);

            }catch(IOException e){
                e.printStackTrace();
            }
        }

    };

}
