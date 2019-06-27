package zheng.androidkits;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zheng.androidkits.glid.MyLinearLayout;
import zheng.androidkits.glid.RotateTransformation;
import zheng.androidkits.glid.RoundTransformation;

public class GlidActivity extends AppCompatActivity {

    @BindView(R.id.btn_loadimage)
    Button btn_loadimage;

    @BindView(R.id.btn_loadthumbimage)
    Button btn_loadthumbimage;

    @BindView(R.id.btn_imagesimpletarget)
    Button btn_loadtargetimage;

    @BindView(R.id.btn_imagetransformation)
    Button btn_imagetransformation;

    private String url = "http://img1.dzwww.com:8080/tupian_pl/20150813/16/7858995348613407436.jpg";

    private String backgroundUrl = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glid);
        ButterKnife.bind(this);
        loadBackgroundImage();
    }

    /**
     * 引用图片到指定控件作为背景
     */
    public void loadBackgroundImage() {
        Glide.with(this)
                .load(backgroundUrl)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        linearLayout.setBackground(resource);
                    }
                });
    }

    @OnClick(R.id.btn_loadimage)//glid常用参数
    public void onLoadImageClick() {
        ImageView imageView = findViewById(R.id.imageView1);
        Glide.with(this)
                .load(url)
                .skipMemoryCache(true) //跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) //关闭磁盘缓存
                //.DiskCacheStrategy.NONE 什么都不缓存
                //.DiskCacheStrategy.SOURCE 只缓存全尺寸图
                //.DiskCacheStrategy.RESULT 只缓存最终的加载图
                //.DiskCacheStrategy.ALL 缓存所有版本图（默认行为）

                .priority(Priority.HIGH) //设置图片加载的优先级
                .crossFade(2000) //添加动画效果
                .into(imageView);
    }

    @OnClick(R.id.btn_loadthumbimage) //glid加载缩略图
    public void loadImageThumbnailRequest() {
        ImageView imageView = findViewById(R.id.imageView2);
        DrawableRequestBuilder<String> thumbnailRequest = Glide.with(this).load(url);

        Glide.with(this)
                .load(url)
                .thumbnail(thumbnailRequest)
                .into(imageView);
    }

    @OnClick(R.id.btn_imagesimpletarget)
    public void loadImageSimpleTarget() {
        Glide.with(this.getApplicationContext())
                .load(url)
                .asBitmap()
                .into(simpleTarget);
    }

    private SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>(50, 50) {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            ImageView imageView = findViewById(R.id.imageView3);
            imageView.setImageBitmap(resource);
        }
    };

    /***
     * 图片显示之前对图片执行预处理
     */
    @OnClick(R.id.btn_imagetransformation)
    public void loadTransformationImage() {
        ImageView imageView = findViewById(R.id.imageView4);
        Glide.with(this)
                .load(url)
                .transform(new RoundTransformation(this, 20), new RotateTransformation(this, 90))
                .into(imageView);
    }

}
