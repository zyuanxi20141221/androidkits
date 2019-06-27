package listview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import zheng.androidkits.R;

public class ListViewActivity extends Activity {

    @BindView(R.id.list_view)
    ListView listView;

    FruitAdapter fruitAdapter;

    private String[] data = {
            "Apple",
            "Banana","Orange","Watermelon",
            "Pear","Grape","Pineapple",
            "Strawberry","Cherry","Mango"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        fruitAdapter = new FruitAdapter(this,Arrays.asList(data));
        listView.setAdapter(fruitAdapter);
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
