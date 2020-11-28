package com.hfax.ucard.modules.entrance;

import android.graphics.Canvas;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.hfax.app.BaseFragment;
import com.hfax.lib.BaseApplication;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.utils.PermissionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.widget.GuideBottomView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by eson on 2017/8/9.
 */

public class GuideActivity extends BaseNetworkActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.guide_bottom_view)
    GuideBottomView guideBottomView;
    @BindView(R.id.start_btn)
    View start_btn;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_guide;
    }

    private GuideAdapter mAdapter;
    private int[] gifs = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4};
    private List<GuideFragment> fragments = new ArrayList<>();

    @Override
    public void initData() {
        for (int ignored : gifs) {
            fragments.add(new GuideFragment());
        }
        mAdapter = new GuideAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        PermissionUtils.initPermission(this);
        guideBottomView.setSize(mAdapter.getCount());
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        final long delayMillis = 200;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    onPageSelected(0);
                } catch (Exception e) {
                    mHandler.postDelayed(this, delayMillis);
                }
            }
        }, delayMillis);
    }

    private void openApp() {
        UCardUtil.startActivity(this, getIntent().setClass(this, MainActivity.class));
        finish();
    }

    @Override
    public void initListener() {
    }

    @OnClick({R.id.skip, R.id.start_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
            case R.id.start_btn:
                openApp();
                break;
            default:
        }
    }

    @Override
    protected boolean isStartSupportGestureFinish() {
        return false;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        Runnable runnable = null;
        if (position == gifs.length - 1) {
            guideBottomView.setVisibility(View.GONE);
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mViewPager.getCurrentItem() == position) {
                        start_btn.setVisibility(View.VISIBLE);
                    }
                }
            };
        } else {
            guideBottomView.setVisibility(View.VISIBLE);
            start_btn.setVisibility(View.GONE);
            guideBottomView.setPosition(position);
        }
        try {
            mAdapter.getItem(position).setGif(gifs[position], runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected boolean isNeedNoNetworkProcess() {
        return false;
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }

    public class GuideAdapter extends FragmentPagerAdapter {

        public GuideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public GuideFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return gifs.length;
        }
    }

    public static class GuideFragment extends BaseFragment {
        ImageView iv;
        RequestManager requestManager;

        @Override
        protected int getLayoutRes() {
            return 0;
        }

        @Override
        protected View getLayoutView() {
            iv = new ImageView(getContext()) {
                @Override
                protected void onDraw(Canvas canvas) {
                    try {
                        super.onDraw(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            iv.setBackgroundColor(0xFFF9FAFF);
            return iv;
        }

        @Override
        protected void initData() {

        }

        @Override
        protected void initListener() {

        }

        public void setGif(int id, final Runnable runnable) {
            if (iv != null && requestManager == null) {
                requestManager = Glide.with(this);
                requestManager.load(id).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Integer, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception arg0, Integer arg1, Target<GlideDrawable> arg2, boolean arg3) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // 计算动画时长
                        int duration = 0;
                        GifDrawable drawable = (GifDrawable) resource;
                        GifDecoder decoder = drawable.getDecoder();
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }
                        mHandler.postDelayed(runnable, duration);
                        return false;
                    }
                }) //仅仅加载一次gif动画
                        .into(new GlideDrawableImageViewTarget(iv, 1));

            } else if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        public void onDestroyView() {
            try {
                if (requestManager != null) {
                    requestManager.onDestroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                requestManager = null;
            }
            super.onDestroyView();
            iv=null;
        }
    }
}
