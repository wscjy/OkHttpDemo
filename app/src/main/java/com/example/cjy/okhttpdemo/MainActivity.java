package com.example.cjy.okhttpdemo;

import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient mOkHttpClient = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] data = {null};
        new Thread(new Runnable() {
            @Override
            public void run() {
                data[0] = getData();
            }
        }).start();
        if(data[0] != null && data[0].isEmpty()) {
            Log.i("TAG", data[0]);
            Toast.makeText(this, data[0], Toast.LENGTH_SHORT).show();
        } else if (data[0] == null) {
            Log.i("TAG", "null");
        } else {
            Log.i("TAG", "空");
        }
    }

    public String getData(){
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting" +
                ".billboard.billList&type=1&size=100&offset=0";
//        Request request = new Request.Builder().url(url).removeHeader("User-Agent").addHeader("User-Agent",
//                getUserAgent()).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserAgent() {
        Log.i("TAG","测试git提交");
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(this);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
