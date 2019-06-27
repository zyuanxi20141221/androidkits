package recycleview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import zheng.androidkits.R;

public class RecycleViewActivity extends Activity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

        //获取控件
        recyclerView = findViewById(R.id.recycle_view);

        //设置布局方式
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //线性布局
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 5)); //网格布局
//        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.VERTICAL, R.drawable.divider_drawable)); //添加自定义分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);//系统默认分割线
        recyclerView.setHasFixedSize(true);

        List<News> newsList = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            News news = new News();
            news.setTitle("以习近平同志为核心的党中央坚定不移推进全面深化改革" + i);
            news.setSource("新华网" + i);
            news.setPublishtime("2019-03-04");
            newsList.add(news);
        }

        NewsAdapter newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        /*点击事件*/
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int positon, String data) {
                Logger.d("onItemClick " + positon + " " + data);
            }
        });

        /*长按事件*/
        newsAdapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int positon, String data) {
                Logger.d("onItemLongClick " + positon + " " + data);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
