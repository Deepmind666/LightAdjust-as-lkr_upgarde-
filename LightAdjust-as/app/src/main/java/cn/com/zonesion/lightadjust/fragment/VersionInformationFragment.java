package cn.com.zonesion.lightadjust.fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.update.UpdateService;
import cn.com.zonesion.lightadjust.view.APKVersionCodeUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * VersionInformationFragment用来显示版本等相关信息的页面，在该页面可以查看当前版本、进行版本升级和清除升级日志
 */

public class VersionInformationFragment extends Fragment{
	 /**
     * 点击该按钮可以进行版本升级
     */
    private Button btnVersionUp;
    /**
     * 点击该按钮查看升级日志
     */
    private Button btnVersionLogcat;
    /**
     * 该按钮用来清除本地日志
     */
    private Button deleteVersionLogcat;
    /**
     * 用来提示当前APP的版本
     */
    private TextView textCurrentVersion;
    /**
     * 用来展示二维码
     */
    private ImageView qrDownloadApp;
    private String verName;
    private String v;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //获取屏幕的宽高，单位是像素
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        ////通过inflater()方法将布局文件转换为一个View对象
        View view = inflater.inflate(R.layout.version_information, container, false);
        btnVersionUp = (Button) view.findViewById(R.id.btn_version_up);
        btnVersionLogcat = (Button) view.findViewById(R.id.btn_version_logcat);
        deleteVersionLogcat = (Button) view.findViewById(R.id.delete_version_logcat);
        textCurrentVersion = (TextView) view.findViewById(R.id.text_current_version);
        qrDownloadApp = (ImageView) view.findViewById(R.id.qr_download_app);
        verName = APKVersionCodeUtils.getVerName(getActivity());
        textCurrentVersion.setText("当前版本"+verName);
        //传入字符串，动态生成相应的二维码图片
        String url = "http://demo.zhiyun360.com/appstore/XLab/lightAdjust/lightAdjust.apk";
        Bitmap qrBitmap = generateBitmap(url,screenWidth/5 ,screenHeight/3 );
        //给二维码图片中间添加相应的logo
        Bitmap finalBitmap = addLogo(qrBitmap, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //将二维码显示在ImageView控件上
        qrDownloadApp.setImageBitmap(finalBitmap);
        return view;
    }
    /**
     * 该方法用来生成相应的二维码图片，并以Bitmap的形式返回
     * @param content 指定的要生成的二维码的内容的字符串形式
     * @param width 生成的二维码的宽度，单位为像素
     * @param height 生成的二维码的高度，单位为像素
     * @return 生成的二维码的Bitmap对象
     */
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 给二维码图片添加中心logo
     * @param qrBitmap 生成的二维码图片的Bitmap对象
     * @param logoBitmap 要添加的LOGO的图片的Bitmap对象
     * @return 一个添加了LOGO的二维码图片的Bitmap对象
     */
    private Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
        int qrBitmapWidth = qrBitmap.getWidth();
        int qrBitmapHeight = qrBitmap.getHeight();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();
        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        canvas.save();
        float scaleSize = 1.0f;
        while ((logoBitmapWidth / scaleSize) > (qrBitmapWidth / 5) || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
            scaleSize *= 2;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);
        canvas.restore();
        return blankBitmap;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //版本升级
        btnVersionUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
              if(verName.equals(v)) {
            	  Toast.makeText(getActivity(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
              }else {
            	  new updateApkTask().execute("");
              }
			}
		});
        //查看升级版本
        btnVersionLogcat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        //清除本地日志
        deleteVersionLogcat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Toast.makeText(getActivity(), "清除完成", Toast.LENGTH_SHORT).show();
			}
		});
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private class updateApkTask extends AsyncTask<String,String,String> {
        String url_version = "http://demo.zhiyun360.com/appstore/XLab/lightAdjust/lightAdjust.txt";
        String url_apk = "http://demo.zhiyun360.com/appstore/XLab/lightAdjust/lightAdjust.apk";
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            InputStreamReader in = null;
            URL url;
            try {
                url = new URL(url_version);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setRequestProperty("ContentType",
                        "text/xml;charset=utf-8");
                in = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuffer strBuffer = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null){
                    strBuffer.append(line);
                }
                connection.disconnect();

                v = strBuffer.toString();
                return v;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //作UI线程的修改
            if(result.length() != 0 && !result.equals(verName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("检测到新的版本"+result+"是否更新软件");
                builder.setTitle("软件更新");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), UpdateService.class);
                        i.putExtra("url", url_apk);
                        getActivity().startService(i);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }
}
