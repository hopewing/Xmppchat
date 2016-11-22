package com.whp.xmppchat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whp.xmppchat.R;
import com.whp.xmppchat.fragment.ContactsFragment;
import com.whp.xmppchat.fragment.SessionFragment;
import com.whp.xmppchat.utils.ToolBarUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mActivityMain;
    private TextView mMainTvTitle;
    private ViewPager mMainViewpager;
    private LinearLayout mMainBottom;
    private List<Fragment>mFragments=new ArrayList<Fragment>();
    private String[] mToolBarTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        initDatas();
        initListener();
    }

    private void initListener() {
        mMainViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //修改颜色
                ToolBarUtil.changeColor(position);
                mMainTvTitle.setText(mToolBarTexts[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ToolBarUtil.SetOnToolBarClickListener(new ToolBarUtil.OnToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                mMainViewpager.setCurrentItem(position);
            }
        });
    }

    private void assignViews() {
        mActivityMain = (LinearLayout) findViewById(R.id.activity_main);
        mMainTvTitle = (TextView) findViewById(R.id.main_tv_title);
        mMainViewpager = (ViewPager) findViewById(R.id.main_viewpager);
        mMainBottom = (LinearLayout) findViewById(R.id.main_bottom);
    }

    private void initDatas() {
        //添加fragment
        mFragments.add(new SessionFragment());
        mFragments.add(new ContactsFragment());
        mMainViewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //添加底部按钮
        mToolBarTexts = new String[]{"会话","联系人"};
        int[] toolBarIcons={R.drawable.selector_icon_meassage,R.drawable.selector_icon_selfinfo};
        ToolBarUtil.createToolBar(mMainBottom, mToolBarTexts,toolBarIcons);
        //设置默认选中
        ToolBarUtil.changeColor(0);
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
