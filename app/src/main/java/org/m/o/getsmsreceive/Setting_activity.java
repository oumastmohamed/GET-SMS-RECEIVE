package org.m.o.getsmsreceive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class Setting_activity extends AppCompatActivity {
    EditText editLink, editBlock;
    DBConnections dbSMS=null;
    DBConnectionSetting dbSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_activity);
        editLink = (EditText) findViewById(R.id.editTextLink);
        editBlock = (EditText) findViewById(R.id.editTextBlock);
        dbSMS = new DBConnections(this);
        dbSetting = new DBConnectionSetting(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        Intent i = getIntent();
        String l ="", b = "";
        l= i.getStringExtra("link");
        b = i.getStringExtra("block");
        editLink.setText(l);editBlock.setText(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mymenu1 = getMenuInflater();
        mymenu1.inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId()== R.id.action_save) {
            String link = editLink.getText().toString();
            String block = editBlock.getText().toString();
            if(!link.equals("")){
                dbSetting.insertLinkAndNumberBlock(link, block);
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            }else {
                Toast.makeText(this, "Please entre the link !", Toast.LENGTH_LONG+4).show();
                return true;
            }
            return true;
        }else if(item.getItemId() == R.id.action_cancel){
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
