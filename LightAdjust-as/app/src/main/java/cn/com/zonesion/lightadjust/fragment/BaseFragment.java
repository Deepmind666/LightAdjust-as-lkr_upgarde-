package cn.com.zonesion.lightadjust.fragment;

import cn.com.zonesion.lightadjust.application.LCApplication;
import cn.com.zonesion.lightadjust.listener.OnWSNDataListener;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements
		OnWSNDataListener {
	/**
	 * 定义上下文
	 */
	protected Context mContext;

	LCApplication mTApplication;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mTApplication = (LCApplication) getActivity().getApplication();
		mTApplication.registerListener(this);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, Bundle savedInstanceState) {
		return initView();
	}

	/**
	 * 由子类实现该方法，创建自己的视图
	 * 
	 * @return
	 */
	public abstract View initView();

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	/**
	 * 子类，需要初始化数据，请求数据并且绑定数据，等重写该方法
	 */
	public void initData() {

	};

	@Override
	public void onMessageArrive(String mac, String dat) {
		if (dat.startsWith("{") && dat.endsWith("}")) {
			String[] its = dat.substring(1, dat.length() - 1).split(",");
			for (String x : its) {
				String[] iv = x.split("=");
				if (iv.length == 2) {
					onZXBee(mac, iv[0], iv[1]);

				}
			}
		}
	}

	public void sendMessageTo(String mac, String dat) {
		mTApplication.sendMessage(mac, dat);
	}

	abstract public void onZXBee(String mac, String tag, String val);
}
