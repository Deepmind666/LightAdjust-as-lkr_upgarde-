package cn.com.zonesion.lightadjust.fragment;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zhiyun360.wsn.droid.WSNHistory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import cn.com.zonesion.lightadjust.R;

public class HDFragment extends	BaseFragment{
    /**
     * 用来选择想要查看的历史记录的时间段
     */
    private Spinner airTemperatureSearch;
    /**
     * Spinner对象被选中的位置，即下标
     */
    private int position;
    
    private String id;
    private String key;
    private String sv;
    View view;
    
    private SimpleDateFormat simpleDateFormat;
	private SimpleDateFormat outputDateFormat;
	private SimpleDateFormat rtTime;
	
	Date curDate = new Date(System.currentTimeMillis());
	
	private String mMac;
	
	private Button btn;
	
	 private SharedPreferences preferences;
	 private Handler handler;
	 private boolean state;
  
    @Override
    public View initView() {
    	view = View.inflate(mContext, R.layout.hd_layout, null);
    	airTemperatureSearch = (Spinner) view.findViewById(R.id.air_temperature_search);
    	btn = (Button) view.findViewById(R.id.btn);
     	return view;
    }
    
    @Override
    public void initData() {
    	// TODO Auto-generated method stub
    	super.initData();
    	id = mTApplication.getCurrentId();
    	key = mTApplication.getCurrentKey();
    	sv = mTApplication.mSrvAddr;
    	initViewAndBindEvent();
    	handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                	preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    mMac = preferences.getString("mac_a", null);
                    state = preferences.getBoolean("state", false);
                    if (mMac == null && !state) {
//                        Toast.makeText(mContext, "请绑定设备", Toast.LENGTH_SHORT).show();
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
        }, 0,1000);
    	simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
		outputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		rtTime = new SimpleDateFormat("HH:mm:ss");
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int ti = airTemperatureSearch.getSelectedItemPosition();
				if (ti != 0) {
					String mac = mMac;
					if (mac != null) {
						String ch = mac+"_"+"A2";
						System.out.println(""+(ti-1));
						new historyDataAsyn(ch, (ti-1)).execute(new String[0]);
					}else {
						Toast.makeText(mContext, "设备正在紧张的获取中，请稍后..", Toast.LENGTH_SHORT).show();
					}
				}else {
						Toast.makeText(mContext, "请选择馍查询的时间段", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
    }
    /**
     * 初始化视图和绑定控件的响应事件
     */
    private void initViewAndBindEvent() {
        //声明一个ArrayAdapter对象，用来对Spinner进行适配
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.airTemperatureSearch));
        //通过setAdapter将适配器设置给Spinner
        airTemperatureSearch.setAdapter(adapter);
        //绑定Spinner的响应事件
        airTemperatureSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //当Spinner的某一条数据被选中的时候回调
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                
                reflectChangeSpinnerPosition(AdapterView.INVALID_POSITION);
            }

            //当Spinner的数据没有被选中的时候调用
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            	
            }
        });


    }

    /**
     * 通过java反射来让Spinner选择同一个选项时会触发onItemSelected事件
     */
    private void reflectChangeSpinnerPosition(int position){
        try {
            Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
            field.setAccessible(true);  //设置mOldSelectedPosition可访问
            field.setInt(airTemperatureSearch,position); //设置mOldSelectedPosition的值
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        reflectChangeSpinnerPosition(position);
    }
    
    class historyDataAsyn extends AsyncTask<String, String, String>{
    	
    	String ch;
		int duration;
		
		public historyDataAsyn(String ch, int duration) {
			this.ch = ch;
			this.duration = duration;
		}
		
		private ProgressDialog myDialog;
		@Override
		protected String doInBackground(String... arg0) {
			String result = "";
			
			if (id.length()==0 || key.length()==0) return null;
			WSNHistory history = new WSNHistory(id,key);
			history.setServerAddr(sv+":8080");
			try {
				switch (duration) {
				case 0:
					result = history.queryLast1H(ch);
					break;
				case 1:
					result = history.queryLast6H(ch);
					break;
				case 2:
					result = history.queryLast12H(ch);
					break;
				case 3:
					result = history.queryLast1D(ch);
					break;
				case 4:
					result = history.queryLast5D(ch);
				case 5:
					result = history.queryLast14D(ch);
					break;
				case 6:
					result = history.queryLast1M(ch);
					break;
				case 7:
					result = history.queryLast3M(ch);
					break;
				case 8:
					result = history.queryLast6H(ch);
					break;
				default:
					result = history.queryLast1Y(ch);
					break;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			myDialog.cancel();
			if (result == null) {
				Toast.makeText(mContext, "查询历史数据失败!",
	    			     Toast.LENGTH_SHORT).show();
				result = "";
			}
			try {
				List<HashMap<String, Object>> li = getList(result);
				if(li.size() <= 0){
					showNullLine(curDate, 1);
				}else {
					showLine(li);
				}
			} catch (Exception e) {
				showNullLine(curDate, 1);
				Toast.makeText(mContext, "没有找到指定的数据!",
	    			     Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			myDialog = new ProgressDialog(mContext);
			myDialog.setIcon(null);
			myDialog.setTitle("");
			myDialog.setMessage("历史数据获取中，请稍候......");
			myDialog.show();
		}
    }
    
    /**
	 * 解析json字符串，获得List
	 * @param resultJson
	 * @return
	 */
	public List<HashMap<String, Object>> getList(String resultJson){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try {
			JSONObject jsonObjs = new JSONObject(resultJson);
			JSONArray datapoints = jsonObjs.getJSONArray("datapoints");
			for (int i = 0; i < datapoints.length(); i++) {
				JSONObject jsonObj = datapoints.getJSONObject(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Value", jsonObj.getString("value"));
				map.put("At", jsonObj.getString("at"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return list;
	}
    
	/**
	 *  显示空曲线
	 * @param date
	 * @param daySumNum
	 */
	public void showNullLine(Date date, long daySumNum) {
		List<Date[]> x = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		XYMultipleSeriesRenderer renderer = setRenderer();
		setChartSettings(renderer, "", "时间", "数据", (date.getTime() - daySumNum * 24 * 60 * 60 * 1000), date.getTime(), -5, 30, Color.LTGRAY, Color.LTGRAY);
		mGrapView = ChartFactory.getTimeChartView(mContext, buildDateDataset(x, values), renderer, "M/d-H:mm");
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.curveLayout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.weight = 1;
		layout.removeAllViews();
		layout.addView(mGrapView, lp);
	}
	
	private GraphicalView mGrapView;
	List<HashMap<String, Object>> mLastData;
	
	/**
	 * 显示历史数据曲线
	 * @param list
	 */
	public void showLine(List<HashMap<String, Object>> list) {
		mLastData = list;
		String[] at = new String[list.size()];
		double[] value = new double[list.size()];
		Date[] date = new Date[list.size()];
		for(int i = 0; i < list.size(); i++) {
			HashMap<String, Object> map = list.get(i);
			at[i] = (String) map.get("At");
			try {
				date[i] = outputDateFormat.parse(at[i]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			value[i] = Double.parseDouble((String) map.get("Value"));
		}
		List<Date[]> x = new ArrayList<Date[]>();
		x.add(date);
		List<double[]> values = new ArrayList<double[]>();
		values.add(value);
		double minValue = value[0];
		double maxValue = value[0];
		for (int i = 0; i < value.length; i++) {
			if (value[i] < minValue) {
				minValue = value[i];
			}
			if (value[i] > maxValue) {
				maxValue = value[i];
			}
		}
		double scale = (maxValue - minValue) / 6;
		XYMultipleSeriesRenderer renderer = setRenderer();
		setChartSettings(renderer, "", "时间", "数据", x.get(0)[0].getTime(), x.get(0)[date.length - 1].getTime(), (minValue - scale), (maxValue + scale), Color.LTGRAY, Color.LTGRAY);
		mGrapView = ChartFactory.getTimeChartView(mContext, buildDateDataset(x, values), renderer, "M/d-H:mm");
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.curveLayout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.weight = 1;
		layout.removeAllViews();
		layout.addView(mGrapView, lp);
	}
	
	/**
	 * 曲线相关设置
	 * @return
	 */
	private XYMultipleSeriesRenderer setRenderer() {
		// TODO Auto-generated method stub
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });

		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.rgb(30, 144, 255));
		// r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(false);
		r.setLineWidth(1);
		r.setDisplayChartValues(true);
		renderer.addSeriesRenderer(r);

		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setXLabels(10);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setZoomButtonsVisible(true);

		return renderer;
	}
	
	private XYMultipleSeriesDataset buildDateDataset(List<Date[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		TimeSeries series = new TimeSeries("历史数据");
		if (xValues.size() <= 0) {
			series.add((curDate.getTime() - 5 * 24 * 60 * 60 * 1000), 0);
		} else {
			Date[] xV = xValues.get(0);
			double[] yV = yValues.get(0);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
		}
		dataset.addSeries(series);
		return dataset;
	}
	
	private void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
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
}
