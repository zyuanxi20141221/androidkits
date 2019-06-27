package fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import zheng.androidkits.R;

public class Fragment1 extends Fragment {

    String params;
    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context; //获取Activity对象
        params = getArguments().getString("param_key"); //获取参数
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_1, container, false);
        TextView textView = root.findViewById(R.id.fragment1_text);
        textView.setText(params);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static Fragment1 newInstance(String str) {
        Fragment1 fragment = new Fragment1();
        Bundle bundle = new Bundle();
        bundle.putString("param_key", str);
        fragment.setArguments(bundle); //设置参数
        return fragment;
    }
}
