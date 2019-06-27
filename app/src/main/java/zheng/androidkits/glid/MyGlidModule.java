package zheng.androidkits.glid;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * 自定义Glide module
 */
public class MyGlidModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888); //这里使用高质量的图片格式，每个像素占用4个字节,glide默认使用的RGB_565(每个像素占用2个字节)

    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
