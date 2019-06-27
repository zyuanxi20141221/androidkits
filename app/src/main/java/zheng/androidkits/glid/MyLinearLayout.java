package zheng.androidkits.glid;

import android.content.Context;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.jar.Attributes;

/**
 * 将图片加载为控件背景
 */
public class MyLinearLayout extends LinearLayout {

    private ViewTarget<MyLinearLayout, GlideDrawable> viewTarget;

    public MyLinearLayout(Context context) {
        super(context);
        viewTarget = new ViewTarget<MyLinearLayout, GlideDrawable>(this) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                MyLinearLayout myLinearLayout = getView();
                myLinearLayout.setImageAsBackground(resource);
            }
        };
    }

    public ViewTarget<MyLinearLayout, GlideDrawable> getViewTarget() {
        return viewTarget;
    }

    public void setImageAsBackground(GlideDrawable resource) {
        setBackground(resource);
    }
}
