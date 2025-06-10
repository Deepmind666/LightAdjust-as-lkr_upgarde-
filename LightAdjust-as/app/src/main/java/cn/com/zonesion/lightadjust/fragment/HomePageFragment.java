package cn.com.zonesion.lightadjust.fragment;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.view.DialChartIlluminationView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * HomePageFragment是用来展示首页页面的Fragment
 */

public class HomePageFragment extends BaseFragment implements
		OnCheckedChangeListener {
	private TextView textIlluminationState;
	private DialChartIlluminationView dialChartIlluminationView;
	private TextView textLightState;
	private ImageView imageLightState;
	private Button openOrCloseLight;
	private TextView textSunshadeState;
	private ImageView imageSunshadeState;
	private Button openOrCloseSunshade;
	private SeekBar seekBarThreshold;
	private TextView textAmount;
	private RadioButton securityModule;
	private RadioButton manualModule;
	private TextView securityModuleTip;
	private TextView manualModuleTip;
	/**
	 * 除湿控制器,温湿度的MAC地址
	 */
	private String sensorAMac;
	private String sensorBMac;
	/**
	 * SharedPreferences实例，用于存取数据
	 */
	private SharedPreferences preferences;
	/**
	 * 用于存储数据
	 */
	private SharedPreferences.Editor editor;
	/**
	 * 用户设置的湿度上限，当大于这个上限的时候，打开除湿器
	 */
	private int temperatureUpperLimit;
	/**
	 * 当前的环境湿度
	 */
	private float currentTemperature;
	/**
	 * 当前是否处于自动模式的标识 true:处于自动模式 false:处于手动模式
	 */
	private boolean isSecurityMode = true;
	private int numResult1;
	
	private Handler handler;
	 private boolean state;

	/**
	 * 初始化用户的ID和KEY以及使用的服务器地址
	 */
	private void initSetting() {
		securityModule.setOnCheckedChangeListener(this);
		manualModule.setOnCheckedChangeListener(this);
		manualModule.setChecked(true);
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.home_page_layout, null);
		textIlluminationState = (TextView) view
				.findViewById(R.id.text_illumination_state);
		dialChartIlluminationView = (DialChartIlluminationView) view
				.findViewById(R.id.illumination_circle_view);
		textLightState = (TextView) view.findViewById(R.id.text_light_state);
		imageLightState = (ImageView) view.findViewById(R.id.image_light_state);
		openOrCloseLight = (Button) view.findViewById(R.id.open_or_close_light);
		textSunshadeState = (TextView) view
				.findViewById(R.id.text_sunshade_state);
		imageSunshadeState = (ImageView) view
				.findViewById(R.id.image_sunshade_state);
		openOrCloseSunshade = (Button) view
				.findViewById(R.id.open_or_close_sunshade);
		seekBarThreshold = (SeekBar) view.findViewById(R.id.seek_bar_threshold);
		textAmount = (TextView) view.findViewById(R.id.text_amount);
		securityModule = (RadioButton) view.findViewById(R.id.security_module);
		manualModule = (RadioButton) view.findViewById(R.id.manual_module);
		securityModuleTip = (TextView) view
				.findViewById(R.id.security_module_tip);
		manualModuleTip = (TextView) view.findViewById(R.id.manual_module_tip);
		return view;
	}

	@Override
	public void initData() {
		super.initData();
		preferences = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		editor = preferences.edit();
		handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                   
                	sensorAMac = preferences.getString("mac_a", null);
                	sensorBMac = preferences.getString("mac_b", null);
                    state = preferences.getBoolean("state", false);
                    if (sensorAMac == null && sensorBMac == null && !state) {
                    	Toast.makeText(mContext, "请绑定设备", Toast.LENGTH_SHORT).show();
                    }else{
                    	sendMessageTo(sensorAMac, "{A2=?}");
                    	sendMessageTo(sensorBMac, "{D1=?}");
                    }
                }
            }
        };
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        }, 0,30000);
		initSetting();
		// 设置SeekBar的监听事件，监听当SeekBar的进度改变的时候的响应事件
		seekBarThreshold
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int i,
							boolean fromUser) {
						temperatureUpperLimit = i;// SeekBar的进度即为湿度的上限值
						textAmount.setText(String.valueOf(i));
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						temperatureTooHigh();
						preferences
								.edit()
								.putInt("temperatureUpperLimit",
										temperatureUpperLimit).apply();
					}
				});
		seekBarThreshold.setProgress(preferences.getInt(
				"temperatureUpperLimit", 0));
		openOrCloseLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sensorBMac != null) {
					if (openOrCloseLight.getText().equals("开启")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{OD1=16,D1=?}");
							}
						}).start();

					}
					if (openOrCloseLight.getText().equals("关闭")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{CD1=16,D1=?}");
							}
						}).start();

					}
				} else {
					Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		openOrCloseSunshade.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sensorBMac != null) {
					if (openOrCloseSunshade.getText().equals("开启")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{OD1=4,D1=?}");
							}
						}).start();

					}
					if (openOrCloseSunshade.getText().equals("关闭")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{CD1=4,D1=?}");
							}
						}).start();

					}
				} else {
					Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * 当当前温度超过设置的温度上限值的时候，打开除湿器
	 */
	private void temperatureTooHigh() {
		if (sensorBMac != null) {
			if (isSecurityMode == true) {
				if (temperatureUpperLimit >= currentTemperature) {
					sendMessageTo(sensorBMac, "{OD1=16,D1=?}");
					if (numResult1 == 4) {
						sendMessageTo(sensorBMac, "{CD1=4,D1=?}");
					}
				} else {
					sendMessageTo(sensorBMac, "{CD1=16,D1=?}");
					if (numResult1 != 4) {
						sendMessageTo(sensorBMac, "{OD1=4,D1=?}");
					}
				}
			} else {
				Toast.makeText(mContext, "手动模式", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		// lcApplication.unregisterOnWSNDataListener(this);
		super.onDestroy();
	}

	/**
	 * 当服务器信息到达时回调的方法
	 * 
	 * @param mac
	 *            mac地址
	 * @param tag
	 *            数据的key值
	 * @param val
	 *            数据的value值
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void onZXBee(String mac, String tag, String val) {
		if (tag.equalsIgnoreCase("A2") && mac.equalsIgnoreCase(sensorAMac)) {
			textIlluminationState.setText("在线");
			if (isAdded()) {
				textIlluminationState.setTextColor(getResources().getColor(
						R.color.line_text_color));
			}
			currentTemperature = Float.parseFloat(val);
			dialChartIlluminationView.setCurrentStatus(currentTemperature);
			dialChartIlluminationView.invalidate();
		}
		if (tag.equalsIgnoreCase("D1") && mac.equalsIgnoreCase(sensorBMac)) {
			textLightState.setText("在线");
			if (isAdded()) {
				textLightState.setTextColor(getResources().getColor(
						R.color.line_text_color));
			}
			textSunshadeState.setText("在线");
			textSunshadeState.setTextColor(getResources().getColor(
					R.color.line_text_color));
			int numResult = Integer.parseInt(val);
			if ((numResult & 0X10) == 0x10) {
				openOrCloseLight.setText("关闭");
				if (isAdded()) {
					imageLightState.setImageDrawable(getResources().getDrawable(
							R.drawable.open_lamp));
					openOrCloseLight.setBackground(getResources().getDrawable(
							R.drawable.close));
				}
				
			} else {
				openOrCloseLight.setText("开启");
				if (isAdded()) {
					imageLightState.setImageDrawable(getResources().getDrawable(
							R.drawable.close_lamp));
					openOrCloseLight.setBackground(getResources().getDrawable(
							R.drawable.open));
				}
			}
			numResult1 = Integer.parseInt(val);
			if ((numResult1 & 0X04) == 0x04) {
				openOrCloseSunshade.setText("关闭");
				if (isAdded()) {
					imageSunshadeState.setImageDrawable(getResources().getDrawable(
							R.drawable.curtains_off));
					openOrCloseSunshade.setBackground(getResources().getDrawable(
							R.drawable.close));
				}
			} else {
				openOrCloseSunshade.setText("开启");
				if (isAdded()) {
					imageSunshadeState.setImageDrawable(getResources().getDrawable(
							R.drawable.curtains_on));
					openOrCloseSunshade.setBackground(getResources().getDrawable(
							R.drawable.open));
				}
				
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.security_module:
			if (isChecked) {
				isSecurityMode = true;
				openOrCloseLight.setEnabled(false);
				openOrCloseSunshade.setEnabled(false);
				securityModuleTip.setVisibility(View.VISIBLE);
				manualModuleTip.setVisibility(View.GONE);
			}
			break;
		case R.id.manual_module:
			if (isChecked) {
				isSecurityMode = false;
				openOrCloseLight.setEnabled(true);
				openOrCloseSunshade.setEnabled(true);
				securityModuleTip.setVisibility(View.GONE);
				manualModuleTip.setVisibility(View.VISIBLE);
			}
			break;
		}
	}
}
