package com.liangxiao.sendmessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangxiao.sendmessage.R.id;
import com.liangxiao.sendmessage.toast.BaseActivity;
import com.liangxiao.sendmessage.toast.myToast;
import com.thinkland.sdk.sms.SMSCaptcha;
import com.thinkland.sdk.util.BaseData.ResultCallBack;

public class MainActivity2 extends BaseActivity implements OnClickListener,
		TextWatcher {
	private static final String TAG = "接收验证码";
	private static final int UPDATE_TIME = 60;
	private String phoneNum1;
	private String formatedPhone;
	private int time = UPDATE_TIME;

	private TextView tv_identify_notify;// 我们正在发送信息部分
	private TextView tv_phone;// 手机号
	private EditText et_put_identify;// 验证码
	private ImageView iv_clear;
	private TextView tv_unreceive_identify;// 刷新时间部分
	private Button btn_submit;// 提交
	private SMSCaptcha smsCaptcha;
	private Handler handler;
	private Message msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		smsCaptcha = SMSCaptcha.getInstance();
		handler = new myToast(MainActivity2.this);
		// 获取手机号
		Intent intent = getIntent();
		formatedPhone = intent.getStringExtra("formatedPhone");
		phoneNum1 = intent.getStringExtra("phoneNum1");

		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		btn_submit.setEnabled(false);

		// 验证码change事件
		et_put_identify = (EditText) findViewById(R.id.et_put_identify);
		et_put_identify.addTextChangedListener(this);

		// 我们已经发送过去了
		tv_identify_notify = (TextView) findViewById(R.id.tv_identify_notify);
		String text = getResources().getString(
				R.string.smssdk_make_sure_mobile_detail);
		tv_identify_notify.setText(Html.fromHtml(text));

		// 手机号显示部分
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_phone.setText(formatedPhone);

		// 刷新时间部分
		tv_unreceive_identify = (TextView) findViewById(R.id.tv_unreceive_identify);
		String unReceive = getResources().getString(
				R.string.smssdk_receive_msg, time);
		tv_unreceive_identify.setText(Html.fromHtml(unReceive));

		tv_unreceive_identify.setOnClickListener(this);
		tv_unreceive_identify.setEnabled(false);

		iv_clear = (ImageView) findViewById(id.iv_clear);
		iv_clear.setOnClickListener(this);

		// 60seconds
		countDown();

	}

	private void countDown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (time-- > 0) {
					// 接收短信大约需要60seconds
					String unReceive = MainActivity2.this.getResources()
							.getString(R.string.smssdk_receive_msg, time);
					// 刷新
					updaTvunReceive1(unReceive);
					Log.v("time is about ", unReceive);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String unReceive = getResources().getString(
						R.string.smssdk_unreceive_identify_code, time);
				updaTvunReceive2(unReceive);
				time = UPDATE_TIME;
			}
		}).start();
	}

	/**
	 * 刷新操作1
	 * 
	 * @param unReceive
	 */

	private void updaTvunReceive1(final String unReceive) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				tv_unreceive_identify.setText(Html.fromHtml(unReceive));
				tv_unreceive_identify.setEnabled(false);

			}
		});
	}

	/**
	 * 刷新操作2
	 * 
	 * @param unReceive
	 */

	private void updaTvunReceive2(final String unReceive) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				tv_unreceive_identify.setText(Html.fromHtml(unReceive));
				tv_unreceive_identify.setEnabled(true);

			}
		});
	}

	@Override
	public void onBackPressed() {
		shoNotifyDialog();
	}

	private void shoNotifyDialog() {
		String strContent = this.getResources().getString(
				R.string.smssdk_close_identify_page_dialog);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity2.this);
		builder.setTitle("提示框")
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setMessage(strContent)
				.setPositiveButton(R.string.smssdk_wait,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.smssdk_back,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
		builder.create().show();
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int after, int count) {
		if (s.length() > 0) {
			btn_submit.setEnabled(true);
			iv_clear.setVisibility(View.VISIBLE);
			btn_submit.setBackgroundResource(R.drawable.smssdk_btn_enable);
		} else {
			btn_submit.setEnabled(false);
			iv_clear.setVisibility(View.GONE);
			btn_submit.setBackgroundResource(R.drawable.smssdk_btn_disenable);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_clear:
			et_put_identify.getText().clear();
			break;
		case R.id.btn_submit:
			showDialog(getResources().getString(
					R.string.smssdk_get_verification_code_content));
			// 验证码
			String verificationCode = et_put_identify.getText().toString()
					.trim();
			smsCaptcha.commitCaptcha(phoneNum1, verificationCode,
					new ResultCallBack() {

						@Override
						public void onResult(int code, String reason,
								String result) {
							closeDialog();
							Log.v(TAG, "code:" + code + ";resason:" + reason
									+ ";result:" + result);
							if (code == 0) {
								finish();
								msg = handler.obtainMessage();
								msg.arg1 = 3;
								handler.sendMessage(msg);
							} else {
								msg = handler.obtainMessage();
								msg.arg1 = 7;
								handler.sendMessage(msg);
							}
						}
					});

			break;
		case R.id.tv_unreceive_identify:
			// 再次获取验证码
			showDialogMessage();
			break;
		default:
			break;
		}
	}

	/**
	 * 再次获取验证码
	 */
	private void showDialogMessage() {
		// 重新获取验证码
		String strContent = getResources().getString(
				R.string.smssdk_resend_identify_code);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity2.this);
		builder.setTitle("提示框")
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setMessage(strContent)
				.setPositiveButton(R.string.smssdk_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showDialog(getResources()
										.getString(
												R.string.smssdk_get_verification_code_content));

								smsCaptcha.sendCaptcha(phoneNum1.trim(),
										new ResultCallBack() {

											@Override
											public void onResult(int code,
													String reason, String result) {
												closeDialog();
												msg = handler.obtainMessage();
												msg.arg1 = 3;
												handler.sendMessage(msg);
												Log.v(TAG, "code:" + code
														+ ";reason:" + reason
														+ ";result:" + result);
											}
										});
							}
						})
				.setNegativeButton(R.string.smssdk_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// finish();
							}
						});

		AlertDialog dialog = builder.create();
		dialog.show();

	}
}
