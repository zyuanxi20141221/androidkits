package zheng.androidkits.glid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 *图片旋转的切换
 */
public class RotateTransformation extends BitmapTransformation {

    private float rotateRotationAngle = 0.0f;

    public RotateTransformation(Context context, float angle) {
        super(context);
        this.rotateRotationAngle = angle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateRotationAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return getClass().getName() + Math.round(rotateRotationAngle);
    }
}
