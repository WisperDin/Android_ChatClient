package myandroid.testui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by asus1 on 2016/12/13.
 */
class SckCln  extends Thread {


    public enum Status {
        DISCONNECTED, CONNECTING, CONNECTED
    }
    Handler msgHandler;
    public boolean bRunning = true;
    public boolean bConnected = false;
    public Status status = Status.DISCONNECTED;

    String srvName = "192.168.191.1";
    int srvPort = 6666;

    public SckCln(String arg_srvName, int arg_port, Handler arg_msgHandler) {
        if (arg_srvName == null || arg_srvName.isEmpty() || arg_srvName.length() <= 0) {
            throw new RuntimeException("invalid srvName");
        }
        if (arg_port <= 0) {
            throw new RuntimeException("invalid port");
        }
        if (arg_msgHandler == null) {
            throw new RuntimeException("arg_msgReceiver is null.");
        }

        srvPort = arg_port;
        srvName = arg_srvName;
        msgHandler = arg_msgHandler;
    }
    ///关闭连接
    public void Close() {
        bConnected = false;
        status = Status.DISCONNECTED;

        if (!sckCln.isClosed()) {
            try {
                sckCln.close();
            } catch (IOException e) {
                msgToUI(4040, e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }


    Socket sckCln;
    InputStream sckIn;
    OutputStream sckOut;

    //更新信息在UI上
    void msgToUI(int arg_what, String arg_msg) {
        Message m = Message.obtain();
        m.what = arg_what;
        Bundle data = new Bundle();
        data.putString("peerData", arg_msg);
        m.setData(data);
        System.out.println(arg_msg);
        msgHandler.sendMessage(m);/////把Message参数传给msgHandler
    }

    public boolean sendMsg(String arg_msg) {
        if (!bConnected) {
            msgToUI(4020, "disconnected with server.");
            return false;
        }

        try {
            System.out.println("try send msg: " + arg_msg + " to server.");
            sckOut.write(arg_msg.getBytes("UTF-8"));
            sckOut.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    //SckCln线程开启后做的函数
    @Override
    public void run() {
        try {

            bConnected = false;
            status = Status.CONNECTING;
            msgToUI(4020, "connecting to server... ");
            sckCln = new Socket();
            sckCln.connect(new InetSocketAddress(srvName, srvPort), 2500);
            status = Status.CONNECTED;
            sckIn = sckCln.getInputStream();
            sckOut = sckCln.getOutputStream();
            msgToUI(4030, "connected to server ");
            bConnected = true;

            byte[] buf = new byte[4 * 1024];
            int readLen = 0;
            do {
                readLen = sckIn.read(buf);/////////////////////////////////////////////消息就是在这里被读取的
                String msg = new String(buf, 0, readLen, "UTF-8");
                System.out.println("get msg: " + msg + " from server.");
                msgToUI(1000, msg);
          /*
          String dstMsg = marshalMsgR(null,null,"SUCCESS",null,-1,MarShalMode.FeedBackFra);
          System.out.println(dstMsg);
          cln.sendMsg(dstMsg);*/
            } while (bRunning);

        } catch (Exception e) {
            status = Status.DISCONNECTED;
            msgToUI(4040, e.getMessage());
        }
    }
}