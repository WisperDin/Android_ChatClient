package myandroid.testui;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by asus1 on 2016/12/13.
 */
public class JSONmarshall {
    enum MarShalMode {
        FeedBackFra,
        ChatFra,
        InitUserFra
    }
    private static long GetTim() {
        long mS = System.currentTimeMillis();
        return (long) (Math.pow(10, 6) * mS);
    }
    public static String marshalMsgR(String arg_src, String[] arg_dstList, String arg_msg, String arg_pw, int arg_id, MarShalMode modeFlag) {
        JSONObject qNearMsg = new JSONObject();
        JSONObject qNearSender = new JSONObject();
        JSONObject qNearDst = new JSONObject();
        JSONObject qNearFb = new JSONObject();
        try {
            qNearMsg.put("protoSign", 142857);
            qNearMsg.put("msgLength", 1073742063);
            //qNearMsg.put("msgLength", 0x12345678);
            qNearMsg.put("senderTimer", GetTim());
            switch (modeFlag) {
                case FeedBackFra: {
                    qNearMsg.put("actionStatus", qNearFb);
                    qNearMsg.put("msgType", 2);
                    qNearFb.put("actionMsgType", 22);
                    qNearFb.put("actionRslCode", 1);
                    qNearFb.put("actionRslMsg", arg_msg);

                    break;
                }
                case ChatFra: {
                    qNearMsg.put("sender", qNearSender);
                    if (!arg_src.isEmpty()) {
                        qNearSender.put("userName", arg_src);
                        if (arg_id != -1) {
                            qNearSender.put("userID", arg_id);
                        }
                    }
                    qNearMsg.put("msgType", 22);
                    System.out.println(arg_dstList[0]);
                    if (arg_dstList.length == 1 && arg_dstList[0].matches("serve")) {//发送目标只有服务器
                        qNearMsg.put("feedbackType", "s");
                    } else {//其他情况是 存在发送目标有客户端
                        qNearMsg.put("feedbackType", "c");
                    }
                    if (arg_dstList != null) {
                        JSONArray qnearUL = new JSONArray();


                        //List list = new ArrayList();
                        for (int i = 0; i < arg_dstList.length; i++) {
                            JSONObject qNearU = new JSONObject();
                            qNearU.put("userName", arg_dstList[i]);
                            qnearUL.put(qNearU);
                        }
                        qNearMsg.put("userList", qnearUL);
                    }
                    qNearMsg.put("payLoad", arg_msg);
                    break;
                }
                case InitUserFra: {
                    qNearMsg.put("msgType", 22);
                    qNearMsg.put("sender", qNearSender);
                    qNearMsg.put("feedbackType", "s");
                    if (!arg_src.isEmpty()) {
                        qNearSender.put("userName", arg_src);
                        if (arg_id != -1) {
                            qNearSender.put("userID", arg_id);
                        }
                        if (arg_pw != "") {
                            qNearSender.put("userPWD", arg_pw);
                        }

                    }


                    break;
                }
            }
            String s = qNearMsg.toString();
            //String sLen = String.format("0x%08x", s.length()); // | 0x00000040

            //String sLen = String.format("0x%x", s.length());
            //long ilen=Long.parseLong(sLen,16);
            qNearMsg.put("msgLength",s.length()|0x40000000);
            s = qNearMsg.toString();
            return s;

            //return s.replaceAll("0x12345678", sLen);
        } catch (Exception e) {
            return e.getMessage();
        }

    }
}
