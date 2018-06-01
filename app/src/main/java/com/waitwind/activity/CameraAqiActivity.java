package com.waitwind.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scene.net.Net;
import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.ImageTools;
import com.waitwind.utils.OkHttpUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 拍一拍测AQI
 */

public class CameraAqiActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;//返回按钮
	private TextView tvTitle = null;
	private TextView tvAqiText = null;//aqi等级描述
	private LinearLayout llCamera = null;//拍照按钮
	private ImageView imageView = null;
	private String url = "http://new.scapi.tianqi.cn/pm25AndVis/VisPmForApp.php";//图片上传接口
	private TextView tvAqi = null;
	private TextView tvVisible = null;
	private ProgressBar progressBar = null;
	private static final int SCALE = 5;//照片缩小比例

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_aqi);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("拍一拍测AQI");
		tvAqiText = (TextView) findViewById(R.id.tvAqiText);
		llCamera = (LinearLayout) findViewById(R.id.llCamera);
		llCamera.setOnClickListener(this);
		imageView = (ImageView) findViewById(R.id.imageView);
		tvAqi = (TextView) findViewById(R.id.tvAqi);
		tvVisible = (TextView) findViewById(R.id.tvVisible);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int imgWidth = (int)(dm.widthPixels-40*dm.density);
		int imgHeight = imgWidth*3/4;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
		imageView.setLayoutParams(params);

		Animation animation = new RotateAnimation(0, 10);
		animation.setDuration(0);
		animation.setFillAfter(true);
		tvAqiText.setAnimation(animation);
	}
	
	/**
	 * 拍摄对话框
	 */
	private void cameraDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.camera_dialog, null);
		RelativeLayout reCamera = (RelativeLayout) view.findViewById(R.id.reCamera);
		RelativeLayout reSelect = (RelativeLayout) view.findViewById(R.id.reSelect);
		RelativeLayout reCancel = (RelativeLayout) view.findViewById(R.id.reCancel);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.getWindow().setGravity(Gravity.CENTER|Gravity.BOTTOM);
		dialog.show();
		
		reCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				getCamera();
			}
		});
		
		reSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				getAlbum();
			}
		});
		
		reCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 调取系统相机
	 */
	private void getCamera() {
		Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + CONST.AQI_ADDR));  
		Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
		startActivityForResult(intentCamera, 0);
	}
	
	/**
	 * 获取相册
	 */
	private void getAlbum() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "false");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
	}

	/**
	 * 上传图片
	 * @param url 接口地址
	 */
	private void uploadPortrait(String url) {
		progressBar.setVisibility(View.VISIBLE);
		tvAqi.setText("");
		tvAqiText.setText("");
		tvAqiText.setVisibility(View.GONE);
		tvVisible.setText("");

		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		File file = new File(Environment.getExternalStorageDirectory() + CONST.AQI_ADDR);
		if (file != null) {
			RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
			builder.addFormDataPart("imgfile", file.getName(), body);
			builder.addFormDataPart("image", file.getName());
		}
		OkHttpUtil.enqueue(new Request.Builder().post(builder.build()).url(url).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String result = response.body().string();
				Log.e("result", result);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							progressBar.setVisibility(View.GONE);
							try {
								JSONObject obj = new JSONObject(result);
								if (!obj.isNull("AQI")) {
									tvAqi.setText(getString(R.string.aqi_level) + obj.getString("AQI"));
									tvAqi.setGravity(Gravity.CENTER_HORIZONTAL);
									tvAqiText.setText(getString(R.string.aqi_level) + obj.getString("AQI"));
									tvAqiText.setVisibility(View.VISIBLE);
								}
								if (!obj.isNull("visibility")) {
									tvVisible.setText(getString(R.string.visibility) + obj.getString("visibility"));
									tvVisible.setGravity(Gravity.CENTER_HORIZONTAL);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			finish();
			break;
		case R.id.llCamera:
			cameraDialog();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				//将保存在本地的图片取出并缩小后显示在界面上  
                Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + CONST.AQI_ADDR);  
                Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);  
                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常  
                bitmap.recycle();  
                  
                //将处理过的图片显示在界面上，并保存到本地  
                imageView.setImageBitmap(newBitmap);  
                ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), CONST.AQI_ADDR);  
                uploadPortrait(url);
				break;
			case 1:
				ContentResolver resolver = getContentResolver();
				//照片的原始资源地址
				Uri originalUri = data.getData(); 
	            try {
	            	//使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					if (photo != null) {
						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
						//释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						
						imageView.setImageBitmap(smallBitmap);
		                ImageTools.savePhotoToSDCard(smallBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), CONST.AQI_ADDR);
		                uploadPortrait(url);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}  
				break;

			default:
				break;
			}
		}
	}
	
}
