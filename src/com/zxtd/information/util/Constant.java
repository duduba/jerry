package com.zxtd.information.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.Session;
import com.zxtd.information.bean.UserInfo;
import android.os.Environment;

public class Constant {
	
	public static final String ZASE_APP_PUBLIC_KEY="9193943101D7B9F3FC6B4FC11BBB1FE0";
	public static final String ZASE_APP_SERIALNUMBER="1362734097";
	
	public static final String 	TAG="NEWS";
	public static final int MINE_PHOTO_SIZE=8;
	//腾讯QQ
	public static final String APP_ID="100488610";
	public static final String APP_KEY="1e54f092be1a085a00eb1ee593c0304b";
	public static final String SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,"
            + "add_share,add_topic,list_album,upload_pic,add_album,set_user_face,get_vip_info,get_vip_rich_info,get_intimate_friends_weibo,match_nick_tips_weibo";
	public static final String SHAREDPREFERENCES="newsconfig";
	//新浪
	public static final String CONSUMER_KEY ="4154241754"; // 2045436852 //"1623847837";
	public static final String SINA_KEY="acdba03ff3a474e4943dea94e19bb9a3";//"1c54d4b9c19df46c4fa0da8ac8a2e4e0";
	public static final String REDIRECT_URL = "http://www.sina.com";
	
	//新支持scope 支持传入多个scope权限，用逗号分隔
	public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write," +
				"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
					"follow_app_official_microblog";
	
	//腾讯微博
	public static final long TENCENT_APP_ID = 801389844;
	public static final String TENCENT_APP_SECKET = "6d3b45ae6e1a341a28867bbcf35ade12";
	
	//微信
	public static final String WECHAT_APP_ID = "wxa86208c243df5f4d";
	
	public static final String[] EMAIL_SUFFIX={"@163.com", "@126.com","@yeah.net", "@qq.com",
		"@sina.com","@vip.sina.com","@gmail.com","@hotmail.com","@sohu.com"
		,"@139.com","@189.cn","@21.cn"};
	public static final int PAGESIZE=20;
	public static UserInfo  loginUser;
	public static final String NOTIFY_FANSFOCUS_DATA_CHANGED="com.zxtd.information.notify.fansfocus.data.change";
	public static final String NOTIFY_CACHE_DATA_CHANGED="com.zxtd.information.notify.cache.data.change";
	public static final String UPDATE_MINE_TABHOST_ITEM="com.zxtd.update.mine.tabhost.item";
	public static final String UPDATE_TOTAL_NO_READ="com.zxtd.update.total_no_read";
	public static final String REFRESH_COLLECTION_DATA="com.zxtd.refresh.collection.data";
	public static final String RENREN_APPID="238743";
	public static final String RENREN_APPKEY="f7dd9c907f924f2d996e97f9229b8335";
	public static final String RENREN_SECRETKEY="a33773b0f5654bc3a74a53abc7364d24";
	
	
	public static final String XMPP_IP="www.zaseinfo.com";//119.57.53.136
	public static final int XMPP_PORT=5222;
	public static final String PREFIX_STAG="*#*";
	public static final String SUFFIX_STAG="#*#";
	public static final int XMPP_HEAD_LENGTH=22;
	
	public static final String RECIVEIMMSG="com.zxtd.information.recive.message";
	public static String currentChatJid="";
	public static final int IM_NOTIFY_FLAG=2013072317;
	public static boolean isXmppLogin=false;
	public static final Map<String,Session> CACHESESSION=new HashMap<String,Session>();
	public static final String REFRESH_SESSION="com.zxtd.information.refresh.session";
	public static final String UPDATE_IM_RECEIVE_FILE_PROGRESS="com.zxtd.information.im_receive_file_progress";
	
	
	public static final String USER_VERSION_KEY="1";
	public static final String FANS_VERSION_KEY="2";
	public static final String PUBLIC_VERSION_KEY="3";
	public static final String COMMENT_VERSION_KEY="4";
	public static final String COLLECTION_VERSION_KEY="5";
	
