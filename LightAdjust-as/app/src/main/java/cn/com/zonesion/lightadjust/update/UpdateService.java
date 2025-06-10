package cn.com.zonesion.lightadjust.update;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class UpdateService extends Service{
		/**
	     * 安卓系统下载类
	     */
	    DownloadManager manager;
	    /**
	     *接受下载完的广播
	     */
	    DownloadCompleteReceiver receiver;
	    /**
	     * 初始化下载器
	     * @param url
	     */
	    @SuppressLint("NweApi")
	    private void initDownManager(String url){
	        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	        receiver = new DownloadCompleteReceiver();
	        Uri uri = Uri.parse(url);
	        //设置下载地址
	        DownloadManager.Request down = new DownloadManager.Request(uri);
	
	        //设置允许使用的网络类型，这里是移动网络和WiFi都可以
	        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
	        //下载时，通知栏显示途中
	        down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
	
	        // 显示下载界面
	        down.setVisibleInDownloadsUi(true);
	
	        // 设置下载后文件存放的位置
	        down.setDestinationInExternalFilesDir(this,
	                Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
	
	        // 将下载请求放入队列
	        manager.enqueue(down);
	
	        // 注册下载广播
	        registerReceiver(receiver, new IntentFilter(
	                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	    }
	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        String url = intent.getStringExtra("url");
	        //调用下载
	        initDownManager(url);
	
	        return super.onStartCommand(intent, flags, startId);
	    }
	
	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
	        return null;
	    }
	    @Override
	    public void onDestroy() {
	        if(receiver != null) {
	            unregisterReceiver(receiver);
	        }
	        super.onDestroy();
	    }
	    //接受下载完成后的intent
	    class DownloadCompleteReceiver extends BroadcastReceiver {
	
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            //判断是否下载完成的广播
	            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
	                //获取下载的文件id
	                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
	
	                //自动安装apk
	                installAPK(manager.getUriForDownloadedFile(downId));
	
	                //停止服务并关闭广播
	                UpdateService.this.stopSelf();
	            }
	        }
	
	        /**
	         * 安装apk文件
	         * @param apk
	         */
	        private void installAPK(Uri apk){
	            //通过intent安装Apk
	            Intent intent = new Intent();
	
	            intent.setAction("android.intent.action.VIEW");
	            intent.addCategory("android.intent.category.DEFAULT");
	            intent.setType("application/vnd.android.package-archive");
	            intent.setData(apk);
	            intent.setDataAndType(apk,
	                    "application/vnd.android.package-archive");
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//            android.os.Process.killProcess(android.os.Process.myPid());
	            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
	            startActivity(intent);
	        }
	    }
}
