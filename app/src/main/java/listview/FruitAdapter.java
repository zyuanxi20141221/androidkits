package listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import zheng.androidkits.R;

public class FruitAdapter extends BaseAdapter {

    private Context context;
    private List<String> fruitList;

    public FruitAdapter(Context context, List<String> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
    }

    @Override
    public int getCount() {
        return fruitList.size();
    }

    @Override
    public Object getItem(int position) {
        return fruitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fruit_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fruitName = view.findViewById(R.id.fruit_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            //取出缓存
            viewHolder = (ViewHolder) view.getTag();
        }
        String fruit = (String) getItem(position);
        viewHolder.fruitName.setText(fruit);
        return view;
    }

    class ViewHolder {
        TextView fruitName;
    }
}
