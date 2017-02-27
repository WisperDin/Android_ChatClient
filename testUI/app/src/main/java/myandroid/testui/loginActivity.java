package myandroid.testui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {

    Button btn_login;
    Button btn_reg;
    EditText edt_userName;
    EditText edt_PW;
    EditText edt_ID;

    boolean InitUserMsg(String userName,String pw, int id) {
        SckCln socket =globalvalue.GetSocket();
        if (socket==null){//socket为空则表示连接不成功
            return false;
        }
        String dstMsg = JSONmarshall.marshalMsgR(userName, null, null, pw, id, JSONmarshall.MarShalMode.InitUserFra);
        System.out.println(dstMsg);
        socket.sendMsg(dstMsg);
        return true;
    }
    private  void SwitchAct(String UserName){
        Intent intent = new Intent(loginActivity.this, chatActivity.class);
        intent.putExtra("UserName",UserName);
        startActivity(intent);
        this.finish();
    }
/*
    public void UpdloginUI(boolean isLogin) {
        //连接之后就锁定输入栏，使按钮变成DISCON
        edt_userName.setEnabled(!isLogin);
        edt_ID.setEnabled(!isLogin);
        edt_PW.setEnabled(!isLogin);
    }*/
    SckCln cln;
    final static String serveIP="101.201.71.152";
    final static int servePort=6666;
    private void connectServe(){
        cln = new SckCln(serveIP, servePort, MsgHandle.GetMsgHandle());
        cln.start();
        MsgHandle.socket = cln;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectServe();
        MsgHandle.currentAct=this;
        edt_userName=(EditText) findViewById(R.id.edt_userName);
        edt_PW=(EditText) findViewById(R.id.edt_Pw);
        edt_ID=(EditText) findViewById(R.id.edt_ID);
        btn_reg=(Button)findViewById(R.id.btn_Init);
        btn_login=(Button)findViewById(R.id.btn_login);
        //UpdloginUI(false);//先锁定这些框框
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edt_userName.getText())&&!TextUtils.isEmpty(edt_PW.getText())){
                    String UserName=edt_userName.getText().toString();
                    String Pw=edt_PW.getText().toString();
                    int id;
                    if (!TextUtils.isEmpty(edt_ID.getText())) {
                        id = Integer.parseInt(edt_ID.getText().toString());
                    }
                    else{
                        id=-1;
                    }
                        if(InitUserMsg(UserName,Pw,id))
                            SwitchAct(UserName);
                }
                else{
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edt_userName.getText())) {
                    String UserName=edt_userName.getText().toString();
                    if (InitUserMsg(UserName,"",-1))
                        SwitchAct(UserName);
                }
                else{
                    Toast.makeText(getApplicationContext(), "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
