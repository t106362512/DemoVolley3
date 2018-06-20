package com.example.user.demovolley3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    int autoRefresh_status = 0;
    int status_mem = 0;
    String getJSON = "http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/getJSON.php";


    Handler handler=  new Handler();
    Runnable myRunnable = new Runnable() {
        public void run() {
            download(getJSON);
            handler.postDelayed(this,1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        download(getJSON);      //取得 JSON 頁面
    }

    private void download(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<HashMap<String, String>> List = new ArrayList<>();
                String ID = null, userid = null, temperature = null, datetime = null;
                try {
                    JSONArray jsonArray = response.getJSONArray("myJSON");
                    int count = jsonArray.length() - 1;     //count 用來倒著顯示
                    while (true) {
                        ID = ((JSONObject) (jsonArray.get(count))).getString("ID");    //到 JSON FORMATTOR 網站 整理 JSON 內容
                        userid = ((JSONObject) (jsonArray.get(count))).getString("userid");
                        temperature = ((JSONObject) (jsonArray.get(count))).getString("temperature");
                        datetime = ((JSONObject) (jsonArray.get(count))).getString("datetime");
                        System.out.println("ID = " + ID + " ,userid = " + userid);     //測試有無抓到的資料
                        System.out.println("temperature = " + temperature + " ,datetime" + datetime);     //測試有無抓到的資料
                        if (ID == null) break;
                        else {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("ID", ID);
                            map.put("userid", userid);
                            map.put("temperature", temperature);
                            map.put("datetime", datetime);
                            List.add(map);
                        }
                        count--;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, List, R.layout.listview_item,
                        new String[]{"ID", "userid", "temperature", "datetime"},
                        new int[]{R.id.IDNUM, R.id.useridNUM, R.id.temp, R.id.date});
                listView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            }
        });
        queue.add(jsObjRequest);
    }

    //=======================================================================設定按鈕動作===========================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 10, 0, "autoRefresh").setIcon(R.drawable.autorefresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 20, 0, "startUpdateData").setIcon(R.drawable.start).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 30, 0, "stopUpdateData").setIcon(R.drawable.stop).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 40, 0, "refleshJSON").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 50, 0, "delJSON").setIcon(R.drawable.garbage).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case 10:
                if (autoRefresh_status == 0) {
                    Toast.makeText(MainActivity.this, "開啟自動更新", Toast.LENGTH_SHORT).show();
                    autoRefresh_status = 1;
                } else {
                    Toast.makeText(MainActivity.this, "關閉自動更新", Toast.LENGTH_SHORT).show();
                    autoRefresh_status = 0;
                }
                if (autoRefresh_status == 1 && status_mem == 1){
                    System.out.println("更新");
                    handler.post(myRunnable);
                }else{
                    System.out.println("停止");
                    handler.removeCallbacks(myRunnable);
                }
                break;
            case 20:
                status_mem = 1;
                changeStatus("1");      //status 為 1 => Raspberry Pi 讀取 data
                Toast.makeText(MainActivity.this, "狀態改為'1'", Toast.LENGTH_SHORT).show();
                break;
            case 30:
                status_mem = 0;
                handler.removeCallbacks(myRunnable);
                changeStatus("0");      //status 為 0 => Raspberry Pi 停止讀取 data
                Toast.makeText(MainActivity.this, "狀態改為'0'", Toast.LENGTH_SHORT).show();
                break;
            case 40:
                ControlPHP.refelsh();        //整理 ID 鍵值
                download(getJSON);
                Toast.makeText(MainActivity.this, "重新載入資料", Toast.LENGTH_SHORT).show();
                break;
            case 50:
                delJSON();
                Toast.makeText(MainActivity.this, "按下刪除按鈕", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //==================================================================================================================================================================
    private void delJSON() {
        final EditText item = new EditText(this);
        new AlertDialog.Builder(MainActivity.this)   //物件建立
                .setTitle("刪除")
                .setMessage("請輸入欲刪除ID")
                .setView(item)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {   //第二個參數決定是哪個按鈕
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            String delNUM = item.getText().toString();
                            strictMode();      //檢測錯誤
                            String r = ControlPHP.del(delNUM);      //呼叫刪除程式
                            System.out.println(r);     //開發人員檢查
                            download(getJSON);
                            //Toast.makeText(MainActivity.this, "刪除", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {   //第二個參數決定是哪個按鈕
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            //Toast.makeText(MainActivity.this, "按下\"取消\"按鈕", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();    //有傳回值才可以這樣串接
    }

    private void changeStatus(String s) {
        strictMode();       //檢測錯誤
        String r = ControlPHP.status(s);    //改變狀態
        System.out.println(r);
    }

    public void strictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskWrites()
                .detectDiskReads()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

}
