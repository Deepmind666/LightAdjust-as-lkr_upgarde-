package cn.com.zonesion.lightadjust.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.zhiyun360.wsn.droid.WSNRTConnect;
import com.zhiyun360.wsn.droid.WSNRTConnectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.zonesion.lightadjust.listener.OnWSNDataListener;

/**
 * LCApplication继承Application类，在整个APP里对象唯一。在该类中，使用单例模式创建WSNRTConnect对象，使
 * WSNRTConnect对象在整个APP里对象唯一
 */

public class LCApplication extends Application implements WSNRTConnectListener {
	private static final String USER_CONFIG = "_user_config";

	public static final String NI_TYPE = "_type";
	public static final String NI_LASTDATA = "ldat";
	public static final String NI_LRECVTIME = "lrtime";

	private SharedPreferences mSharePreferences;
	private final WSNRTConnect mWSNRTConnect = new WSNRTConnect();
	private final ConcurrentHashMap<String, HashMap<String, Object>> cache = new ConcurrentHashMap<>();
	private final List<OnWSNDataListener> mLiList = new ArrayList<>();

	public String mSrvAddr;
	private int mSelectIdx = -1;
	private List<HashMap<String, Object>> mIdKeyList = new ArrayList<>();
	private JSONArray mIdKeyJSArray;
	private final MyHandler mHandler = new MyHandler(this);
	private int connect_status = 0; // 0: disconnected, 1: connecting, 2: connected, 3: disconnecting

	@Override
	public void onCreate() {
		super.onCreate();
		mSharePreferences = getSharedPreferences(USER_CONFIG, MODE_PRIVATE);
		loadIdKeys();
		mWSNRTConnect.setRTConnectListener(this);
		connect();
	}