	public static final String CACHE_SAVE_PATH=Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/imgcache/";
	
	
	
	public interface Url {
//		String HOST_URL = "http://zase.zxtd.net";   http://192.168.1.233:8080
//		String HOST_URL ="http://192.168.1.233:8080"; // "http://www.zaseinfo.com";
//		String HOST_URL = "http://test.zaseinfo.com";
		String HOST_URL = "http://www.zaseinfo.com";
		String REQUEST_URL = HOST_URL+"/newinfo/clientAccess";
		String IMAGE_REQUEST_URL = HOST_URL +  "/newinfo/clientAccessUpload";
		String ALBUMS_HEAD_REQUEST_URL=HOST_URL +  "/newinfo/clientUserAccessUpload";
	}
	
	public interface Information {
		String ZXTD_SI = "3000";
		String ZXTD_DI = "00";
	}
	
	public interface Flag{
		String HOT_TYPE = "1";
		String NORMAL_TYPE = "0";
		String NET_FRIEND_TYPE = "2";
		String VIDEO_TYPE = "3";
		String POSTER_TYPE = "10";
	}
	
	public interface ItemFlag{
		String NEW_ITEM = "0";
		String NET_FRIEND_ITEM = "1";
	}
	public interface NetFriendListFlag{
		String BOUTIQUE_ITEM = "3";  //精华帖
		String NORMAL_ITEM = "4";	 //最新帖
		String TYPE_ITEM = "5";		 //类别
	}
	
	public interface ResponseCode{
		int SUCCESS = 200;
		int NET_ERROR = 201;
		int XML_ERROR = 202;
	}
	
	public interface DownStatus {
		int ERROR = 0;
		int INIT = 1;
		int DOWNLOADING = 2;
		int PAUSE = 3;
		int COMPLETE = 4;
		int UPDATE = 5;
	}

	public interface DownFile {
		String downingPath = Environment.getExternalStorageDirectory()
				.toString() + "/zxtd/cache/";
		String downedPath = Environment.getExternalStorageDirectory()
				.toString() + "/zxtd/down/";
		String endName = ".apk";
	}

	public interface BundleKey {
		String NEWBEAN = "NewBean";
		String SOFTBEAN = "SoftBean";
		String NEWID = "NewId";
		String PUBLISH_BEAN = "publishBean";
		String FLAG = "Flag";
		String OPERATE_PUSH = "operate_push";
		String NET_FRIEND_TYPE = "net_friend_type";
		String NET_FRIEND_TYPE_NAME = "net_friend_type_name";
		String POST_COUNT = "post_count";
		String IMAGE_INDEX = "image_index";
		String IMGE_DATA = "image_data";
		String WHICH_CHANNEL = "which_channel";
	}


	public interface DataKey {
		String UUID = XmlTagName.UUID;
		String REGISTERDATE = XmlTagName.REGISTERDATE;
		String FILE_NAME = "Data";
		String NICK_NAME = "nickName";
		String ISPUSH = "ispush";
		String FONTTYPESIZE = "fontSize";
		String IMAGE_MODE = "imageMode";
		String IS_SOUND_ON = "isSoundOn";
		String IS_SHOCK_ON = "isShockOn";
		String IS_PRIVATE_LETTER_ON = "isPrivateLetterOn";
		String IS_FANS_ON = "isFansOn";
		String IS_COMMENT_ON = "isCommentOn";
	}

	public interface XmlTagName {
		String UUID = "uuid";
		String REGISTERDATE = "registerdate";
	}

	public interface XmlMapKey {
		String ACTION = "ZXTD.action";

	}
	
	public interface posterDataKey{
		String MENUPAGEPOSTID = "menu_id";
		String MENUPAGEHTTPURL = "menu_httpUrl";
		String MENUPAGEPATH = "menu_path";
		String MENUPAGEFILENAME = "menuPage.jpg";
	}
	
