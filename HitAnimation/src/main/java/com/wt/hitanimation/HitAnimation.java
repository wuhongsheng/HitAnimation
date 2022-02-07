package com.wt.hitanimation;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aichiptech.ui.hitanimation.R;

/**
 * 命中动画帮助类
 *
 * @author whs
 * @date 2021/8/6
 */
public class HitAnimation {
    private static final String TAG = HitAnimation.class.getSimpleName();
    private AnimationDrawable animationDrawable;
    private Context mContext;
    private WindowManager mWindowManager;
    private ImageView mImageView;
    private View mAnchor;
    /**
     * Animation size
     */
    private int size;
    /**
     * Animation 资源ID
     */
    private int animationId;
    /**
     * 偏移量
     */
    private int offsetX,offsetY;
    private Handler animHandler = new Handler();
    private int duration;
    private int screenWidthDiff,screenHeightDiff,statusBarHeight,totalScreenHeightDiff,halfSize;
    private CallBackListener callBackListener;

    /**
     * Animation 完成回调，由调用方决定处理逻辑
     */
    public interface CallBackListener{
        void onAnimationFinish(View mAnchor);
    }

    public HitAnimation(Builder builder) {
        this.mContext = builder.mContext;
        this.animationId = builder.animationId;
        this.size = builder.size;
        this.mAnchor = builder.mAnchor;
        this.duration = builder.duration;
        this.callBackListener = builder.callBackListener;
        this.offsetX = builder.offsetX;
        this.offsetY = builder.offsetY;
        initWindow();
    }

    public void initWindow() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        statusBarHeight = getStatusBarHeight();
        screenWidthDiff = getScreenWidth() - getAppScreenWidth();
        screenHeightDiff = getScreenHeight() - getAppScreenHeight();
        //totalScreenHeightDiff = screenHeightDiff + statusBarHeight;
        totalScreenHeightDiff = screenHeightDiff;
        halfSize = size / 2;
        Log.i(TAG, "halfSize:"+halfSize);
        Log.i(TAG, "statusBarHeight:"+statusBarHeight);
        Log.i(TAG, "screenWidthDiff:"+screenWidthDiff);
        Log.i(TAG, "totalScreenHeightDiff:"+totalScreenHeightDiff);
    }

    public void handleEvent(View view) {
        if(view == null){
            return;
        }
        this.mAnchor = view;
        if (animationDrawable == null) {
            initAnimation();
        }
        addView();
        playAnim();
    }

    public void handleEvent(){
        handleEvent(mAnchor);
    }

    Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            stopAnim();
            if(callBackListener != null){
                callBackListener.onAnimationFinish(mAnchor);
            }else {
                mAnchor.performClick();
            }
        }
    };

    public void playAnim() {
        animationDrawable.start();
        animHandler.postDelayed(clickRunnable, duration);
    }

    private void initAnimation() {
        mImageView = new ImageView(mContext);
        mImageView.setBackgroundResource(animationId);
        animationDrawable = (AnimationDrawable) mImageView.getBackground();
        if(duration == 0){
            duration = getDuration(animationDrawable);
        }
        Log.i(TAG, "duration" + duration);
    }

    private int getDuration(AnimationDrawable animationDrawable) {
        if(animationDrawable == null){
            return 0;
        }
        for (int i = 0; i < animationDrawable.getNumberOfFrames() ; i++) {
            duration += animationDrawable.getDuration(i);
        }
        return duration;
    }

    private void stopAnim() {
        if(animationDrawable != null){
            animationDrawable.stop();
        }
        removeView();
    }

    private void removeView() {
        if (mImageView != null && mImageView.isAttachedToWindow()) {
            mWindowManager.removeView(mImageView);
        }
    }

    private void addView() {
        if (mAnchor == null) {
            return;
        }
        int height = mAnchor.getHeight();
        int width = mAnchor.getWidth();
        Log.i(TAG, "addView: "+width+"x"+height);
        int[] location = new int[2];
        int[] location2 = new int[2];

        mAnchor.getLocationOnScreen(location);
        mAnchor.getLocationInWindow(location2);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.width = size;
        layoutParams.height = size;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        Log.i(TAG, "halfSize: "+halfSize);
        layoutParams.x = location[0] + width / 2 - halfSize - screenWidthDiff + offsetX;
        layoutParams.y = location[1] + height / 2 - halfSize - totalScreenHeightDiff + offsetY;
        Log.i(TAG, "layoutParams.x" + layoutParams.x);
        Log.i(TAG, "layoutParams.y" + layoutParams.y);
        layoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowManager.addView(mImageView, layoutParams);
    }

    /**
     * 回收动画内存
     * 回收完之后可以请求System.gc();回收　
     * @param animationDrawables
     */
    private void tryRecycleAnimationDrawable(AnimationDrawable animationDrawables) {
        if (animationDrawables != null) {
            animationDrawables.stop();
            for (int i = 0; i < animationDrawables.getNumberOfFrames(); i++) {
                Drawable frame = animationDrawables.getFrame(i);
                if (frame instanceof BitmapDrawable) {
                    ((BitmapDrawable) frame).getBitmap().recycle();
                }
                frame.setCallback(null);
            }
            animationDrawables.setCallback(null);

        }
    }


    public void release(){
        tryRecycleAnimationDrawable(animationDrawable);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getScreenHeight() {
        if (mWindowManager == null) return -1;
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public int getScreenWidth() {
        if (mWindowManager == null) return -1;
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public int getAppScreenWidth() {
        if (mWindowManager == null) return -1;
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }


    public int getAppScreenHeight() {
        if (mWindowManager == null) return -1;
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    /**
     * 控件1/3宽度*1/3高度的面积区域是否在屏幕可见
     * @param view
     * @return
     */
    public boolean isVisibleToUser(View view) {
        Rect rect = new Rect();
        if (view.getGlobalVisibleRect(rect)) {
            if (rect.right - rect.left >= view.getMeasuredWidth() / 3
                && (rect.bottom - rect.top >= view.getMeasuredHeight() / 3)) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    public static class Builder {
        private int size = 120;
        private int animationId = R.drawable.hitanimation_default;
        private View mAnchor;
        private Context mContext;
        private int duration = 0;
        private CallBackListener callBackListener;
        private int offsetX,offsetY;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setAnimationId(int animationId) {
            this.animationId = animationId;
            return this;
        }
        public Builder setAnchorView(View anchor) {
            this.mAnchor = anchor;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setOffset(int x,int y){
            this.offsetX = x;
            this.offsetY = y;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }
        public Builder setCallBackListener(CallBackListener callBackListener) {
            this.callBackListener = callBackListener;
            return this;
        }

        public HitAnimation build() {
            return new HitAnimation(this);
        }
    }
}
