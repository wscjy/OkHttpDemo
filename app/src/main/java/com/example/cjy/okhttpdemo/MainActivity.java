package com.example.cjy.okhttpdemo;

import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
                if(data[0] != null && !data[0].isEmpty()) {
                    Log.i("TAG", data[0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, data[0], Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (data[0] == null) {
                    Log.i("TAG", "null");
                } else {
                    Log.i("TAG", "可惜为空");
                }
            }
        }).start();
        if(data[0] != null && data[0].isEmpty()) {
            Log.i("TAG", data[0]);
            Toast.makeText(this, data[0], Toast.LENGTH_SHORT).show();
        } else if (data[0] == null) {
            Log.i("TAG", "null");
        } else {
            Log.i("TAG", "可惜为空");
        }
    }

    public String getData(){
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting";
//        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&" +
//                "method=baidu.ting.search.catalogSug&format=json&callback=&query=%22%E6%" +
//                "BC%94%E5%91%98%E2%80%9D";
        RequestBody body = null;
        try {
            body = new FormBody.Builder()
                    .add("method", URLEncoder.encode("baidu.ting.billboard.billList", "utf-8"))
                    .add("type", URLEncoder.encode("1", "utf-8"))
                    .add("size", URLEncoder.encode("100", "utf-8"))
                    .add("offset", URLEncoder.encode("0", "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", getUserAgent())
                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            Log.i("返回码:", String.valueOf(response.code()));
            if (response.isSuccessful()) {
                String msg = response.body().string();
                Log.i("返回数据为：", msg);
                return msg;
            } else {
                throw new IOException("Unexpected code " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(this);
//                userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4";
                Log.i("TAG1",userAgent);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
                Log.i("TAG2",userAgent);
            }
        } else {
            userAgent = System.getProperty("http.agent");
            Log.i("TAG9",userAgent);
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