	public interface RequestCode {
		String REGISTER = "0";
		String NEWS_FIRST = "10";
		String NEWS_LIST = "11";
		String NEW_INFO = "12";
		String CHANGE_NEWS_INFO = "13";
		String ZXTD_CHECK_INVITATION = "30";
		String ZXTD_REPORT_INVITATION = "31";
		String ZXTD_SUPPORT_INVITATION = "32";
		String ZXTD_REPLY_INVITATION = "33";
		String ZXTD_PUBLISH_NEWS = "41";
		String ZXTD_PUBLISH_NEWS_TYPE = "42";
		String ZXTD_PUBLISH_NEWS_ITEM_LIST = "43";
		String ZXTD_PUBLISH_NEWS_MENU = "44";
		String ZXTD_PUBLISH_NEWS_CHECK_INVITATION = "50";
		String ZXTD_PUBLISH_NEWS_REPORT_INVITATION = "51";
		String ZXTD_PUBLISH_NEWS_SUPPORT_INVITATION = "52";
		String ZXTD_PUBLISH_NEWS_REPLY_INVITATION = "53";
		String ZXTD_VIDEO_LIST = "60";
		String ZXTD_VIDEO_CHECK_INVITATION = "70";
		String ZXTD_VIDEO_REPORT_INVITATION = "71";
		String ZXTD_VIDEO_SUPPORT_INVITATION = "72";
		String ZXTD_VIDEO_REPLY_INVITATION = "73";
		String ZXTD_PUSH_NEWS = "80";
		String ZXTD_COVER = "100";
		
		//用户模块
		String MINE_REGIST="120";
		String MINE_LOGIN="110";
		String MINE_COMPLETE_USER_INFORMATION="122";
		String MINE_REQUEST_FANS="130";
		String MINE_REQUEST_FOCUS="132";
		String MINE_REQUEST_RECOMMEND="134";
		String MINE_ADD_FOCUS="131";
		String MINE_CANCEL_FOCUS="133";
		String MINE_GET_COLLECTION="150";
		String MINE_ADD_COLLECTION="151";
		String MINE_SYNCH_COLLECTION_DATA="153";
		String MINE_SYNCH_GET_COLLECTION_PAGE="154";
		String MINE_GOTO_SELFHOMEPAGE="121";
		String MINE_GOTO_OTHERHOMEPAGE="129";
		String MINE_GET_PUBLIC="170";
		String MINE_SYNCH_PUBLIC_DATA="174";
		String MINE_SYNCH_GET_PUBLIC_PAGER="175";
		String MINE_GET_PUBLISH_INFO = "176";
		String MINE_DELETE_COLLECTION="152";
		String MINE_DELETE_PUBLIC="173";
		String MINE_MODIF_USERINFO = "124";
		String MINE_DELETE_ALBUMS_HEAD="128";
		String MINE_MODIFY_HEAD="125";
		String MINE_GET_COMMENT="180";
		String MINE_GET_REPLY = "181";
		String MINE_MODIF_PASSWORD = "126";
		String MINE_MODIF_PORTRAIT = "125";
		String MINE_GET_ALL_FOCUS="135";
		String MINE_FIND_PASSWORD="111";
		String MINE_MODIFY_PUB = "172";
		
		String MINE_CACHE_GETVERSION="190";
		String MINE_SEARCH_USER="160";
	}

	public interface Separator {
		String SYMBOL = "\\$\\^\\$";
		String ITEM = ";";
		String ELEMENT = "~";
	}
	
	public interface CoverDataKey {
		String COVERID = "cover_id";
		String TIME = "cover_time";
		String COVERIMAGENAME = "coverImage.jpg";
		String COVERIMAGEPATH = "cover_local_image_path";
		String SCREENWIDTH = "screen_width";
		String SCREENHEIGHT = "screen_height";
	}
	
	public static final String SDCARD_CAMERA_PATH = "mnt/sdcard/DCIM/Camera/";
	public static final File IMAGE_CACHE_FILE = ZXTD_Application.getMyContext().getCacheDir();
}
