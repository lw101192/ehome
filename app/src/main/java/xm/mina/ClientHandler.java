package xm.mina;


import android.os.Message;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.google.gson.Gson;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.bean.StaticVar;
import com.xm.Bean.MessageBean;

/**
 * Created by liuwei on 2017/1/20.
 */
public class ClientHandler extends IoHandlerAdapter {

    private boolean isKicked = false;
    Gson mGson = new Gson();
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("exceptionCaught"+cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session,message);
        System.out.println("获得服务器传过来的数据"+message.toString());
        message = mGson.fromJson(message.toString(),MessageBean.class);
        if(message instanceof MessageBean){
            MessageBean messageBean = (MessageBean)message;
            System.out.println("action>>"+messageBean.getAction());
//            File filePath = Environment.getExternalStorageDirectory();
//            String savePath = filePath + "/" + "32.doc";
//            System.out.print("savePath>>" + savePath);
//            FileOutputStream out = new FileOutputStream(savePath);
//            FileChannel fc = out.getChannel();
//            fc.write(ByteBuffer.wrap(messageBean.getContent().getBytecontent()));
//            System.out.println("文件接收完成");
            switch (messageBean.getAction()){
                case "login":
                    if(messageBean.getResult().getCode()==1){
                            Client.getInstance().onSuccess(1,messageBean.getAction());
                        }else{
                            Client.getInstance().onFaliure(0);
                        }

                    break;
                case "kickoff":
                    isKicked = true;
                    System.out.println("kickoff"+session.getLocalAddress()+" "+Client.getInstance().getSession().getLocalAddress());
//                    if(session==Client.getInstance().getSession()){
                        MainActivity.handler.sendEmptyMessage(StaticVar.LADNDING_IN_DIFFERENT_PLACES);
                        Client.getInstance().closeNow(true);
//                    }
                    break;
                case "SendCmd":
                    Object[] objmsg = new Object[3];
                    objmsg[0] = messageBean.getFrom().getId();//FromID
                    objmsg[1] = messageBean.getTo().getId();//ToID
                    objmsg[2] = messageBean.getContent().getStringcontent();//Content
                    System.out.println("obj>>"+objmsg[2].toString());
                    Message.obtain(MainActivity.handler, StaticVar.SEND_MESSAGE, objmsg).sendToTarget();
                    break;
                case "resonse":
                default:
                    Client.getInstance().onResopnse(messageBean);
                    break;
            }
        }


    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        System.out.println("messageSent");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        System.out.println("sessionClosed"+session.getLocalAddress());
        Client.getInstance().closeNow(true);
        if(Client.getInstance().isLogin()&&!isKicked)
            MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);

    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        System.out.println("sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        System.out.println("sessionOpened");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        System.out.println("sessionIdle");
    }
}
