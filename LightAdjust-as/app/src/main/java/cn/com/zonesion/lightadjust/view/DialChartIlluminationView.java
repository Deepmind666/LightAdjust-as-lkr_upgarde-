package cn.com.zonesion.lightadjust.view;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.chart.DialChart;
import org.xclcharts.common.MathHelper;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotAttrInfo;
import org.xclcharts.view.GraphicalView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;

public class DialChartIlluminationView extends GraphicalView{
	private String TAG = "DialChartIlluminationView";
	private DialChart chart = new DialChart();
	private float mPercentage = 0f;
	
	public DialChartIlluminationView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public DialChartIlluminationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public DialChartIlluminationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	private void initView() {
		chartRender();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		chart.setChartRange(w, h);
	}
	public void chartRender() {
		try {

			// 设置标题背景
			chart.setApplyBackgroundColor(true);
			// 设置当前百分比
			Log.e(TAG, String.valueOf(mPercentage));
			chart.getPointer().setPercentage(mPercentage/2000f);

			// 设置指针长度
			chart.getPointer().setLength(0.7f, 0.03f);

			// 增加轴
			addAxis();
			/////////////////////////////////////////////////////////////
			// 增加指针
			addPointer();
			// 设置附加信息
			addAttrInfo();
			/////////////////////////////////////////////////////////////
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
	}
	
	public void addAxis() {

		List<String> rlabels = new ArrayList<String>();
		int j = 0;
		for (int i = 0; i <= 2000;) {
			if (0 == i || j == 9) {
				rlabels.add(Integer.toString(i));
				j = 0;
			} else {
				rlabels.add("");
				j++;
			}

			i += 50;
		}
		chart.addOuterTicksAxis(0.8f, rlabels);

		// 环形颜色轴
		List<Float> ringPercentage = new ArrayList<Float>();
		ringPercentage.add(0.33f);
		ringPercentage.add(0.33f);
		ringPercentage.add(1 - 2 * 0.33f);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(133, 206, 130));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(229, 63, 56));
		chart.addStrokeRingAxis(0.8f, 0.7f, ringPercentage, rcolor);

	

		chart.getPlotAxis().get(0).setDetailModeSteps(4);
		chart.getPlotAxis().get(0).getTickLabelPaint().setColor(Color.BLACK);
		chart.getPlotAxis().get(0).getTickMarksPaint().setColor(Color.BLUE);
		chart.getPlotAxis().get(0).hideAxisLine();
		chart.getPlotAxis().get(0).showAxisLabels();

		chart.getPointer().setPointerStyle(XEnum.PointerStyle.TRIANGLE);
		chart.getPointer().setBaseRadius(15);
		chart.getPointer().getPointerPaint().setStrokeWidth(7);
		chart.getPointer().getBaseCirclePaint().setColor(Color.GRAY);

	}
	private void addAttrInfo() {
		/////////////////////////////////////////////////////////////
		PlotAttrInfo plotAttrInfo = chart.getPlotAttrInfo();
		// 设置附加信息

		Paint paintBT = new Paint();
		paintBT.setColor(Color.RED);
		paintBT.setTextAlign(Align.CENTER);
		paintBT.setTextSize(15);
		paintBT.setFakeBoldText(true);
		paintBT.setAntiAlias(true);
		plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM,
				"光照强度：" + Float.toString(MathHelper.getInstance().round(mPercentage, 2)) + "xl", 0.85f, paintBT);
	}
	
	public void addPointer() {

	}
	
	public void setCurrentStatus(float percentage) {
		// 清理
		chart.clearAll();

		mPercentage = percentage;
		// 设置当前百分比
		
		chart.getPointer().setPercentage(mPercentage/2000f);
		addAxis();
		addPointer();
		addAttrInfo();
	}
	
	@Override
	public void render(Canvas canvas) {
		// TODO Auto-generated method stub
		try {
			chart.render(canvas);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
