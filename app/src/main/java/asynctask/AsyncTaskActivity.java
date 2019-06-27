package asynctask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zheng.androidkits.R;

/**
 * android异步任务，只能被执行一次,多次调用时将会出现异常
 */
public class AsyncTaskActivity extends Activity {

    @BindView(R.id.btn_startasync)
    Button btnAsyncTask;

    @BindView(R.id.async_text)
    TextView asyncText;

    private MyAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asynctask);
        ButterKnife.bind(this);
        myAsyncTask = new MyAsyncTask(asyncText);
    }

    /**
     * AsyncTask参数说明：
     * Params:输入参数。对应的是调用自定义的AsyncTask的类中调用excute()方法中传递的参数。如果不需要传递参数，则直接设为Void即可。
     * Progress：子线程执行的百分比
     * Result：返回值类型。和doInBackground（）方法的返回值类型保持一致
     */
    public class MyAsyncTask extends AsyncTask<Void, Integer, String> {

        private TextView showText;

        private WeakReference weakReference;

        public MyAsyncTask(Context context) {
            weakReference = new WeakReference<>(context);
        }

        public MyAsyncTask(TextView textView) {
            this.showText = textView;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Logger.t("AsyncTaskActivity").d("doInBackground传入参数");
            //模拟耗时的操作
            try {
                int i = 0;
                while (i <= 50) {
                    i++;
                    publishProgress(i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "doInBackground返回";
        }

        /**
         * 该方法运行在UI线程当中,可以对UI空间进行设置
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Logger.t("AsyncTaskActivity").d("开始执行AsyncTask");
            showText.setText("开始执行");
        }

        /**
         * 接收doInBackground的返回值
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Logger.t("AsyncTaskActivity").d("AsyncTask执行结束" + s);
            showText.setText("执行结束");
        }

        /**
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Logger.t("AsyncTaskActivity").d("onProgressUpdate " + values[0]);
            showText.setText(values[0]+"");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @OnClick(R.id.btn_startasync)
    public void onAsyncTaskButtonClick() {
        myAsyncTask.execute();
    }
}
