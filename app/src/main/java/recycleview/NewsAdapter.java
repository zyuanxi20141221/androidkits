package recycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import zheng.androidkits.R;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<News> newsList;

    private final int HEADER_TYPE = 0;

    private final int FOOTER_TYPE = -1;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == HEADER_TYPE) {
            return createHeaderViewHolder(viewGroup);
        } else if (viewType == FOOTER_TYPE) {
            return createFooterViewHolder(viewGroup);
        } else {
            return createNewsViewHolder(viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof HeaderViewHolder) {
            bindViewForHeader(viewHolder);
        } else if (viewHolder instanceof NewsViewHolder) {
            bindViewForNews(viewHolder, i);
        } else if (viewHolder instanceof FooterViewHolder) {
            bindViewForFooter(viewHolder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == HEADER_TYPE) {
            return 0;
        } else if (position <= newsList.size()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 1 : newsList.size() + 2; //控制position的数目，添加一个头视图和一个脚视图
    }

    public News getItem(int positon) {
        return newsList.get(positon - 1);
    }

    private HeaderViewHolder createHeaderViewHolder(ViewGroup viewGroup) {
        //实例化子布局
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_recycle_view_header, viewGroup, false);
        //获得一个ViewHolder实例
        return new HeaderViewHolder(itemView);
    }

    private NewsViewHolder createNewsViewHolder(ViewGroup viewGroup) {
        //实例化子布局
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_recycle_view_item, viewGroup, false);
        //获得一个ViewHolder实例
        return new NewsViewHolder(itemView);
    }

    private FooterViewHolder createFooterViewHolder(ViewGroup viewGroup) {
        //实例化子布局
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_recycle_view_footer, viewGroup, false);
        //获得一个ViewHolder实例
        return new FooterViewHolder(itemView);
    }

    /**
     * 绑定头视图
     *
     * @param viewHolder
     */
    private void bindViewForHeader(RecyclerView.ViewHolder viewHolder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        headerViewHolder.imageView.setImageResource(R.drawable.header_icon);
    }

    /**
     * 绑定数据到视图
     *
     * @param viewHolder
     * @param position
     */
    private void bindViewForNews(RecyclerView.ViewHolder viewHolder, int position) {
        NewsViewHolder newsViewHolder = (NewsViewHolder) viewHolder;
        News news = getItem(position);
        //把数据填充进去
        newsViewHolder.titleTextView.setText(news.getTitle());
        newsViewHolder.sourceTextViwe.setText(news.getSource());
        newsViewHolder.publishtimeTextView.setText(news.getPublishtime());

        //添加自定义点击事件
        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new MyOnClickListener(position, news.getTitle()));
        }

        //添加自定义长按事件
        if (onItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new MyLongOnClickListener(position, news.getTitle()));
        }
    }

    /**
     * 绑定脚视图
     *
     * @param viewHolder
     */
    private void bindViewForFooter(RecyclerView.ViewHolder viewHolder) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.text_footer.setText("这不是无底洞");
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取头部子控件实例
            imageView = itemView.findViewById(R.id.news_header_image);
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView sourceTextViwe;
        private TextView publishtimeTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取子布局的控件实例
            titleTextView = itemView.findViewById(R.id.news_title);
            sourceTextViwe = itemView.findViewById(R.id.news_source);
            publishtimeTextView = itemView.findViewById(R.id.news_publishtime);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView text_footer;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            text_footer = itemView.findViewById(R.id.news_footer_text);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int position;
        private String data;

        public MyOnClickListener(int position, String data) {
            this.position = position;
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, position, data);
            }
        }
    }

    private class MyLongOnClickListener implements View.OnLongClickListener {
        private int position;
        private String data;

        public MyLongOnClickListener(int position, String data) {
            this.position = position;
            this.data = data;
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, position, data);
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int positon, String data);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int positon, String data);
    }

}
