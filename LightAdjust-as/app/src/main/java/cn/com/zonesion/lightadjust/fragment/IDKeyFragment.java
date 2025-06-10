package cn.com.zonesion.lightadjust.fragment;

import com.example.qr_codescan.MipcaActivityCapture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.activity.IdKeyShareActivity;
import cn.com.zonesion.lightadjust.bean.IdKeyBean;

/**
 * IDKeyFragment是当你点击更多导航栏，选中IDKey选项时所展示的页面
 * 该页面由多个TextView、EditText和Button组成，可以通过ID、KEY和SERVER（服务器地址）将设备与服务器进行连接
 */

public class IDKeyFragment extends BaseFragment {
	/**
     * 通过startActivityForResult( Intent intent,int code)方式启动Activity的请求吗
     */
    private static final int REQUEST_CODE = 1;
    /**
     * 用来输入设备的ID
     */
    private EditText idEdit;
    /**
     * 用来输入设备的KEY
     */
    private EditText keyEdit;
    /**
     * 用来输入服务器地址
     */
    private EditText serverEdit;
    /**
     * 点击该按钮连接设备与服务器
     */
    private Button btnConnection;
    /**
     * 点击该按钮进行扫描二维码
     */
    private Button btnScannerKey;
    /**
     * 点击该按钮生成包含用户的ID、KEY和服务器地址的二维码
     */
    private Button btnShareKey;
    
    String mid;
    String mkey;
    String mserver;
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (mid!=null && mkey != null && mserver != null) {
    		if ((!mid.equals(idEdit.getText().toString().trim()))
    				|| (!mkey.equals(keyEdit.getText().toString().trim())
    				|| (!mid.equals(serverEdit.getText().toString().trim())))
    				) {
        		idEdit.setText(mid);
        		keyEdit.setText(mkey);
        		serverEdit.setText(mserver);
    		}
		}else {
			idEdit.setText(mTApplication.getCurrentId());
			keyEdit.setText(mTApplication.getCurrentKey());
			serverEdit.setText(mTApplication.mSrvAddr);
		}
    }
    
    @Override
    public View initView() {
    	View view = View.inflate(mContext, R.layout.idkey_layout, null);
    	idEdit = (EditText) view.findViewById(R.id.id_edit);
        keyEdit = (EditText) view.findViewById(R.id.key_edit);
        serverEdit = (EditText) view.findViewById(R.id.server_edit);
        btnConnection = (Button) view.findViewById(R.id.btn_connection);
        btnScannerKey = (Button) view.findViewById(R.id.btn_scanner_key);
        btnShareKey = (Button) view.findViewById(R.id.btn_share_key);
        return view;
    }
    
    @Override
    public void initData() {
    	super.initData();
		idEdit.setText(mTApplication.getCurrentId());
		keyEdit.setText(mTApplication.getCurrentKey());
		serverEdit.setText(mTApplication.mSrvAddr);
    	btnConnection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String myZCloudID = idEdit.getText().toString();
				String myZCloudKey = keyEdit.getText().toString();
				String addr = serverEdit.getText().toString();
				if (myZCloudID.equals(mTApplication.getCurrentId())
						&& myZCloudKey.equals(mTApplication
						.getCurrentKey())
						&& mTApplication.mSrvAddr.equals(addr)) {
					return;
				}
				mTApplication.setCurrentIdKey(myZCloudID, myZCloudKey,
						addr);
				mTApplication.setServerAddr(addr);
				mTApplication.reconnect();
			}
		});
        btnScannerKey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);//动态申请打开相机的权限
				}else{
                  startActivityForResult(new Intent(getActivity(), MipcaActivityCapture.class), REQUEST_CODE);//启动二维码扫描页面
				}
			}
		});
        btnShareKey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String id = idEdit.getText().toString().trim();
				  String key = keyEdit.getText().toString().trim();
				  String server = serverEdit.getText().toString().trim();
				  if (id.length() == 0 || key.length() == 0 || server.length() == 0) {
					  Toast.makeText(getContext(),
								"ID和KEY不能为空！", Toast.LENGTH_SHORT).show();
					  return;
				}
              Intent intent = new Intent();
              intent.putExtra("id", id);
              intent.putExtra("key", key);
              intent.putExtra("server", server);
              intent.setClass(getActivity(), IdKeyShareActivity.class);
              startActivity(intent);
			}
		});
    }
    
    /**
     * 将扫描到的二维码的结果回传给该Fragment时调用
     *
     * @param requestCode 通过startActivityForResult(Intent intent,int requestCode)启动Activity时的请求码
     * @param resultCode  被启动的Activity回传数据给该Fragment时的结果码
     * @param data        CaptureActivity回传给该Fragment的携带结果数据的Intent对象
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String scanResult;
        if (resultCode == Activity.RESULT_OK ) {
            if (data == null) {
                Toast.makeText(getActivity(), "没有扫描到二维码！", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = data.getExtras();
                scanResult = bundle.getString("result");
                try {
                    Gson gson = new Gson();
                    IdKeyBean idKeyBean = gson.fromJson(scanResult, IdKeyBean.class);
                    mid  = idKeyBean.getId();
                    mkey =  idKeyBean.getKey();
                    mserver =  idKeyBean.getServer();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    /*兼容原来的格式*/
                    String id = null,key = null;
                    String[] its = scanResult.split(",");
                    if (its.length == 2) {
                    	for (String x : its) {
							String[] i = x.split(":");
							if (i.length == 2) {
								if (i[0].equals("ID")) {
									id = i[1];
								}
								if (i[0].equals("KEY")) {
									key = i[1];
								}
							}
						}
					}
                    if (id != null && key != null) {
                    	mid  = id;
                   	  	mkey =  key;
                   	  	mserver =  "api.zhiyun360.com";
					}else {
						Toast.makeText(getActivity(), "错误，不支持的配置格式", Toast.LENGTH_SHORT).show();
					}
                }
            }
        }
    }

	@Override
	public void onZXBee(String mac, String tag, String val) {
		// TODO Auto-generated method stub
		
	}
}
