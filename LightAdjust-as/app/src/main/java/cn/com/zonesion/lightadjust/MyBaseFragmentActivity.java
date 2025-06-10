package cn.com.zonesion.lightadjust;

import java.util.List;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;

public class MyBaseFragmentActivity extends FragmentActivity{
	 private String TAG = "MainActivity";
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	// TODO Auto-generated method stub
	    	super.onActivityResult(requestCode, resultCode, data);
	    	FragmentManager fragmentManager = getSupportFragmentManager();
	    	for (int i = 0; i < fragmentManager.getFragments().size(); i++)
	    	{
	    		Fragment fragment = fragmentManager.getFragments().get(i);
	    		if (fragment == null) 
	    		{
	    			Log.w(TAG, "Activity result no fragment exists for index: 0x"  
	                        + Integer.toHexString(requestCode));  
				}else 
				{
					handleResult(fragment, requestCode, resultCode, data);
				}
			}
	    }
	    /**
	     * 递归调用，对所有的子Fragment生效
	     * @param fragment
	     * @param requestCode
	     * @param resultCode
	     * @param data
	     */
	    private void handleResult(Fragment fragment,int requestCode,int resultCode,Intent data)
	    {
	    	fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult
	    	Log.e(TAG, "MainActivity");
	 
	    	 List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment
	    	 if (childFragment != null) 
	    	 {
				for (Fragment f:childFragment) 
				{
					if (f != null) 
					{
						handleResult(f, requestCode, resultCode, data);
					}
					if (childFragment == null) 
					{
						Log.e(TAG, "MainActivity1111");
					}
				}
			}
	    	
	    } 
}
