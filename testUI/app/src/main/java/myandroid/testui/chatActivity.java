package myandroid.testui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.os.Handler;

public class chatActivity extends AppCompatActivity {

    EditText edt_msg;
    EditText edt_taruser;
    Button btn_send;
    ListView listView;
    List<String> msgList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public void showMsg(String arg_msg) {
        if (arg_msg == null || arg_msg.isEmpty()) {
            return;
        }
        msgList.add(arg_msg + "\t " + sdf.format(new Date()));
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(msgList.size());
    }

    private void SendMsg(String UserName){
        String tarUserName = "";
        if (!TextUtils.isEmpty(edt_taruser.getText())) {
            tarUserName = edt_taruser.getText().toString();
            tarUserName = tarUserName.replace('　', ' ');
            tarUserName = tarUserName.replace('，', ',');
            tarUserName = tarUserName.replaceAll(", ", ",");
            tarUserName = tarUserName.replaceAll(" ,", ",");
        } else {
            Toast.makeText(getApplicationContext(), "目标用户不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String[] dstList = null;
        dstList = tarUserName.substring(0, tarUserName.length()).split(",");

        String msg = "";
        if (!TextUtils.isEmpty(edt_msg.getText())) {
            msg = edt_msg.getText().toString();
        } else {
            Toast.makeText(getApplicationContext(), "信息内容不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        String dstMsg = JSONmarshall.marshalMsgR(UserName, dstList, msg, null, -1, JSONmarshall.MarShalMode.ChatFra);
        System.out.println(dstMsg);

        String dstL = "";
        int i;
        for (i = 0; i < dstList.length - 1; i++) {
            dstL += dstList[i] + ",";
        }
        dstL += dstList[i];
        showMsg("To " + dstL + " : " + msg);
        globalvalue.GetSocket().sendMsg(dstMsg);///////////////////////////////////////////发送信息
        edt_msg.getText().clear();
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    boolean isExit=false;
    public void exit(){
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "Press again to exit...", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);//延迟两秒后执行
        } else {
            //2s内如果再次按了返回键就会进入这个分支

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            this.finish();
            globalvalue.GetSocket().Close();//还要断开连接
            System.exit(0);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        MsgHandle.currentAct=this;
        edt_msg=(EditText) findViewById(R.id.edt_msg);
        edt_taruser=(EditText) findViewById(R.id.edt_taruser);
        btn_send=(Button)findViewById(R.id.btn_send);

        listView = (ListView) findViewById(R.id.msgview);
        adapter = new ArrayAdapter<String>(this, R.layout.msg_item_view, R.id.receivedMsg, msgList);
        listView.setAdapter(adapter);//为控件绑定变量adapter
        Intent intent = getIntent();
        final String UserName;
        if (intent.getExtras().get("UserName") != null){
            UserName= intent.getExtras().getString("UserName");
        }
        else{
            UserName="";
        }
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserName!="")
                    SendMsg(UserName);
                else{
                    Toast.makeText(getApplicationContext(), "未知错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
