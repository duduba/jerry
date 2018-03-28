package com.zxtd.information.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.zxtd.information.ui.R;

public class ExpressionUtil {

	public static void dealContent(Context context,String content,TextView view,boolean isChat){
		view.setText("");
		SpannableString spannableString = new SpannableString(content);                                      
		String regex="\\[(f0[0-9]{2}|f10[0-5])\\]";
		Pattern patten = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = patten.matcher(spannableString);  
		List<Object> list=new ArrayList<Object>();
		try{
			  while(matcher.find()) { 
		            String key = matcher.group();  
		            String temp=key.replace("[", "").replace("]", "");
		            Field field = R.drawable.class.getDeclaredField(temp);  
		            int resId = Integer.parseInt(field.get(null).toString()); 
		            if(resId>0){
		            	int index=content.indexOf(key);
						if(index!=-1){
							if(index==0){
								SpannableString text=getSpan(context,resId,isChat);
								list.add(text);
								content=content.substring(key.length()+index);
							}else{
								list.add(content.substring(0, index));
								content=content.substring(key.length()+index);
								SpannableString text=getSpan(context,resId,isChat);
								list.add(text);
							}
						}
		            }  
		      }
				if(list.isEmpty()){
					list.add(content);
				}else{
					if(content.length()>0){
						list.add(content);
					}
				}
				Iterator<Object> it=list.iterator();
				while(it.hasNext()){
					Object obj=it.next();
					if(obj instanceof SpannableString){
						view.append((SpannableString)obj);
					}else{
						view.append(obj.toString());
					}
				}
		}catch(Exception ex){
			Utils.printException(ex);
		}
	}
	
	
	private static SpannableString getSpan(Context context,int faceId,boolean isChat){
		Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), faceId);
		ImageSpan imageSpan=new ImageSpan(bitmap);
		SpannableString text=new SpannableString(faceId+"");
		if(isChat){
			//text.setSpan(new AbsoluteSizeSpan(20), 4, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//text.setSpan(new RelativeSizeSpan(2.0f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
		}
		text.setSpan(imageSpan, 0, (faceId+"").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return text;
	}
	
}