	private void loadIdKeys() {
		String iks = mSharePreferences.getString("idkeys", "[]");
		try {
			mIdKeyJSArray = new JSONArray(iks);
			mIdKeyList = new ArrayList<>();
			for (int i = 0; i < mIdKeyJSArray.length(); i++) {
				JSONObject o = mIdKeyJSArray.getJSONObject(i);
				HashMap<String, Object> m = new HashMap<>();
				m.put("id", o.getString("id"));
				m.put("key", o.getString("key"));
				m.put("info", o.getString("info"));
				boolean sle = o.getBoolean("sle");
				m.put("sle", sle);
				m.put("server", o.optString("server", "api.zhiyun360.com"));
				mIdKeyList.add(m);
				if (sle) {
					mSelectIdx = i;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mIdKeyJSArray = new JSONArray();
			mIdKeyList = new ArrayList<>();
			mSelectIdx = -1;
		}
	}

	private void saveIdKeys() {
		if (mIdKeyJSArray != null) {
			mSharePreferences.edit().putString("idkeys", mIdKeyJSArray.toString()).apply();
		}
	}
	
	public void setCurrentIdKey(String id, String key, String sv) {
		if (id == null || id.isEmpty()) return;

		for (int i = 0; i < mIdKeyList.size(); i++) {
			if (id.equals(mIdKeyList.get(i).get("id"))) {
				HashMap<String, Object> item = mIdKeyList.get(i);
				item.put("key", key);
				item.put("server", sv);
				try {
					JSONObject jo = mIdKeyJSArray.getJSONObject(i);
					jo.put("key", key);
					jo.put("server", sv);
					saveIdKeys();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				selectIDKeyIdx(i);
				return;
			}
		}
		addIdKey(id, key, sv, "");
	}

	public void addIdKey(String id, String key, String sv, String info) {
		if (id == null || id.isEmpty()) return;
		HashMap<String, Object> m = new HashMap<>();
		m.put("id", id);
		m.put("key", key);
		m.put("server", sv);
		m.put("info", info);
		m.put("sle", false); // New entries are not selected by default
		mIdKeyList.add(m);
		try {
			JSONObject jo = new JSONObject();
			jo.put("id", id);
			jo.put("key", key);
			jo.put("server", sv);
			jo.put("info", info);
			jo.put("sle", false);
			mIdKeyJSArray.put(jo);
			saveIdKeys();
			selectIDKeyIdx(mIdKeyList.size() - 1); // Select the newly added key
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public void delIdKey(int idx) {
		if (idx < 0 || idx >= mIdKeyList.size()) return;

		mIdKeyList.remove(idx);
		mIdKeyJSArray.remove(idx);
		saveIdKeys();

		if (mSelectIdx == idx) {
			mSelectIdx = -1; // Deselect
			reconnect();
		} else if (mSelectIdx > idx) {
			mSelectIdx--;
		}
	}

	public void selectIDKeyIdx(int idx) {
		if (idx == mSelectIdx) return;

		// Unselect previous
		if (mSelectIdx >= 0 && mSelectIdx < mIdKeyJSArray.length()) {
			try {
				mIdKeyJSArray.getJSONObject(mSelectIdx).put("sle", false);
				mIdKeyList.get(mSelectIdx).put("sle", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		mSelectIdx = idx;

		// Select new
		if (mSelectIdx >= 0 && mSelectIdx < mIdKeyJSArray.length()) {
			try {
				mIdKeyJSArray.getJSONObject(mSelectIdx).put("sle", true);
				mIdKeyList.get(mSelectIdx).put("sle", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		saveIdKeys();
		reconnect();
	}

	public void connect() {
		if (connect_status != 0) return;

		String id = getCurrentId();
		String key = getCurrentKey();
		if (id.isEmpty() || key.isEmpty()) {
			 return;
		}

		connect_status = 1;
		setServerAddr(getCurrentServer());
		mWSNRTConnect.setIdKey(id, key);
		mWSNRTConnect.connect();
	}

	public void disconnect() {
		if (connect_status == 0) return;
		connect_status = 3;
		mHandler.removeMessages(1);
		mHandler.removeMessages(2);
		mWSNRTConnect.disconnect();
		connect_status = 0;
		cache.clear();
	}

	public void reconnect() {
		disconnect();
		mHandler.sendEmptyMessageDelayed(1, 500);
	}

	@Override
	public void onConnect() {
		connect_status = 2;
		Toast.makeText(getApplicationContext(), "已连接到 " + getCurrentId(), Toast.LENGTH_SHORT).show();
		mHandler.sendEmptyMessage(2);
	}

	@Override
	public void onConnectLost(Throwable arg0) {
		connect_status = 0;
		Toast.makeText(getApplicationContext(), "连接已断开, 3秒后尝试重连...", Toast.LENGTH_SHORT).show();
		mHandler.sendEmptyMessageDelayed(1, 3000);
	}
	
	@Override
	public void onMessageArrive(String mac, byte[] data) {
		String dat = new String(data);
		HashMap<String, Object> info = cache.get(mac);
		if (info == null) {
			info = new HashMap<>();
			info.put(NI_TYPE, "");
			info.put("rtype", System.currentTimeMillis() / 1000 - 7);
			cache.put(mac, info);
		}

		info.put(NI_LASTDATA, dat);
		info.put(NI_LRECVTIME, System.currentTimeMillis() / 1000);

		if (dat.startsWith("{") && dat.endsWith("}")) {
			try {
				JSONObject jo = new JSONObject(dat);
				if (jo.has("TYPE")) {
					info.put(NI_TYPE, jo.getString("TYPE"));
				}
			} catch (JSONException e) {
				// Ignore
			}
		}
		
		for (OnWSNDataListener li : new ArrayList<>(mLiList)) {
			li.onMessageArrive(mac, dat);
		}
	}

	// Getters and other public methods
	public int getConnectStatus() { return connect_status; }
	public String getCurrentId() {
		if (mSelectIdx >= 0 && mSelectIdx < mIdKeyList.size()) {
			return (String) mIdKeyList.get(mSelectIdx).getOrDefault("id", "");
		}
		return "";
	}
	public String getCurrentKey() {
		if (mSelectIdx >= 0 && mSelectIdx < mIdKeyList.size()) {
			return (String) mIdKeyList.get(mSelectIdx).getOrDefault("key", "");
		}
		return "";
	}
	public String getCurrentServer() {
		if (mSelectIdx >= 0 && mSelectIdx < mIdKeyList.size()) {
			return (String) mIdKeyList.get(mSelectIdx).getOrDefault("server", "api.zhiyun360.com");
		}
		return "api.zhiyun360.com";
	}
	public void setServerAddr(String addr) {
		mSrvAddr = addr;
		if (mSharePreferences != null) {
			mSharePreferences.edit().putString("_addr", mSrvAddr).apply();
		}
		mWSNRTConnect.setServerAddr(mSrvAddr);
	}
	public void registerListener(OnWSNDataListener li) { if (!mLiList.contains(li)) mLiList.add(li); }
	public void unregisterListener(OnWSNDataListener li) { mLiList.remove(li); }
	public void sendMessage(String mac, String dat) { if (connect_status == 2) mWSNRTConnect.sendMessage(mac, dat.getBytes()); }
	public Set<String> getNodes() { return cache.keySet(); }
	public HashMap<String, Object> getNodeInfo(String mac) { return cache.get(mac); }
	public List<HashMap<String, Object>> getIdKeyList() { return mIdKeyList; }
	 public int getSelectIdx() {
		return mSelectIdx;
	}


	private static class MyHandler extends Handler {
		private final WeakReference<LCApplication> appRef;

		MyHandler(LCApplication app) {
			super(Looper.getMainLooper());
			this.appRef = new WeakReference<>(app);
		}

		@Override
		public void handleMessage(Message msg) {
			LCApplication app = appRef.get();
			if (app == null) return;

			switch (msg.what) {
				case 1: // Connect
					app.connect();
					break;
				case 2: // Periodic check
					if (app.connect_status == 2) {
						for (String mac : new ArrayList<>(app.cache.keySet())) {
							HashMap<String, Object> m = app.cache.get(mac);
							if (m != null) {
								Object rtypeObj = m.get("rtype");
								if (rtypeObj instanceof Long) {
									long rt = (Long) rtypeObj;
									long ct = System.currentTimeMillis() / 1000;
									Object typeObj = m.get(NI_TYPE);
									if (typeObj instanceof String && ((String) typeObj).isEmpty() && ct - rt > 10) {
										app.sendMessage(mac, "{TYPE=?}");
										m.put("rtype", ct);
									}
								}
							}
						}
						sendEmptyMessageDelayed(2, 1000);
					}
					break;
			}
		}
	}
}
