package com.liangxiao.sendmessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangxiao.sendmessage.toast.BaseActivity;
import com.liangxiao.sendmessage.toast.myToast;
import com.thinkland.sdk.sms.SMSCaptcha;
import com.thinkland.sdk.util.BaseData.ResultCallBack;

public class MainActivity extends BaseActivity implements OnClickListener,
		TextWatcher {
	private EditText et_content;
	private TextView tv_country_num;
	private ImageView iv_clear;
	private Button btn_next;
	private SMSCaptcha smsCaptcha;
	private Handler handler;
	private Message msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		smsCaptcha = SMSCaptcha.getInstance();
		initView();

	}

	private void initView() {
		handler = new myToast(MainActivity.this);
		btn_next = (Button) findViewById(R.id.btn_next);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		tv_country_num = (TextView) findViewById(R.id.tv_country_num);
		et_content = (EditText) findViewById(R.id.et_write_phone);
		et_content.addTextChangedListener(this);
		et_content.setText("");
		et_content.requestFocus();
		if (et_content.getText().length() > 0) {
			btn_next.setEnabled(true);
			iv_clear.setVisibility(View.VISIBLE);
			btn_next.setBackgroundResource(R.drawable.smssdk_btn_enable);
		}

		btn_next.setOnClickListener(this);
		iv_clear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			// 得到+86加手机号
			String code = tv_country_num.getText().toString().trim();
			String phoneNum = et_content.getText().toString().trim();
			// 检查开始
			checkPhoneNum(phoneNum, code);
			break;
		case R.id.iv_clear:
			et_content.getText().clear();
			break;
		default:
			break;
		}
	}

	/**
	 * 检查手机号部分
	 * 
	 * @param phoneNum
	 * @param code
	 */
	private void checkPhoneNum(String phoneNum, String code) {
		// 判断不能为空手机号
		if (TextUtils.isEmpty(phoneNum)) {
			msg = handler.obtainMessage();
			msg.arg1 = 6;
			handler.sendMessage(msg);
			return;
		}
		showDialogs(phoneNum, code);
	}

	public void showDialogs(final String phoneNum, String code) {
		String phoneNums = code + " " + splitPhoneNum(phoneNum);
		// 我们将发送验证码短信到这个号码：phoneNums
		String strContent = getResources().getString(
				R.string.smssdk_make_sure_mobile_detail)
				+ phoneNums;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("提示框")
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setMessage(strContent)
				.setPositiveButton(R.string.smssdk_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// loading获取中ing
								showDialog(getResources()
										.getString(
												R.string.smssdk_get_verification_code_content));
								smsCaptcha.sendCaptcha(phoneNum,
										new ResultCallBack() {

											@Override
											public void onResult(int code,
													String reason, String result) {
												closeDialog();
												// 跳转到验证码Activity
												if (code == 0) {
													String phoneNum1 = et_content
															.getText()
															.toString()
															.trim()
															.replace("\\s*", "");
													String code1 = tv_country_num
															.getText()
															.toString().trim();
													// 格式化过的手机号
													String formatedPhone = code1
															+ " "
															+ splitPhoneNum(phoneNum1);
													// msg = handler
													// .obtainMessage();
													// msg.arg1 = 3;
													// handler.sendMessage(msg);
													Intent intent = new Intent(
															MainActivity.this,
															MainActivity2.class);
													intent.putExtra(
															"formatedPhone",
															formatedPhone);
													intent.putExtra("phoneNum1",
															phoneNum1);
													startActivity(intent);
												}
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
							}
						});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}

		builder.reverse();
		return builder.toString();

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 判断输入框的状态显示部分
		if (s.length() > 0) {
			btn_next.setEnabled(true);
			iv_clear.setVisibility(View.VISIBLE);
			btn_next.setBackgroundResource(R.drawable.smssdk_btn_enable);
		} else {
			btn_next.setEnabled(false);
			iv_clear.setVisibility(View.GONE);
			btn_next.setBackgroundResource(R.drawable.smssdk_btn_disenable);
		}
	}
}
