package org.m.o.getsmsreceive;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

public class JsonSendMessageCustomer {
    private SmsObject obj;

    public JsonSendMessageCustomer(SmsObject obj) {
        this.obj = obj;
    }
    public JsonSendMessageCustomer(){}
    public SmsObject getObj() {
        return obj;
    }

    public void setObj(SmsObject obj) {
        this.obj = obj;
    }

    public static String POST(String url, SmsObject smsObject){
        InputStream inputStream = null;
        String result = "";
        int i=0;
        try {
            // 1. create HttpClient

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", smsObject.getPhone());
            jsonObject.put("message", smsObject.getMessage());
            jsonObject.put("date", smsObject.getDate());
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            System.out.println("---------------------------------- response code :"+statusCode);
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            System.out.println("hahowa Exception --");
            System.out.println("Exception : "+e.getMessage());
            System.out.println("ha fin sala l Exception ---");
        }

        // 11. return result
        System.out.println("-----------------------------> Result POST : "+result.toString());
        return result;
    }

    public String sendResult(String url){
        return POST(url, obj);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }
    public void sendToServer(String u){
        try {
            String query = u;
            String json=null;
            JSONObject jsonObject1 = new JSONObject();
            String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj.getDate());
            jsonObject1.put("phone", obj.getPhone());
            jsonObject1.put("message", obj.getMessage());
            jsonObject1.put("date", str);
            json = jsonObject1.toString();

            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            //JSONObject jsonObject = new JSONObject(result);

            in.close();
            conn.disconnect();
            System.out.println("---------------------------------------------");
            System.out.println(""+result);
            System.out.println("---------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
}
