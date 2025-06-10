package cn.com.zonesion.lightadjust.fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.update.UpdateService;
import cn.com.zonesion.lightadjust.view.APKVersionCodeUtils;
import cn.com.zonesion.lightadjust.view.PagerSlidingTabStrip;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MoreInformationFragment extends Fragment{
	private ViewPager pager;
    /**
     * IDKeyFragment用来设置和显示ID、KEY和服务器地址的页面
     */
    private IDKeyFragment idKeyFragment;
    /**
     * MacSettingFragment用来设置和显示被检测项的MAC地址的页面
     */
    private MacSettingFragment macSettingFragment;
    /**
     * VersionInformationFragment用来显示版本等相关信息的页面
     */
    private VersionInformationFragment versionInformationFragment;
    /** 
     * PagerSlidingTabStrip的实例 
     */  
    private PagerSlidingTabStrip tabs;  
  
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm;  
    
    private void initFourChildFragment(){
    	dm = getResources().getDisplayMetrics();
		pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		tabs.setViewPager(pager);
		 // 设置Tab是自动填充满屏幕的  
        tabs.setShouldExpand(true);  
        // 设置Tab的分割线是透明的  
        tabs.setDividerColor(Color.TRANSPARENT);  
        // 设置Tab底部线的高度  
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));  
        // 设置Tab Indicator的高度  
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));  
        // 设置Tab标题文字的大小  
        tabs.setTextSize((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_SP, 16, dm));  
        // 设置Tab Indicator的颜色  
        tabs.setIndicatorColor(Color.parseColor("#67AE37"));  
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)  
        tabs.setSelectedTextColor(Color.parseColor("#67AE37"));  
        // 取消点击Tab时的背景色  
        tabs.setTabBackground(0); 
    }
    
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
    		@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.more_information_layout, container, false);
    	pager = (ViewPager) view.findViewById(R.id.viewpager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        return view;
    }

    @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initFourChildFragment();
	}
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
  
    }
    public class MyPagerAdapter extends FragmentPagerAdapter {  
  	  
        public MyPagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
  
        private final String[] titles = { "IDKey", "MAC设置", "版本信息" };  
  
        @Override  
        public CharSequence getPageTitle(int position) {  
            return titles[position];  
        }  
  
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
  
        @Override  
        public Fragment getItem(int position) {  
            switch (position) {  
            case 0:  
                if (idKeyFragment == null) {  
                    idKeyFragment = new IDKeyFragment();  
                }  
                return idKeyFragment;  
            case 1:  
                if (macSettingFragment == null) {  
                    macSettingFragment = new MacSettingFragment();  
                }  
                return macSettingFragment;  
            case 2:  
                if (versionInformationFragment == null) {  
                    versionInformationFragment = new VersionInformationFragment();  
                }  
                return versionInformationFragment;  
            default:  
                return null;  
            }  
        }  
    }  
}
