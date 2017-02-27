package myandroid.testui;

/**
 * Created by asus1 on 2016/12/19.
 */
public class globalvalue {
    private static SckCln socket=null;
    public static void SetSocket(SckCln sck){socket=sck;};
    public static SckCln GetSocket(){return socket;};
}
