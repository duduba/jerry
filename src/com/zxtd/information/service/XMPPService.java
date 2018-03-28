package com.zxtd.information.service;
import java.io.File;
import java.util.Date;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.json.JSONException;
import org.json.JSONObject;

import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.IMMessageBean;
import com.zxtd.information.bean.Session;
import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.manager.FansFocusManager;
import com.zxtd.information.manager.MessageManager;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.manager.SessionManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.FansAndFocusActivity;
import com.zxtd.information.ui.me.MyPublicActivity;
import com.zxtd.information.ui.me.im.ChatActivity;
import com.zxtd.information.ui.post.MineCommentActivity;
import com.zxtd.information.util.Base64Utils;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class XMPPService extends Service {

	//private boolean runFlag=true;

	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	private final MyBinder myBinder=new MyBinder();
	
	public class MyBinder extends Binder{
		public XMPPService getMyService(){
			return XMPPService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	private static boolean isFirst=true;
	public void execute(final Handler handler){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
					while(true){
						try{
							if(Utils.isNetworkConn()){
								if(!XmppUtils.getSimpleConnection().isAuthenticated() || !XmppUtils.getSimpleConnection().isConnected()){
									XmppUtils.closeConnection();
									boolean isLogin=XmppUtils.doLoginXmpp();
									if(isLogin){
										if(isFirst){
											handler.sendEmptyMessage(1);
										}
										ChatManager chatManager=XmppUtils.getSimpleConnection().getChatManager();
										if(!isFirst){
											chatManager.removeChatListener(listener);
										}
										chatManager.addChatListener(listener);

										ServiceDiscoveryManager sdm = ServiceDiscoveryManager .getInstanceFor(XmppUtils.getSimpleConnection()); 
										if (sdm == null) { 
											sdm = new ServiceDiscoveryManager(XmppUtils.getSimpleConnection()); 
										}
										sdm.addFeature("http://jabber.org/protocol/disco#info");
			                            sdm.addFeature("jabber:iq:privacy");
										FileTransferManager filemanager = new FileTransferManager(XmppUtils.getSimpleConnection());
										FileTransferNegotiator.setServiceEnabled(XmppUtils.getSimpleConnection(), true);
										
										if(!isFirst){
											filemanager.removeFileTransferListener(fileListener);
										}
										filemanager.addFileTransferListener(fileListener);
										isFirst=false;
									}
								}
							}
							Thread.sleep(10000);
						}catch(Exception ex){
							Utils.printException(ex);
							try{
								Thread.sleep(10000);
							}catch(Exception e){}
						}
					}
			}
		}.start();
	}
	
	
	private JSONObject getJson(String data){
		try {
			JSONObject jsonObject =new JSONObject(data);
			return jsonObject;
		} catch (JSONException e) {
			return null;
		}
		
	}
	
	
	private void notifyUser(int type,int data,String childId){
		//String content="";
		Intent newIntent=new Intent(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED);
		newIntent.putExtra("type", type);
		newIntent.putExtra("isOpenfire", true);
		switch(type){
			case 0:{
				int localCount=0;
				if(null!=Constant.loginUser && Constant.loginUser.getFansCount()>0){
					localCount=Constant.loginUser.getFansCount();
				}else{
					localCount=UserInfoManager.getInstance().getFansCount();
				}
				newIntent.putExtra("data", data);
				if(data>localCount){
					/*content="您有一个新粉丝";
					Intent intent=new Intent(this,FansAndFocusActivity.class);
					intent.putExtra("type", 0);
					intent.putExtra("from", "self");
					intent.putExtra("userId",XmppUtils.getUserId());
					intent.putExtra("userName", "我的");
					*/
					//评论我的粉丝通知
					//doNotify(content,intent);
					UserInfoManager.getInstance().setNewFans(data,1);
					newIntent.putExtra("isNotify", true);
					if(Constant.loginUser==null){
						UserInfoManager.getInstance().setRedPoint("fansHasNew");
					}
				}else{
					UserInfoManager.getInstance().setNewFans(data,0);
					newIntent.putExtra("isNotify", false);
				}
			}break;
			case 1:{
				//通知 点击进入我的评论
				/*content="您的发布有新评论了";
				Intent intent=new Intent(this,MyPublicActivity.class);
				intent.putExtra("userId",XmppUtils.getUserId());
				intent.putExtra("userName", "我的");
				*/
				//屏蔽我的评论的通知
				//doNotify(content,intent);
				
				newIntent.putExtra("isNotify", true);
				newIntent.putExtra("ids", childId);
				if(null==Constant.loginUser){
					UserInfoManager.getInstance().setRedPoint("publicHasNew");
					PublicManager.getInstance().setPublicRedPoint(childId);
				}
			}break;
			case 2:{
				/*content="您的评论有人回复了.";
				Intent intent=new Intent(this,MineCommentActivity.class);
				intent.putExtra("userId",XmppUtils.getUserId());
				intent.putExtra("userName", "我的");
				*/
				newIntent.putExtra("isNotify", true);
				newIntent.putExtra("ids", childId);
				if(null==Constant.loginUser){
					UserInfoManager.getInstance().setRedPoint("hasNewComment");
				}
			}break;
		}
		sendBroadcast(newIntent);	
	}
	
	private void doNotify(String content,Intent intent){
		boolean isNotify=Utils.load(this, Constant.DataKey.IS_PRIVATE_LETTER_ON,true);
		isNotify=true;
		if(isNotify){
			NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(Constant.IM_NOTIFY_FLAG);
			// 定义Notification的各种属性 
			Notification notification = new Notification(R.drawable.ic_news, 
	                "", System.currentTimeMillis()); 
	        //Notification.FLAG_NO_CLEAR |
	        notification.flags=Notification.FLAG_AUTO_CANCEL | 
	        					 Notification.FLAG_SHOW_LIGHTS;;//将此通知放到通知栏的"Ongoing"即"正在运行"组中 
	        notification.defaults=Notification.DEFAULT_LIGHTS;
	        notification.defaults |= Notification.DEFAULT_SOUND;
	        
	        notification.ledARGB = Color.BLUE; 
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
        	notification.defaults |=Notification.FLAG_SHOW_LIGHTS;
        	
        	notification.defaults |= Notification.DEFAULT_VIBRATE;
        	long[] vibrate = {0,100,200,300};
        	notification.vibrate = vibrate;
        	
        	
        	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT); 
            notification.setLatestEventInfo(this, "杂色新闻", content, contentIntent); 
            manager.notify(Constant.IM_NOTIFY_FLAG, notification);
		}
	}
	
	
	/**
	 * 判断是否为文件
	 * @param body
	 * @param from
	 */
	private boolean isFile(String body,String from){
		String prefix=Constant.PREFIX_STAG+"_"+from;
		String suffix=Constant.SUFFIX_STAG+"_"+from;
		if(body.startsWith(prefix) && body.endsWith(suffix)){
			
			String head=body.substring(0,Constant.XMPP_HEAD_LENGTH).trim();
			String content=body.substring(Constant.XMPP_HEAD_LENGTH,(body.length()-suffix.length()));
			
			int index=head.indexOf("_");
			head=head.substring(index+1);
			String[] params=head.split("_");
			
			int fileType=Integer.valueOf(params[1]);
			String strType="图片";
			String direType="images";
			int voiceSecond=0;
			if(fileType==2){
				strType="语音消息";
				direType="voices";
				voiceSecond=Integer.valueOf(params[2]);
			}
			String fileSuffix=params[3];
			
			String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zxtd_zase/"+direType+"/";
			Date date=new Date();
			String savePath=directory+date.getYear()+"/"+(date.getMonth()+1)+"/";
			File file=new File(savePath);
			if(!file.exists()){
				file.mkdirs();
			}
			String fileName=savePath+new Date().getTime()+"."+fileSuffix;
			IMMessageBean msgBean=progressMsg(TimeUtils.getNow(), strType, state, from, fileType, fileName, voiceSecond);
			boolean flag=Base64Utils.decodeFile(content, fileName);
			if(flag){
				state=IMMessageBean.SUCCESS;
				android.os.Message message=handler.obtainMessage();
				message.what=1;
				message.obj=msgBean;
				message.sendToTarget();
			}else{
				state=IMMessageBean.ERROR;
			}
			return true;
		}
		return false;
	}
	
	
	
	ChatManagerListener listener=new ChatManagerListener(){
		@Override
		public void chatCreated(Chat chat, boolean arg1) {
			// TODO Auto-generated method stub
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat chat, Message message){
					//Log.e(Constant.TAG,"mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm:"+message.getBody()+"  iiiiiiiiiiiii: "+message.getFrom());
					String from=message.getFrom().split("/")[0].split("@")[0];
					String body=message.getBody();
					
					//base64 传文件
					if(isFile(body,from)){
						return;
					}
					
					
					JSONObject obj=getJson(body);
					int number=0;
					String childs="";
					if(obj !=null){
						int type=Integer.valueOf(obj.optString("type"));
						switch(type){
							case 0:{
								number=Integer.valueOf(obj.optString("noticeNum"));
								//UserInfoManager.getInstance().setNewFans(number);
							}break;
							case 1:{
								try {
									childs=obj.getString("childIds");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}break;
							case 2:{
								
							}break;
						}
						notifyUser(type,number,childs);
						return;
					}
					String time = (String) message.getProperty(IMMessageBean.KEY_TIME);
					time=time == null ? TimeUtils.getNow(): time;
					int state=0;
					if(Message.Type.error == message.getType()) {
						state=IMMessageBean.ERROR;
					} else {
						state=IMMessageBean.SUCCESS;
					}
					
					progressMsg(time,body,state,from,0,"",0);
					/**
					IMMessageBean msg=new IMMessageBean();
					String time = (String) message.getProperty(IMMessageBean.KEY_TIME);
					msg.setTime(time == null ? TimeUtils.getNow(): time);
					String body=message.getBody();
					msg.setContent(body);
					if(Message.Type.error == message.getType()) {
						msg.setState(IMMessageBean.ERROR);
					} else {
						msg.setState(IMMessageBean.SUCCESS);
					}
					boolean isSend=false;
					String from=message.getFrom().split("/")[0].split("@")[0];
					if(!SessionManager.getInstance().checkSessionExtis(from, XmppUtils.getUserId())){
						addSession(from,body);
						isSend=true;
					}
					msg.setSender(from);//设置消息发送人ID
					msg.setType(0);//设置消息类型
					msg.setAttaFile("");//设置文件路径，如果有附近，需保存附近到sdcard
					msg.setIsSelf(IMMessageBean.OTHER);//设置为接收的消息
					
					Session session=Constant.CACHESESSION.get(from);
					msg.setSessionId(session.getSessionId());
					//消息入库
					int msgId=MessageManager.getInstance().insertMessage(msg);
					msg.setMsgId(msgId);
					
					if(!Constant.currentChatJid.equals(from)){
						String title="";
						String name=Constant.CACHESESSION.get(from).getNickName();
						switch(msg.getType()){
							case 0:{
								title=name+": "+msg.getContent();
							}break;
						}
						notifyUser(from,title);
						//通知更新未读数量
						if(!isSend){
							session.setNoReadMsgCount(session.getNoReadMsgCount()+1);
							session.setLastMsg(msg.getContent());
							session.setLastSendTime(TimeUtils.getNow());
							Intent newIntent=new Intent(Constant.REFRESH_SESSION);
							newIntent.putExtra("type", "update");
							newIntent.putExtra("session", session);
							sendBroadcast(newIntent);
						}
						isSend=false;
					}else{
						//更新消息
						Intent intent=new Intent(Constant.RECIVEIMMSG);
						intent.putExtra("immsg", msg);
						sendBroadcast(intent);
					}
				*/
				}
			});
		}
	};
	
	
	
	private IMMessageBean progressMsg(String time,String body,int state,String from,int type,String attaFile,int voiceSecond){
		IMMessageBean msg=new IMMessageBean();
		msg.setTime(TimeUtils.getNow());
		msg.setContent(body);
		msg.setState(state);
		boolean isSend=false;
		if(!SessionManager.getInstance().checkSessionExtis(from, XmppUtils.getUserId())){
			addSession(from,body);
			isSend=true;
		}
		msg.setSender(from);//设置消息发送人ID
		msg.setType(type);//设置消息类型
		msg.setAttaFile(attaFile);//设置文件路径，如果有附近，需保存附近到sdcard
		msg.setIsSelf(IMMessageBean.OTHER);//设置为接收的消息
		msg.setVoiceSecond(voiceSecond);
		
		Session session=Constant.CACHESESSION.get(from);
		msg.setSessionId(session.getSessionId());
		//消息入库
		int msgId=MessageManager.getInstance().insertMessage(msg);
		msg.setMsgId(msgId);
		
		if(!Constant.currentChatJid.equals(from)){
			String title="";
			String name=Constant.CACHESESSION.get(from).getNickName();
			switch(msg.getType()){
				case 0:{
					title=name+": "+msg.getContent();
				}break;
				case 1:{
					title=name+": 发来图片";
				}break;
				case 2:{
					title=name+": 发来一段语音";
				}break;
			}
			if(!isSend){
				session.setNoReadMsgCount(session.getNoReadMsgCount()+1);
			}
			notifyUser(from,title);
			SessionManager.getInstance().modifySession(session.getSessionId(),msg.getContent(),msg.getTime(),session.getNoReadMsgCount());
			
			//通知更新未读数量
			if(!isSend){
				session.setLastMsg(msg.getContent());
				session.setLastSendTime(TimeUtils.getNow());
				
				Intent newIntent=new Intent(Constant.REFRESH_SESSION);
				newIntent.putExtra("type", "update");
				newIntent.putExtra("session", session);
				sendBroadcast(newIntent);
				
				if(null==Constant.loginUser){
					UserInfoManager.getInstance().upLetterCount();
					
					int count=MessageManager.getInstance().getTotalNoReadCount();
					Intent tempIntent=new Intent(Constant.UPDATE_TOTAL_NO_READ);
					tempIntent.putExtra("count", count);
					sendBroadcast(tempIntent);
				}
			}
			isSend=false;
		}else{
			//更新消息
			Intent intent=new Intent(Constant.RECIVEIMMSG);
			intent.putExtra("immsg", msg);
			intent.putExtra("type", "add");
			sendBroadcast(intent);
			
		}
		return msg;
	}
	
	
	String fileType="";
	int state=IMMessageBean.SEND;
	private  FileTransferListener fileListener=new FileTransferListener(){
		@Override
		public void fileTransferRequest(FileTransferRequest arg0) {
			int type=2;
			int voiceSecond=0;
			String direType="voices";
			String content="语音消息";
			
			if(!arg0.getFileName().endsWith(".amr")){
				type=1;
				direType="images";
				content="图片";
				fileType=new Date().getTime()+"_";
			}else{
				voiceSecond=Integer.valueOf(arg0.getDescription());
				fileType="";
			}
			String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zxtd_zase/"+direType+"/";
			Date date=new Date();
			String savePath=directory+date.getYear()+"/"+(date.getMonth()+1)+"/";
			File file=new File(savePath);
			if(!file.exists()){
				file.mkdirs();
			}
			String fileName=savePath+fileType+arg0.getFileName();
			String from=arg0.getRequestor().split("@")[0];
			file=new File(fileName);
			try {
				final IncomingFileTransfer in =  arg0.accept();
				String alertMsg="收到文件："+arg0.getRequestor()+"   文件接收路径........"+fileName+"    文件名:"+arg0.getFileName()+"      "
						+arg0.getDescription()+"    "+arg0.getMimeType()+"    "+in.getProgress()+"   "+in.getException()+"   "
						+in.getStatus();
				Log.e(Constant.TAG,alertMsg);
				
				if(!file.exists()){
					file.createNewFile();
				}
				in.recieveFile(file);
				final IMMessageBean msgBean=progressMsg(TimeUtils.getNow(),content,state,from,type,fileName,voiceSecond);
				new Thread(){
					@Override
					public void run() {
						super.run();
						long timeOut = 100000;
						long sleepMin = 500;
						long spTime = 0;
						
						while(true){
							try{
								Status status =in.getStatus();	
								if((status == FileTransfer.Status.error)
										|| (status == FileTransfer.Status.complete)
										|| (status == FileTransfer.Status.cancelled)
										|| (status == FileTransfer.Status.refused)) {
									if(status == FileTransfer.Status.complete){
										state=IMMessageBean.SUCCESS;
									}else{
										state=IMMessageBean.ERROR;
									}
									//MessageManager.getInstance().modifyMsgState(msgBean.getMsgId(),state);
									//notifyFileComplete(msgBean,state);
									break;
								}
								spTime +=sleepMin;
								if(spTime > timeOut) {
									state=IMMessageBean.ERROR;
									//MessageManager.getInstance().modifyMsgState(msgBean.getMsgId(),state);
									//notifyFileComplete(msgBean,state);
								   break;
								}
								Thread.sleep(sleepMin);
							}catch(Exception ex){
								state=IMMessageBean.ERROR;
								Utils.printException(ex);
								break;
							}
						}
						android.os.Message message=handler.obtainMessage();
						message.what=1;
						message.obj=msgBean;
						message.sendToTarget();
					}
				}.start();
				
			} catch (Exception e) {
				Utils.printException(e);
			}
		}
	};
	
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case 1:{
					//通知
					if(state==IMMessageBean.SEND){
						state=IMMessageBean.SUCCESS;
					}
					IMMessageBean msgBean=(IMMessageBean) msg.obj;
					MessageManager.getInstance().modifyMsgState(msgBean.getMsgId(),state);
					notifyFileComplete(msgBean,state);
				}break;
			}
		}
	};
	
	
	private void notifyFileComplete(IMMessageBean msg,int state){
		msg.setState(state);
		Intent intent=new Intent(Constant.RECIVEIMMSG);
		intent.putExtra("immsg", msg);
		intent.putExtra("type", "refreshImg");
		sendBroadcast(intent);
	}
	
	
	
	/**
	 * 更新进度
	 */
	
	/*
	private void updatProgress(int downSize,long totalSize){
		float progress=(downSize/totalSize)*100;
		DecimalFormat format=new DecimalFormat("###");
		String temp=format.format(progress);
		int percent=Integer.valueOf(temp);
		Intent intent=new Intent(Constant.UPDATE_IM_RECEIVE_FILE_PROGRESS);
		intent.putExtra("progress", percent);
		sendBroadcast(intent);
	}
	*/
	
	private void notifyUser(String from,String content){
		boolean isNotify=Utils.load(this, Constant.DataKey.IS_PRIVATE_LETTER_ON,true);
		isNotify=true;
		if(isNotify){
			NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(Constant.IM_NOTIFY_FLAG);
			// 定义Notification的各种属性 
			Notification notification = new Notification(R.drawable.ic_news, 
	                "", System.currentTimeMillis()); 
	        //Notification.FLAG_NO_CLEAR |
	        notification.flags=Notification.FLAG_AUTO_CANCEL | 
	        					 Notification.FLAG_SHOW_LIGHTS;;//将此通知放到通知栏的"Ongoing"即"正在运行"组中 
	        notification.defaults=Notification.DEFAULT_LIGHTS;
	        notification.defaults |= Notification.DEFAULT_SOUND;
	        
	        notification.ledARGB = Color.BLUE; 
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
        	notification.defaults |=Notification.FLAG_SHOW_LIGHTS;
        	
        	notification.defaults |= Notification.DEFAULT_VIBRATE;
        	long[] vibrate = {0,100,200,300};
        	notification.vibrate = vibrate;
        	
        	Session session=Constant.CACHESESSION.get(from);
        	Intent intent=new Intent(this,ChatActivity.class);
        	Bundle bundle=new Bundle();
    		bundle.putParcelable("session", session);
    		intent.putExtras(bundle);
        	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT); 
            notification.setLatestEventInfo(this, "杂色新闻", content, contentIntent); 
            manager.notify(Constant.IM_NOTIFY_FLAG, notification);
		}
	}
	
	/**
	 * 检查是否存在该会话
	 * @param chatId
	 */
	private void addSession(String chatId,String lastImg){
		//查询好友表取出昵称和图片
		//添加到数据库和缓存
		FansFocusManager manager=new FansFocusManager();
		FansFocusBean bean=manager.getFocusById(chatId, XmppUtils.getUserId());
		int _id=0;
		if(null!=bean){
			 _id=SessionManager.getInstance().addSession(chatId, 
					bean.getNickName(), bean.getImg(), bean.getSign(), lastImg,TimeUtils.getNow(), 1, XmppUtils.getUserId());
		}else{
			UserInfo info=UserInfoManager.getInstance().getUserInfo(Integer.valueOf(chatId), XmppUtils.getUserId(), false);
			if(null!=info){
				 _id=SessionManager.getInstance().addSession(chatId, 
						info.getNickName(),info.getHeader(), info.getSign(), lastImg,TimeUtils.getNow(), 1, XmppUtils.getUserId());
			}
		}
		if(_id>0){
			Session session=SessionManager.getInstance().getSessionById(_id);
			if(null!=session){
				if(null==Constant.loginUser){
					UserInfoManager.getInstance().upLetterCount();
				}
				Constant.CACHESESSION.put(session.getSessionName(), session);
				//通知sessionActivity 更新
				Intent intent=new Intent(Constant.REFRESH_SESSION);
				intent.putExtra("type", "add");
				intent.putExtra("session", session);
				session.setNoReadMsgCount(session.getNoReadMsgCount());//1
				sendBroadcast(intent);
			}
		}
	}
	
	
	/**
	 * 加载会话列表到缓存
	 */
	static{
		SessionManager.getInstance().getSessions(XmppUtils.getUserId());
	}
	
}
