package com.whp.xmppchat.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whp.xmppchat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ToolBarUtil {
    private static List<TextView> mTextViews = new ArrayList<TextView>();

    public static void createToolBar(LinearLayout container, String[] toolBarTexts, int[] toolBarIcons) {
        for (int i = 0; i < toolBarTexts.length; i++) {
            TextView tv = (TextView) View.inflate(container.getContext(), R.layout.inflate_toolbar_bottom, null);
            tv.setText(toolBarTexts[i]);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, toolBarIcons[i], 0, 0);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height, 1);
            container.addView(tv, params);
            //添加textview到集合中
            mTextViews.add(tv);
            //点击事件
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //不同模块之间传值需要接口回调
                    //3传值的地方用接口对象调用接口方法
                    mOnToolBarClickListener.onToolBarClick(finalI);
                }
            });
        }
    }

    public static void changeColor(int position) {
        //还原所有的颜色
        for (TextView tv : mTextViews) {
            tv.setSelected(false);
        }
        mTextViews.get(position).setSelected(true);
    }

    //1创建接口和接口方法
    public interface OnToolBarClickListener {
        void onToolBarClick(int position);
    }

    //2定义接口变量
    static OnToolBarClickListener mOnToolBarClickListener;

    //4暴露公共方法,不能new接口，所以暴露方法
    public static void SetOnToolBarClickListener(OnToolBarClickListener listener) {
        mOnToolBarClickListener = listener;
    }
}
