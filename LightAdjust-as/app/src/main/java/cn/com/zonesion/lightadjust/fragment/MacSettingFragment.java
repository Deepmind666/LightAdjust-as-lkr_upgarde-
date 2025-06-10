package cn.com.zonesion.lightadjust.fragment;

import com.example.qr_codescan.MipcaActivityCapture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.activity.MacShareActivity;
import cn.com.zonesion.lightadjust.bean.MacBean;
/**
 * MacSettingFragment是用户设置被检测项的MAC地址的时候所展示的页面，由多个TextView、EditText和Button组成
 * 用户可以手动设置被检测项的MAC地址
 */

public class MacSettingFragment extends BaseFragment implements View.OnClickListener{
	/**
     * 通过startActivityForResult( Intent intent,int code)方式启动Activity的请求吗
     */
    private static final int REQUEST_CODE = 2;
    /**
     * 用来输入检测Sensor-A的MAC地址
     */
    private EditText sensorAedit;
    private EditText sensorBedit;
    /**
     * Sensor-A的MAC地址
     */
    private String sensorAMac;
    private String sensorBMac;
    
    private Button btnKeep,btnScanner,btnShare;
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean state;
    
    private String macA;
    private String macB;
   
   @Override
	public void onResume() {
		super.onResume();
		sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sensorAMac = sharedPreferences.getString("mac_a", null);
        sensorBMac = sharedPreferences.getString("mac_b", null);
        state = sharedPreferences.getBoolean("state", false);
    	if (macA!=null && macB != null) {
    		if (!macA.equals(sensorAedit.getText().toString().trim())
    				|| (!macB.equals(sensorBedit.getText().toString().trim()))) {
    			sensorAedit.setText(macA);
    			sensorBedit.setText(macB);
    		}
		}else {
			if (sensorAMac != null && sensorBMac != null && state) {
	            sensorAedit.setText(sensorAMac);
	            sensorBedit.setText(sensorBMac);
	        }else {
	            sensorAedit.setText("00:00:00:00:00:00:00:00");
	            sensorBedit.setText("00:00:00:00:00:00:00:00");
	        }
		}
	}
    
    @Override
    public View initView() {
    	View view = View.inflate(mContext, R.layout.mac_setting_layout, null);
    	sensorAedit = (EditText) view.findViewById(R.id.sensor_a_edit);
        sensorBedit = (EditText) view.findViewById(R.id.sensor_b_edit);
        btnKeep = (Button) view.findViewById(R.id.btn_keep);
        btnScanner = (Button) view.findViewById(R.id.btn_scanner);
        btnShare = (Button) view.findViewById(R.id.btn_share);
        return view;
    }
    @Override
    public void initData() {
    	// TODO Auto-generated method stub
    	super.initData();
    	btnKeep.setOnClickListener(this);
    	btnScanner.setOnClickListener(this);
    	btnShare.setOnClickListener(this);
    }
    /**
     * 当服务器信息到达时回调的方法
     *
     * @param mac mac地址
     * @param tag 数据的key值
     * @param val 数据的value值
     */
    @Override
    public void onZXBee(String mac, String tag, String val) {
     
    }
	@Override
	public void onClick(View view) {
		String  sensora =sensorAedit.getText().toString().trim();
		String  sensorb =sensorBedit.getText().toString().trim();
		switch (view.getId()) {
		case R.id.btn_keep:
			 if((!sensora.equals("00:00:00:00:00:00:00:00") && sensora != null)
					 && (!sensorb.equals("00:00:00:00:00:00:00:00") && sensorb != null)) {
				 	editor.putString("mac_a",sensora);
				 	editor.putString("mac_b",sensorb);
	                editor.putBoolean("state",true);
	                editor.commit();
	                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
		        }else {
		            Toast.makeText(getActivity(), "MAC地址为初始值", Toast.LENGTH_SHORT).show();
             }
			break;
		case R.id.btn_scanner:
			 if (ContextCompat.checkSelfPermission(getActivity(),
                     Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(getActivity(),
                         new String[] { Manifest.permission.CAMERA }, 1);// 动态申请打开相机的权限
             } else {
                 startActivityForResult(new Intent(getActivity(),
                         MipcaActivityCapture.class), REQUEST_CODE);// 启动二维码扫描页面
             }
			break;
		case R.id.btn_share:
			 if((!sensora.equals("00:00:00:00:00:00:00:00") && sensora != null)
					 && (!sensorb.equals("00:00:00:00:00:00:00:00") && sensorb != null)) {
                 Intent intent = new Intent();
                 intent.putExtra("sensora", sensora);
                 intent.putExtra("sensorb", sensorb);
                 intent.putExtra("sensorc", "00:00:00:00:00:00:00:00");
                 intent.setClass(getActivity(),MacShareActivity.class);
                 startActivity(intent);
		        }else {
		            Toast.makeText(getActivity(), "MAC地址为初始值", Toast.LENGTH_SHORT).show();
             }
			break;
		}
		
	}
	 @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        String scanResult ;
	        if (resultCode == Activity.RESULT_OK) {
	            if (data == null) {
	                Toast.makeText(getActivity(), "没有扫描到二维码！", Toast.LENGTH_SHORT).show();
	            } else {
	                Bundle bundle = data.getExtras();
	                scanResult = bundle.getString("result");
	                try {
	                    Gson gson = new Gson();
	                    MacBean macBean = gson.fromJson(scanResult, MacBean.class);
	                    macA = macBean.getSensora();
	                    macB = macBean.getSensorb();
	                } catch (JsonSyntaxException e) {
	                    e.printStackTrace();
	                    /*兼容原来的格式*/
	                    Toast.makeText(getActivity(), "错误，不支持的配置格式", Toast.LENGTH_SHORT).show();

	                }
	            }

	        }
	    }
}
