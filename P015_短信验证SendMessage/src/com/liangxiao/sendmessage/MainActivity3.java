package com.liangxiao.sendmessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.liangxiao.sendmessage.toast.BaseActivity;
import com.liangxiao.sendmessage.toast.myToast;
import com.thinkland.sdk.sms.SMSCaptcha;
import com.thinkland.sdk.util.BaseData.ResultCallBack;

public class MainActivity3 extends BaseActivity implements OnClickListener,
		TextWatcher {
	private static final String TAG = "接收验证码";
	private static final int UPDATE_TIME = 60;
	private String phone;
	private String verificationCode;
	private int time = UPDATE_TIME;
	private EditText et_write_phone;// 手机号
	private ImageView iv_clear1;// 清空1
	private EditText et_put_identify;// 验证码
	// private ImageView iv_clear2;// 清空3
	private Button btn_unreceive_identify;// 刷新
	private Button btn_submit;// 完成
	private Handler handler;
	private Message msg;
	private int i;
	private SMSCaptcha smsCaptcha;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		handler = new myToast(MainActivity3.this);
		smsCaptcha = SMSCaptcha.getInstance();
		initView();

	}

	private void initView() {
		et_write_phone = (EditText) findViewById(R.id.et_write_phone);
		et_write_phone.addTextChangedListener(this);
		iv_clear1 = (ImageView) findViewById(R.id.iv_clear1);
		et_put_identify = (EditText) findViewById(R.id.et_put_identify);
		et_put_identify.addTextChangedListener(this);
		// iv_clear2 = (ImageView) findViewById(R.id.iv_clear2);
		btn_unreceive_identify = (Button) findViewById(R.id.btn_unreceive_identify);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		handlerMessage(i);

		btn_unreceive_identify.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		iv_clear1.setOnClickListener(this);
		// iv_clear2.setOnClickListener(this);

		btn_unreceive_identify.setEnabled(true);
		btn_unreceive_identify
				.setBackgroundResource(R.drawable.smssdk_btn_enable);
		btn_submit.setEnabled(false);
		btn_submit.setBackgroundResource(R.drawable.smssdk_btn_disenable);

		// phone = et_write_phone.getText().toString().trim();
		// verificationCode = et_put_identify.getText().toString().trim();
		et_write_phone.setText("");
		et_write_phone.requestFocus();

		// if (et_write_phone.getText().length() > 0) {
		// btn_submit.setEnabled(true);
		// iv_clear1.setVisibility(View.VISIBLE);
		// btn_submit.setBackgroundResource(R.drawable.smssdk_btn_enable);
		// }
	}

	private void handlerMessage(int i) {
		msg = handler.obtainMessage();
		msg.arg1 = i;
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_unreceive_identify:
			// 刷新
			if (!et_write_phone.getText().toString().trim().equals("")) {
				if (btn_unreceive_identify.getText().toString().equals("获取")) {
					getVerificationCode();
				} else {
					showDialogMessage();
				}
			} else {
				handlerMessage(6);
			}

			break;
		case R.id.btn_submit:
			// 完成部分
			Submit();
			break;
		case R.id.iv_clear1:
			et_write_phone.getText().clear();
			et_put_identify.getText().clear();
			break;
		// case R.id.iv_clear2:
		// et_put_identify.getText().clear();
		// break;
		default:
			break;
		}
	}

	/**
	 * 获取验证码
	 */
	private void getVerificationCode() {
		showDialog(getResources().getString(
				R.string.smssdk_get_verification_code_content));

		smsCaptcha.sendCaptcha(et_write_phone.getText().toString().trim(),
				new ResultCallBack() {

					@Override
					public void onResult(int code, String reason, String result) {
						closeDialog();
						countDown();
						msg = handler.obtainMessage();
						msg.arg1 = 3;
						handler.sendMessage(msg);
						Log.v(TAG, "code:" + code + ";reason:" + reason
								+ ";result:" + result);
					}
				});
	}

	private void Submit() {
		if (!et_write_phone.getText().toString().trim().equals("")
				&& !et_put_identify.getText().toString().trim().equals("")) {
			phone = et_write_phone.getText().toString().trim();
			verificationCode = et_put_identify.getText().toString().trim();
			showDialog(getResources().getString(R.string.smssdk_loading));
			smsCaptcha.commitCaptcha(phone, verificationCode,
					new ResultCallBack() {

						@Override
						public void onResult(int code, String reason,
								String result) {
							closeDialog();
							Log.v(TAG, "code:" + code + ";resason:" + reason
									+ ";result:" + result);
							if (code == 0) {
								handlerMessage(3);
							} else {
								handlerMessage(7);
							}
						}
					});
		} else {
			handlerMessage(2);
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
				MainActivity3.this);
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

								smsCaptcha.sendCaptcha(et_write_phone.getText()
										.toString().trim(),
										new ResultCallBack() {

											@Override
											public void onResult(int code,
													String reason, String result) {
												closeDialog();
												countDown();
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

	/**
	 * 读秒部分
	 */
	private void countDown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (time-- > 0) {
					// 接收短信大约需要60seconds
					String unReceive = MainActivity3.this.getResources()
							.getString(R.string.smssdk_receive_msg1, time);
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
						R.string.smssdk_unreceive_identify_code1, time);
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

				btn_unreceive_identify.setText(Html.fromHtml(unReceive));
				btn_unreceive_identify.setEnabled(false);
				btn_unreceive_identify
						.setBackgroundResource(R.drawable.smssdk_btn_disenable);
				et_write_phone.setEnabled(false);
				iv_clear1.setVisibility(View.GONE);
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

				btn_unreceive_identify.setText(Html.fromHtml(unReceive));
				btn_unreceive_identify.setEnabled(true);
				btn_unreceive_identify
						.setBackgroundResource(R.drawable.smssdk_btn_enable);
				et_write_phone.setEnabled(true);
				iv_clear1.setVisibility(View.VISIBLE);
			}
		});
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
		if (et_write_phone.getText().toString().trim().equals("")) {
			btn_unreceive_identify.setText("获取");
		}

		if (s.length() > 0) {
			btn_submit.setEnabled(true);
			iv_clear1.setVisibility(View.VISIBLE);
			btn_submit.setBackgroundResource(R.drawable.smssdk_btn_enable);
		} else {
			btn_submit.setEnabled(false);
			iv_clear1.setVisibility(View.GONE);
			btn_submit.setBackgroundResource(R.drawable.smssdk_btn_disenable);
		}

	}
}
