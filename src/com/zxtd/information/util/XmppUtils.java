package com.zxtd.information.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.UserInfo;

public class XmppUtils {

	private static XMPPConnection con = null;
	public static final  String SERVER_NAME ="android"; //"";
	private static void openConnection(){
		try {
			XMPPConnection.DEBUG_ENABLED = true;//开启DEBUG模式  
			ConnectionConfiguration config = new ConnectionConfiguration(Constant.XMPP_IP, Constant.XMPP_PORT,SERVER_NAME);
			//自动连接
			config.setReconnectionAllowed(true);
			//允许登陆成功后更新在线状态
			config.setSendPresence(true);  
			config.setSASLAuthenticationEnabled(false); 
			config.setSecurityMode(SecurityMode.disabled);
			config.setCompressionEnabled(false);
			//收到邀请后，管理邀请是否接受
			Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
			configure(ProviderManager.getInstance());
			con = new XMPPConnection(config);
			if(!con.isConnected()){
				con.connect();
			}
		} catch (XMPPException ex) {
			Utils.printException(ex);
		}
	}

	public static XMPPConnection getSimpleConnection() {
		if (con == null){
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
		if(con!=null){
			try{
				if(con.isConnected()){
					con.disconnect();
				}
				con = null;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	/**
	 * 登录operfire服务器
	 */
	public static final boolean doLoginXmpp(){
		if (XmppUtils.getSimpleConnection().isAuthenticated()) {
			return true;
		} else {
			try {
				SharedPreferences shareds=ZXTD_Application.getMyContext().getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
				int userId=shareds.getInt("userId", -1);
				if(userId!=-1){
					String password=shareds.getString("xmpppassword", "123456");
					XmppUtils.getSimpleConnection().login(String.valueOf(userId),
							password);
					Presence presence = new Presence(Presence.Type.available);
					XmppUtils.getSimpleConnection().sendPacket(presence);
					boolean flag= XmppUtils.getSimpleConnection()
							.isAuthenticated();
					if(flag && !shareds.contains("xmpp"+userId)){
						Editor editor=shareds.edit();
						editor.putBoolean("xmpp"+userId, flag);
						editor.commit();
					}
					return flag;
				}
				return false;
			} catch (XMPPException e) {
				Utils.printException(e);
				return false;
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 注册插件的connection
	 */
	private static ConnectionConfiguration connectionConfig;

	// init
	/*
	static {
		Connection.DEBUG_ENABLED = false;

		ProviderManager pm = ProviderManager.getInstance();
		configure(pm);

		SmackConfiguration.setKeepAliveInterval(60000 * 5); // 5 mins
		SmackConfiguration.setPacketReplyTimeout(10000); // 10 secs
		SmackConfiguration.setLocalSocks5ProxyEnabled(false);
		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);

		connectionConfig = new ConnectionConfiguration(Constant.XMPP_IP, Constant.XMPP_PORT, "gmail.com");
		connectionConfig.setTruststorePath("/system/etc/security/cacerts.bks");
		connectionConfig.setTruststorePassword("changeit");
		connectionConfig.setTruststoreType("bks");
		// 允许自动连接
		connectionConfig.setReconnectionAllowed(true);
		// 允许登陆成功后更新在线状态
		connectionConfig.setSendPresence(true);
		//收到邀请后，管理邀请是否接受
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
	}*/

	public static XMPPConnection getRegConnection() {
		return new XMPPConnection(connectionConfig);
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			con = new XMPPConnection(connectionConfig);
		}

		if (!con.isConnected()) {
			try {
				con.connect();
			} catch (XMPPException e) {
				Utils.printException(e);
			}
		}
		return con;
	}

	public static void configure(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
		}

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
				new OpenIQProvider());
		pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
				new CloseIQProvider());
		pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

	}
	
	
	
	/**
	 * 注册用户
	 * @param userInfo
	 * @return
	 */
	public static String regist(UserInfo userInfo){
		Registration reg = new Registration();  
        reg.setType(IQ.Type.SET);  
        reg.setTo(getSimpleConnection().getServiceName());  
        reg.setUsername(String.valueOf(userInfo.getUserId()));//不能带@
        reg.setPassword(userInfo.getPassword());  
        reg.addAttribute("name", userInfo.getNickName());  
        reg.addAttribute("email", userInfo.getUserAccount().indexOf("@")!=-1 ? userInfo.getUserAccount() : userInfo.getUserAccount()+"@example.com");  
          
        reg.addAttribute("android", "geolo_createUser_android");  
        PacketFilter filter = new AndFilter(new PacketIDFilter(  
                                        reg.getPacketID()), new PacketTypeFilter(
                                        IQ.class));  
        PacketCollector collector = XmppUtils.getSimpleConnection().  
        createPacketCollector(filter);  
        XmppUtils.getSimpleConnection().sendPacket(reg);  
        IQ result = (IQ) collector.nextResult(SmackConfiguration  
                                        .getPacketReplyTimeout());  
                                // Stop queuing results  
        collector.cancel();// 停止请求results（是否成功的结果）  
        if(result == null){
        	return null;
        }else if(result.getType()==IQ.Type.ERROR){
        	if (result.getError().toString().equalsIgnoreCase("conflict(409)")){  
        		return "exist";
			} else {  
			      return "error";
			}  
        }else if(result.getType()==IQ.Type.RESULT){
        	return "success";
        }
        return "";
	}
	
	
	/**
	 * 根据jid获取用户名
	 */
	public static String getJidToUsername(String jid){
		return jid.split("@")[0];
	}
	
	
	
	/**
	 * 添加关注
	 */
	public static boolean addFriend(String userId,String nickName){
		try {
			 Roster roster = XmppUtils.getSimpleConnection().getRoster();
	    	 String jid = userId+"@"+XmppUtils.getSimpleConnection().getServiceName();
	    	 //默认添加到【我的好友】分组
	    	 //String groupName = null;
	    	 //Presence subscription = new Presence(Presence.Type.subscribe);
	         //subscription.setTo(jid);
			 roster.createEntry(jid,nickName ,null);
			return true;
		} catch (Exception e) {
			Utils.printException(e);
			return false;
		}
		
		
	}
	
	

	/**
	 * 删除好友
	 * @param account
	 * @return
	 */
	public static boolean removeFriend(String userId){
		try {
			/*RosterGroup group = XmppUtils.getSimpleConnection().getRoster().getGroup(null);
            if (group != null) {
                RosterEntry entry =  XmppUtils.getSimpleConnection().getRoster().getEntry(userId);
                if (entry != null){
                	group.removeEntry(entry);
                }
            }*/
			Roster roster = XmppUtils.getSimpleConnection().getRoster();
			RosterEntry entry = roster.getEntry(userId+"@"+XmppUtils.getSimpleConnection().getServiceName());
			roster.removeEntry(entry);
			return true;
		} catch (Exception e) {
			Utils.printException(e);
			return false;
		}	
	}
	
	
	public static int getUserId(){
		if(null==Constant.loginUser || Constant.loginUser.getUserId()<1){
			SharedPreferences shareds=ZXTD_Application.getMyContext().getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			return shareds.getInt("userId", -1);
		}else{
			return Constant.loginUser.getUserId();
		}
	}
	
}
