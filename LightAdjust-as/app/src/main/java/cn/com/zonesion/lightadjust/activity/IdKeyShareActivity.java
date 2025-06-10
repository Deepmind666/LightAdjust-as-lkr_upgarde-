package cn.com.zonesion.lightadjust.activity;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cn.com.zonesion.lightadjust.R;
import cn.com.zonesion.lightadjust.bean.IdKeyBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * IdKeyShareActivity是当你在IDKey页面点击分享按钮的时候，弹出的Activity
 * IdKeyShareActivity是和MacSettingShareActivity共用同一个布局，界面由一个ImageView控件和一个TextView控件组成
 * ImageView用来显示二维码图片，TextView用来显示提示信息
 */

public class IdKeyShareActivity extends Activity{
	 /**
     * 用来显示二维码图片
     */
    private ImageView imageQrCode;

    /**
     * 该方法在IdKeyShareActivity初始化的时候调用，将ButterKnife与该Activity进行绑定，并将
     * 视图控件进行初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        imageQrCode = (ImageView) findViewById(R.id.image_qr_code);
        initView();//初始化视图控件
    }

    /**
     * 该方法用来进行控件的初始化
     */
    private void initView() {
        //获取屏幕的宽高，单位是像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        //获取IDKeyFragment传递过来的数据
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String key = intent.getStringExtra("key");
        String server = intent.getStringExtra("server");

        //将获取到的数据通过构造方法赋值给IdKeyBean
        IdKeyBean idKeyBean = new IdKeyBean(id,key,server);

        //创建一个Gson对象，并将IdKeyBean对象idKeyBean转换为json字符串
        Gson gson = new Gson();
        String json = gson.toJson(idKeyBean);

        //通过generateBitmap()方法并将json字符串传入，生成相应的二维码的Bitmap对象
        Bitmap bitmap = generateBitmap(json, screenWidth/3, screenHeight/3);

        //将生成的二维码的Bitmap对象在ImageView中显示
        imageQrCode.setImageBitmap(bitmap);

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
}
