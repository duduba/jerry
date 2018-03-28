package com.zxtd.information.db;

import com.zxtd.information.application.ZXTD_Application;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class zxtd_DBhelper extends SQLiteOpenHelper {

	private static String DBname = "down.db";
	private static int version = 7;
	
	
	public static final String TB_FOCUS="tb_focus";
	public static final String TB_SESSION="tb_session";
	public static final String TB_MESSAGE="tb_message";
	public static final String TB_MINE="tb_mine";
	public static final String TB_VERSION="tb_version";
	public static final String TB_PUBLIC="tb_pubilc";
	public static final String TB_COLLECTION="tb_collection";
	public static final String TB_IMAGES="tb_images";
	public static final String TB_COMMENT="tb_comment";
	//public static final String TB_SMALL_IMAGES="tb_small_images";
	
	public zxtd_DBhelper(){
		super(ZXTD_Application.getMyContext(), DBname, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sqlDownloadInfo = "create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id text, start_pos integer, " +
				"end_pos integer, compelete_size integer,url text, status integer, filename text)";
		String sqlUserNickName = "create table user_nick_name(_id integer PRIMARY KEY AUTOINCREMENT, nick_name text)";
		String sqlPublish = "create table publish_info(_id integer PRIMARY KEY AUTOINCREMENT, title varchar, content varchar, typeid char, typename varchar, imageurls varchar)";
		String sqlImageCache = "create table image_cache(_id integer PRIMARY KEY AUTOINCREMENT, image_file varchar, image_url varchar)";
		String sqlPosterImageInfo = "create table poster_info(_id integer PRIMARY KEY AUTOINCREMENT, poster_id varchar, http_url varchar, " + 
		"imagefile_name varchar, image_path varchar)";
		
		
		String sqlFocus="create table "+TB_FOCUS+"(_id integer PRIMARY KEY AUTOINCREMENT,focusId integer,focusName text,focusSign text,focusImg text," +
				"focusStatus integer,userId integer)";
		
		String sqlSession="create table "+TB_SESSION+"(sessionId integer PRIMARY KEY AUTOINCREMENT,chatId varchar(64),chatName varchar(225),chatImg varchar(1000),chatSign varchar(1000)," +
				"lastMsg text,lastReceive varchar(20),selfId varchar(64),state integer,createDate varchar(20),noReadCount integer,sortIndex integer)";
		
		String sqlMessage="create table "+TB_MESSAGE+"(msgId integer PRIMARY KEY AUTOINCREMENT,sessionId integer,msgContext text,msgType integer,msgTime varchar(20),msgFrom varchar(64),msgState integer," +
				"msgAtta varchar(500),isSelf integer,voiceSecend integer)";
		
		
		String sqlMine="create table "+TB_MINE+"(userId integer PRIMARY KEY,userAccount varchar(100),userPassword varchar(100),nickName varchar(100),header varchar(500)," +
				"signText text,location varchar(100),job varchar(100),sex varchar(4),birth varchar(20),fansCount integer,fansHasNew integer," +
				"focusCount integer,publicCount integer,publicHasNew integer,commentCount integer,hasNewComment integer,collectionCount integer,letterCount integer,accountType integer)";
		
		String sqlVersion="create table "+TB_VERSION+"(versionType varchar(100),versionNum integer,userId integer)";
		
		String sqlPublic="create table "+TB_PUBLIC+"(autoId integer PRIMARY KEY AUTOINCREMENT,newsId integer,newsTitle text,content text,publicTime varchar(20),replayCount integer," +
				"channel varchar(10),newsUrl varchar(1000),hasNewReplay integer,userId integer)";
		
		String sqlCollection="create table "+TB_COLLECTION+"(autoId integer PRIMARY KEY AUTOINCREMENT,newsId integer,newsTitle text,newSummary text,replayCount integer,newsType integer," +  //0新闻  1网友
				"newsUrl varchar(1000),userId integer)";
		
		
		String sqlMineImage="create table "+TB_IMAGES+"(imageId integer PRIMARY KEY,imageUrl text,smallUrl text,imageDesc varchar(100),userId integer)";
		
		String sqlComment="create table "+TB_COMMENT+"(newsId integer,nickName varchar(100),content text,commentDate varchar(20),floorNum varchar(10),newsType integer,userId newsType)";
		
		
		arg0.execSQL(sqlDownloadInfo);
		arg0.execSQL(sqlUserNickName);
		arg0.execSQL(sqlPublish);
		arg0.execSQL(sqlImageCache);
		arg0.execSQL(sqlPosterImageInfo);
		
		
		arg0.execSQL(sqlFocus);
		arg0.execSQL(sqlSession);
		arg0.execSQL(sqlMessage);
		arg0.execSQL(sqlMine);
		arg0.execSQL(sqlVersion);
		arg0.execSQL(sqlPublic);
		arg0.execSQL(sqlCollection);
		arg0.execSQL(sqlMineImage);
		arg0.execSQL(sqlComment);
		//arg0.execSQL(sqlMineSmallImage);
	}
	
	private void changeDB_6(SQLiteDatabase arg0){
		String deletePublish = "DROP TABLE if exists publish_info";
		String sqlPublish = "create table publish_info(_id integer PRIMARY KEY AUTOINCREMENT, title varchar, content varchar, typeid char, typename varchar, imageurls varchar)";
		arg0.execSQL(deletePublish);
		arg0.execSQL(sqlPublish);
		
		String sqlFocus="create table "+TB_FOCUS+"(_id integer PRIMARY KEY AUTOINCREMENT,focusId integer,focusName text,focusSign text,focusImg text," +
				"focusStatus integer,userId integer)";
		
		String sqlSession="create table "+TB_SESSION+"(sessionId integer PRIMARY KEY AUTOINCREMENT,chatId varchar(64),chatName varchar(225),chatImg varchar(1000),chatSign varchar(1000)," +
				"lastMsg text,lastReceive varchar(20),selfId varchar(64),state integer,createDate varchar(20),noReadCount integer,sortIndex integer)";
		
		String sqlMessage="create table "+TB_MESSAGE+"(msgId integer PRIMARY KEY AUTOINCREMENT,sessionId integer,msgContext text,msgType integer,msgTime varchar(20),msgFrom varchar(64),msgState integer," +
				"msgAtta varchar(500),isSelf integer,voiceSecend integer)";
		
		
		String sqlMine="create table "+TB_MINE+"(userId integer PRIMARY KEY,userAccount varchar(100),userPassword varchar(100),nickName varchar(100),header varchar(500)," +
				"signText text,location varchar(100),job varchar(100),sex varchar(4),birth varchar(20),fansCount integer,fansHasNew integer," +
				"focusCount integer,publicCount integer,publicHasNew integer,commentCount integer,hasNewComment integer,collectionCount integer,letterCount integer,accountType integer)";
		
		String sqlVersion="create table "+TB_VERSION+"(versionType varchar(100),versionNum integer,userId integer)";
		
		String sqlPublic="create table "+TB_PUBLIC+"(autoId integer PRIMARY KEY AUTOINCREMENT,newsId integer,newsTitle text,content text,publicTime varchar(20),replayCount integer," +
				"channel varchar(10),newsUrl varchar(1000),hasNewReplay integer,userId integer)";
		
		String sqlCollection="create table "+TB_COLLECTION+"(autoId integer PRIMARY KEY AUTOINCREMENT,newsId integer,newsTitle text,newSummary text,replayCount integer,newsType integer," +  //0新闻  1网友
				"newsUrl varchar(1000),userId integer)";
		
		
		String sqlMineImage="create table "+TB_IMAGES+"(imageId integer PRIMARY KEY,imageUrl text,smallUrl text,imageDesc varchar(100),userId integer)";
		
		String sqlComment="create table "+TB_COMMENT+"(newsId integer,nickName varchar(100),content text,commentDate varchar(20),floorNum varchar(10),newsType integer,userId newsType)";
		
		arg0.execSQL(sqlFocus);
		arg0.execSQL(sqlSession);
		arg0.execSQL(sqlMessage);
		arg0.execSQL(sqlMine);
		arg0.execSQL(sqlVersion);
		arg0.execSQL(sqlPublic);
		arg0.execSQL(sqlCollection);
		arg0.execSQL(sqlMineImage);
		arg0.execSQL(sqlComment);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		if(arg1 < 7){
			changeDB_6(arg0);
		}
	}

}
