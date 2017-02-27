package myandroid.testui;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by asus1 on 2016/12/13.
 */

public class  MsgHandle {
    public static SckCln socket;
    /*
    static void GetSckCln(SckCln object){
        socket=object;
    }*/
    public static AppCompatActivity currentAct;

    private static void showMsg_LV(String arg_msg) {
        chatActivity act;
        try{
            act = ((chatActivity) currentAct);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        if (arg_msg == null || arg_msg.isEmpty()) {
            return;
        }
        act.showMsg(arg_msg);
    }
    private static void showMsg_Toast(String arg_msg) {
        if (arg_msg == null || arg_msg.isEmpty()) {
            return;
        }
        Toast.makeText(currentAct.getApplicationContext(), arg_msg,
                Toast.LENGTH_SHORT).show();
    }
    private static void  SendFbFrame() {
        System.out.println("正在构造反馈帧");
        try {
            String dstMsg = JSONmarshall.marshalMsgR(null, null, "SUCCESS", null, -1, JSONmarshall.MarShalMode.FeedBackFra);
            System.out.println(dstMsg);
            socket.sendMsg(dstMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static  Handler GetMsgHandle(){
        Handler msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                String peerMsg = msg.getData().getString("peerData");//生信息，即未被处理
                //虽然做的都是一样，但先分好类
                switch (msg.what) {
                    case 4030: // connected,连接成功后设置注册，登录页面的
                        showMsg_Toast(peerMsg);
                        globalvalue.SetSocket(socket);
                        return;
                    case 4040: { // disconnected,,应该是超时啊，地址不正确的各种情况
                        showMsg_Toast(peerMsg);
                        return;
                    }
                    case 4020: {//connecting
                        showMsg_Toast(peerMsg);
                        return;
                    }
                }
                System.out.println(peerMsg + "test");
                if (peerMsg == null || peerMsg.isEmpty() || peerMsg.length() <= 0) {
                    showMsg_Toast("got empty/null message");
                    return;
                }
                Log.d("QNearFE", peerMsg);
                String dstMsg = "";
                try {
                    JSONObject m = new JSONObject(peerMsg);
                    int MT = m.getInt("msgType");
                    //json解码
                    switch (MT)//对收到反馈帧或者信息帧有两种不同的处理方法
                    {
                        case 22: {
                            JSONObject sender = m.getJSONObject("sender");
                            int srcID = -1;
                            if (sender.has("userID")) {
                                srcID = sender.getInt("userID");
                            }
                            if (srcID != -1) {
                                dstMsg = "From " + sender.getString("userName") + "(" + srcID + ") :" + m.getString("payLoad");
                            } else {//没ID就不输出
                                dstMsg = "From " + sender.getString("userName") + " :" + m.getString("payLoad");
                            }
                            showMsg_LV(dstMsg);//先显示消息

                            //if(m.getString("FBT")=="c")
                            if (sender.getString("userName").matches("serve"))//从服务器发来的信息帧才需要反馈
                            {
                                SendFbFrame();//收到信息帧，
                            }
                            break;
                        }

                        case 2: {
                            JSONObject Fb = m.getJSONObject("actionStatus");
                            dstMsg = "feedBack status :" + Fb.getString("actionRslMsg");// Fb.getInt("actionRslCode") + ", " +
                            showMsg_Toast(dstMsg);
                            break;
                        }

                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    dstMsg = peerMsg;
                }
                System.out.println(dstMsg + "good job");
            }
        };
        return msgHandler;
    }

}
