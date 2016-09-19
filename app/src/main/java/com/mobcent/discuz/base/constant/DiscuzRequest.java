package com.mobcent.discuz.base.constant;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.litesuits.android.log.Log;
import com.mobcent.common.AppHashUtil;
import com.mobcent.discuz.activity.LoginUtils;
import com.mobcent.discuz.base.Tasker;
import com.mobcent.discuz.base.cookie.CookiesManager;
import com.mobcent.discuz.fragments.HttpResponseHandler;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Handler;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscuzRequest extends AsyncTask<Void, Integer, String> implements Tasker{
    private String mUrl;
    private String mBody;
    private HttpResponseHandler mHandler;
    private Vector<String> mFiles;
    private String mMethod = "post";
    public static final String baseUrl = "http://forum.longquanzs.org//mobcent/app/web/index.php?r=";
    // Global instance
    public static  CookieJar COOKIE_JAR = new CookiesManager();
    public static  OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().cookieJar(COOKIE_JAR).build();
    public DiscuzRequest(String url, String body, HttpResponseHandler handler) {
        mUrl = url;
        mBody = body;
        mHandler = handler;
    }

    public DiscuzRequest(String url, String body, HttpResponseHandler handler, String method) {
        this(url, body, handler);
        mMethod = method;
    }

    public DiscuzRequest(String url, Vector<String> files, HttpResponseHandler handler) {
        mUrl = url;
        mFiles = files;
        mHandler = handler;
    }

    public DiscuzRequest(String url, Vector<String> files, HttpResponseHandler handler, String method) {
        this(url, files, handler);
        mMethod = method;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (mFiles != null && mFiles.size() > 0) {
            return FileUploader.uploadFile(mUrl, mFiles);
        } else {
            return sendRequest();
        }
    }

    private String sendRequest() {
        // TODO Auto-generated method stub
        // String request = "type=login&forumKey=BW0L5ISVRsOTVLCTJx&accessSecret=&accessToken=&isValidation=1&password=Mrzl2009&sdkVersion=2.4.0&apphash=85eb3e4b&username=17710275730";
        try {
            Request.Builder builder= new Request.Builder()
                    .url(mUrl.startsWith("http") ? mUrl : baseUrl + mUrl)
                    .addHeader("cache-control", "no-cache");
            if (mMethod.equals("get")) {
                builder = builder.get();
            } else {
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                //RequestBody body = RequestBody.create(mediaType, "topOrder=1&sdkVersion=2.4.3.0&apphash=5038dae8&forumKey=BW0L5ISVRsOTVLCTJx&pageSize=20&filterId=0&filterType=typeid&sortby=new&boardId=0&egnVersion=v2035.2&page=1&isRatio=1&accessToken=&accessSecret=&=");
                //MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
                String bodyString = "";
                if (mMethod == "post") {
                    if (!TextUtils.isEmpty(mBody)) {
                        try {
                            JSONObject obj = new JSONObject(mBody);
                            obj.put("egnVersion","v2035.2");
                            obj.put("forumKey", "BW0L5ISVRsOTVLCTJx");
                            obj.put("sdkVersion", "2.4.0");
                            // "85eb3e4b"
                            obj.put("apphash", AppHashUtil.appHash());
                            obj.put("accessSecret", LoginUtils.getInstance().getAccessSecret());
                            obj.put("accessToken", LoginUtils.getInstance().getAccessToken());
                            Iterator it = obj.keys();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                String value = obj.getString(key);
                                bodyString += "&" + key + "=" + value;
                                //bodyString += "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + value + "\r\n";
                            }
                            //bodyString += "-----011000010111000001101001--";
                        } catch (Exception e) {

                        }
                    }
                }
                if (!TextUtils.isEmpty(bodyString)) {
                    bodyString = bodyString.substring(1);
                }
                RequestBody body = RequestBody.create(mediaType, bodyString);
                builder = builder.post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded");
                        //.addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001");
            }
            Request request = builder.build();
            Log.d("http request", request.url());
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("http result", result);
        if (mHandler == null) return;

        if (!TextUtils.isEmpty(result)) {
            mHandler.onSuccess(result);
        } else {
            mHandler.onFail(result);
        }
    }

    public static byte[] toByteArray(File f) throws IOException {
        if (!f.exists()) {
            throw new FileNotFoundException(f.getName());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }
    @Override
    public void begin() {
        execute();
    }

    @Override
    public void cancel() {
        cancel(true);
    }

    @Override
    public boolean isRunning() {
        return !isCancelled();
    }
}

