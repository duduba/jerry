package com.zxtd.information.view;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

public class ShowPictureDialog extends Dialog {
	private ImageView showImage;
	private Button mBtnConfirm;
	private Button mBtnCancel;
	
	private Bitmap bitmap;
	private String mFileName = null;
	
	private OnIsOkListener mOnIsOkListener;
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(v == mBtnCancel){
				ShowPictureDialog.this.dismiss();
			}else if(v == mBtnConfirm){
				if(mOnIsOkListener != null){
					mOnIsOkListener.isOk(mFileName);
				}
				ShowPictureDialog.this.dismiss();
			}
		}
	};
	
	public interface OnIsOkListener{
		void isOk(String fileName);
	}
	
	public ShowPictureDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public ShowPictureDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ShowPictureDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnIsOkListener(OnIsOkListener isOkListener){
		mOnIsOkListener = isOkListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.show_pictrue_dialog);
		
		setDialogStyle();
		
		showImage = (ImageView) this.findViewById(R.id.publist_image_show);
		mBtnConfirm = (Button) this.findViewById(R.id.btn_confirm);
		mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
		
		mBtnConfirm.setOnClickListener(clickListener);
		mBtnCancel.setOnClickListener(clickListener);
		this.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				if(bitmap != null){
					bitmap.recycle();
				}
			}
		});
	}
	private void setDialogStyle() {
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT; // 设置对话框宽度
		params.height = LayoutParams.FILL_PARENT; // 设置对话框高度
		//params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		//params.gravity = Gravity.BOTTOM;
		//params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
	
	public void show(String imageFile){
		super.show();
		bitmap = Utils.decodeSampledBitmapFromResource(imageFile, Utils.getDisplayMetrics().widthPixels, 600);
		showImage.setImageBitmap(bitmap);
		mFileName = imageFile;
	}

	
}
