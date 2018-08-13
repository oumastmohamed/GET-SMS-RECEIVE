package org.m.o.getsmsreceive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public DBConnections dbSMS=null;
    public DBConnectionSetting dbSetting=null;
    TextView txt1;
    SmsObject smsObjectMsg;
    public JsonSendMessageCustomer jsonSendMessageCustomer;
    public String result="Erreur";
    public static String text="";
    public static Context ctx = null;
    public static String link=null, block=null;


    public boolean checkLinkExist(){
        if(dbSetting.getSettingLink() == null){
            dbSetting.insertLinkAndNumberBlock("","");
        }
        link = dbSetting.getSettingLink();
        block = dbSetting.getNumberBlock();
        //to eliminate space
        link = link.replace("\\s+","");
        block = block.replaceAll("\\s+","");
        if(link.equals("") || link == null){
            return false;
        }
        return true;
    }

    //broadcast receiver for receive messages and send to server
    BroadcastReceiver smsReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Build.VERSION.SDK_INT < 19) {
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                    SmsMessage[] msgs;
                    String sender;
                    if (bundle != null) {
                        String p="p", m="m";
                        //---retrieve the SMS message received---
                        try {
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            msgs = new SmsMessage[pdus.length];
                            for (int i = 0; i < msgs.length; i++) {
                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                sender = msgs[i].getOriginatingAddress();
                                String messageBody = msgs[i].getMessageBody();
                                //Toast.makeText(getApplicationContext(), messageBody, Toast.LENGTH_LONG).show();
                                // do things here
                                p=sender;
                                m=messageBody;
                            }
                            sendToServer(p, m, new Date());

                        } catch (Exception e) {
                            Log.d("Exception caught",e.getMessage());
                        }
                    }
                }
            } else {
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    String p ="p", m = "m";
                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        String messageBody = smsMessage.getMessageBody();
                        String sender = smsMessage.getOriginatingAddress();
                        // do things here
                        //Toast.makeText(getApplicationContext(), sender+" / "+messageBody, Toast.LENGTH_LONG).show();
                        // send to server
                        p = sender;
                        m = messageBody;
                    }

                    sendToServer(p, m, new Date());
                }
            }

        }};
    public boolean checkLinkrunning(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            try{
                URL url = new URL(link);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3 * 1000);
                urlc.connect();
                if(urlc.getResponseCode() == 200){
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            }catch (MalformedURLException e1){
                return false;
            }catch (IOException e){
                return false;
            }
        }
        return false;
    }
    public boolean isConnected(){
        final ConnectivityManager connMgr = (ConnectivityManager)
                MainActivity.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            return true;
        } else if (mobile.isConnectedOrConnecting ()) {
            return true;
        } else {
            return false;
        }
    }

    public void sendToServer(String sender, String messageBody, Date date){
        if(isConnected()) {
            try {
                if (sender.contains("+212")) {
                    sender = "0" + sender.substring(4);
                }
                smsObjectMsg = new SmsObject(sender, messageBody, date);
                String[] tab = block.split(";");
                if (ischeckNumber(tab, smsObjectMsg.getPhone())) {
                    Toast.makeText(getApplicationContext(), "This number is blocked", Toast.LENGTH_SHORT).show();
                } else{
                    jsonSendMessageCustomer = new JsonSendMessageCustomer(smsObjectMsg);
                    jsonSendMessageCustomer.sendToServer(link);//https://oumastmohamed15.000webhostapp.com/
                    //result= jsonSendMessageCustomer.sendResult("https://www.moteur.ma/fr/ftp/get_sms_adv");//https://www.moteur.ma/fr/ftp/get_sms_adv
                    //System.out.println("-----------------------> voila le resultat : "+result.toString());
                    //insert date
                    dbSMS.insertDateOfLastSMS(new Date().getTime());
                    refreshT(date + ":\nto " + sender + ": " + messageBody);
                    Toast.makeText(getApplicationContext(), "Sent to server", Toast.LENGTH_SHORT).show();
                    System.out.println("sift l server");
                }

            } catch (Exception e) {
                System.out.println("Error of sending response to server ==> " + e.getMessage());
            }
        }
    }


    public void sleeping(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void requestPermissionInternet(){
        // important for connection to server
        if(android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //for refresh veiw
    public void refreshT(String text){
        this.text=text+"\n\n"+this.text;
        txt1.setText(this.text.toString());
        txt1.setMovementMethod(new ScrollingMovementMethod());
    }
    public void TextResume(){
        txt1.setText(this.text.toString());
        txt1.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissionInternet();

        dbSMS = new DBConnections(this);
        dbSetting = new DBConnectionSetting(this);
        IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(990);
        registerReceiver(smsReceiver, intentFilter);
        txt1 = (TextView) findViewById(R.id.textViewlist);
        this.ctx = MainActivity.this;
    }

    @Override
    protected void onResume(){
        super.onResume();
        TextResume();
        if(!checkLinkExist()){
            //link = dbSetting.getSettingLink();
            //block = dbSetting.getNumberBlock();
            Intent intent = new Intent(this, Setting_activity.class);
            intent.putExtra("link", link);
            intent.putExtra("block", block);
            this.finish();
            this.startActivity(intent);
        }else {
            Long lastTime= new Date().getTime();
            ArrayList<SmsObject> smsObjects = fetchInbox();
            if(dbSMS.getLastrecord() != null){
                lastTime = dbSMS.getLastrecord();
            }
            //Convert time to date
            Date lastDate = new Date(lastTime);

            String[] tab = block.split(";");
            for(SmsObject sms : smsObjects){
                sms.setPhone(delCountryNum(sms.getPhone()));
                if(lastDate.before(sms.getDate())){
                    sendToServer(sms.getPhone(), sms.getMessage(), sms.getDate());
                    sleeping(8);
                }
            }
        }
    }

    public String delCountryNum(String num){
        String cn = "+212";
        if(num.contains(cn)){
            num = "0"+num.substring(4);
        }
        return num;
    }
    public boolean ischeckNumber(String[] tab, String num){

        for(String n : tab){
            n = delCountryNum(n);
            if(num.equals(n)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<SmsObject> fetchInbox(){
        String id, address, body;
        Long date;
        ArrayList<SmsObject> sms = new ArrayList<SmsObject>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor=getContentResolver().query(uri, new String[]{"_id", "address", "date", "body"},null,null, null);

        while(cursor.moveToNext()){
                id = cursor.getString(0);
                address = cursor.getString(1);
                body = cursor.getString(3);
                date = cursor.getLong(2);
                sms.add(new SmsObject(address, body, new Date(date)));
        }
        return sms;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId()== R.id.action_setting) {
            Intent in = new Intent(this, MainActivity.class);
                Intent intent = new Intent(this, Setting_activity.class);
                intent.putExtra("link", link);
                intent.putExtra("block", block);
                this.finish();
                this.startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}