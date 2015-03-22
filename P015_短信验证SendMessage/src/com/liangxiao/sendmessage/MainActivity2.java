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
	private static final String TAG = "������֤��";
	private static final int UPDATE_TIME = 60;
	private String phoneNum1;
	private String formatedPhone;
	private int time = UPDATE_TIME;

	private TextView tv_identify_notify;// �������ڷ�����Ϣ����
	private TextView tv_phone;// �ֻ���
	private EditText et_put_identify;// ��֤��
	private ImageView iv_clear;
	private TextView tv_unreceive_identify;// ˢ��ʱ�䲿��
	private Button btn_submit;// �ύ
	private SMSCaptcha smsCaptcha;
	private Handler handler;
	private Message msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		smsCaptcha = SMSCaptcha.getInstance();
		handler = new myToast(MainActivity2.this);
		// ��ȡ�ֻ���
		Intent intent = getIntent();
		formatedPhone = intent.getStringExtra("formatedPhone");
		phoneNum1 = intent.getStringExtra("phoneNum1");

		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		btn_submit.setEnabled(false);

		// ��֤��change�¼�
		et_put_identify = (EditText) findViewById(R.id.et_put_identify);
		et_put_identify.addTextChangedListener(this);

		// �����Ѿ����͹�ȥ��
		tv_identify_notify = (TextView) findViewById(R.id.tv_identify_notify);
		String text = getResources().getString(
				R.string.smssdk_make_sure_mobile_detail);
		tv_identify_notify.setText(Html.fromHtml(text));

		// �ֻ�����ʾ����
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_phone.setText(formatedPhone);

		// ˢ��ʱ�䲿��
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
					// ���ն��Ŵ�Լ��Ҫ60seconds
					String unReceive = MainActivity2.this.getResources()
							.getString(R.string.smssdk_receive_msg, time);
					// ˢ��
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
	 * ˢ�²���1
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
	 * ˢ�²���2
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
		builder.setTitle("��ʾ��")
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
			// ��֤��
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
			// �ٴλ�ȡ��֤��
			showDialogMessage();
			break;
		default:
			break;
		}
	}

	/**
	 * �ٴλ�ȡ��֤��
	 */
	private void showDialogMessage() {
		// ���»�ȡ��֤��
		String strContent = getResources().getString(
				R.string.smssdk_resend_identify_code);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity2.this);
		builder.setTitle("��ʾ��")
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
