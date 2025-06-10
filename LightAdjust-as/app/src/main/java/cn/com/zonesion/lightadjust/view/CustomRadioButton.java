package cn.com.zonesion.lightadjust.view;

import cn.com.zonesion.lightadjust.view.CustomRadioButton;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

public class CustomRadioButton extends RadioButton{
	private static final String TAG = CustomRadioButton.class.getSimpleName();

    public CustomRadioButton(Context context) {
        super(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public CustomRadioButton(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	 Drawable[] drawables = getCompoundDrawables();
         Drawable drawable = drawables[0];
         int gravity = getGravity();
         int left = 0;
         if(gravity == Gravity.CENTER) {
             left = ((int) (getWidth() - drawable.getIntrinsicWidth() - getPaint().measureText(getText().toString()))
                     /2);
         }
         drawable.setBounds(left, 0, left + drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
}
