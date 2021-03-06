package org.wbing.layout.content;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangbing
 * @date 2018/8/22
 */
public class WContentLayout extends FrameLayout {
    public static WContentLayout wrap(Activity activity) {
        return wrap(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
    }

    public static WContentLayout wrap(Fragment fragment) {
        return wrap(fragment.getView());
    }

    public static WContentLayout wrap(View view) {
        if (view == null) {
            throw new RuntimeException("content view can not be null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            throw new RuntimeException("parent view can not be null");
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int index = parent.indexOfChild(view);
        parent.removeView(view);

        WContentLayout layout = new WContentLayout(view.getContext());
        parent.addView(layout, index, lp);
        layout.addView(view);
        layout.setContentView(view);
        return layout;
    }


    int mEmptyImage;
    CharSequence mEmptyText;

    int mErrorImage;
    CharSequence mErrorText, mRetryText;
    OnClickListener mRetryButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mRetryListener != null) {
                mRetryListener.onClick(v);
            }
        }
    };
    OnClickListener mRetryListener;
    LayoutInflater mInflater;

    OnInflateListener mOnEmptyInflateListener;
    OnInflateListener mOnErrorInflateListener;

    int mTextColor, mTextSize;
    int mButtonTextColor, mButtonTextSize;
    Drawable mButtonBackground;
    int mEmptyResId = NO_ID, mLoadingResId = NO_ID, mErrorResId = NO_ID;
    int mContentId = NO_ID;

    @SuppressLint("UseSparseArrays")
    Map<Integer, View> mLayouts = new HashMap<>();


    public WContentLayout(@NonNull Context context) {
        this(context, null, R.attr.WContentLayoutStyle);
    }

    public WContentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.WContentLayoutStyle);

    }

    public WContentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WContentLayout, defStyleAttr, R.style.WContentLayout_Style);
        mEmptyImage = a.getResourceId(R.styleable.WContentLayout_w_content_EmptyImage, NO_ID);
        mEmptyText = a.getString(R.styleable.WContentLayout_w_content_EmptyText);

        mErrorImage = a.getResourceId(R.styleable.WContentLayout_w_content_ErrorImage, NO_ID);
        mErrorText = a.getString(R.styleable.WContentLayout_w_content_ErrorText);
        mRetryText = a.getString(R.styleable.WContentLayout_w_content_RetryText);

        mTextColor = a.getColor(R.styleable.WContentLayout_w_content_TextColor, 0xff999999);
        mTextSize = a.getDimensionPixelSize(R.styleable.WContentLayout_w_content_TextSize, dp2px(16));

        mButtonTextColor = a.getColor(R.styleable.WContentLayout_w_content_ButtonTextColor, 0xff999999);
        mButtonTextSize = a.getDimensionPixelSize(R.styleable.WContentLayout_w_content_ButtonTextSize, dp2px(16));
        mButtonBackground = a.getDrawable(R.styleable.WContentLayout_w_content_ButtonBackground);

        mEmptyResId = a.getResourceId(R.styleable.WContentLayout_w_content_EmptyResId, R.layout.w_content_layout_empty);
        mLoadingResId = a.getResourceId(R.styleable.WContentLayout_w_content_LoadingResId, R.layout.w_content_layout_loading);
        mErrorResId = a.getResourceId(R.styleable.WContentLayout_w_content_ErrorResId, R.layout.w_content_layout_error);
        a.recycle();
    }

    public interface OnInflateListener {
        void onInflate(View inflated);
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }
        View view = getChildAt(0);
        setContentView(view);
        showLoading();
    }

    private void setContentView(View view) {
        mContentId = view.getId();
        mLayouts.put(mContentId, view);
    }

    public WContentLayout setLoading(@LayoutRes int id) {
        if (mLoadingResId != id) {
            remove(mLoadingResId);
            mLoadingResId = id;
        }
        return this;
    }

    public WContentLayout setEmpty(@LayoutRes int id) {
        if (mEmptyResId != id) {
            remove(mEmptyResId);
            mEmptyResId = id;
        }
        return this;
    }

    public WContentLayout setOnEmptyInflateListener(OnInflateListener listener) {
        mOnEmptyInflateListener = listener;
        if (mOnEmptyInflateListener != null && mLayouts.containsKey(mEmptyResId)) {
            listener.onInflate(mLayouts.get(mEmptyResId));
        }
        return this;
    }

    public WContentLayout setOnErrorInflateListener(OnInflateListener listener) {
        mOnErrorInflateListener = listener;
        if (mOnErrorInflateListener != null && mLayouts.containsKey(mErrorResId)) {
            listener.onInflate(mLayouts.get(mErrorResId));
        }
        return this;
    }

    public WContentLayout setEmptyImage(@DrawableRes int resId) {
        mEmptyImage = resId;
        image(mEmptyResId, R.id.empty_image, mEmptyImage);
        return this;
    }

    public WContentLayout setEmptyText(String value) {
        mEmptyText = value;
        text(mEmptyResId, R.id.empty_text, mEmptyText);
        return this;
    }

    public WContentLayout setErrorImage(@DrawableRes int resId) {
        mErrorImage = resId;
        image(mErrorResId, R.id.error_image, mErrorImage);
        return this;
    }

    public WContentLayout setErrorText(String value) {
        mErrorText = value;
        text(mErrorResId, R.id.error_text, mErrorText);
        return this;
    }

    public WContentLayout setRetryText(String text) {
        mRetryText = text;
        text(mErrorResId, R.id.retry_button, mRetryText);
        return this;
    }

    public WContentLayout setRetryListener(OnClickListener listener) {
        mRetryListener = listener;
        return this;
    }

    public View showLoading() {
        return show(mLoadingResId);
    }

    public View showEmpty() {
        return show(mEmptyResId);
    }

    public View showError() {
        return show(mErrorResId);
    }

    public View showContent() {
        return show(mContentId);
    }

    private View show(int layoutId) {
        for (View view : mLayouts.values()) {
            view.setVisibility(GONE);
        }
        View view = layout(layoutId);
        view.setVisibility(VISIBLE);
        return view;
    }

    private void remove(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            View vg = mLayouts.remove(layoutId);
            removeView(vg);
        }
    }

    private View layout(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts.get(layoutId);
        }
        View layout = mInflater.inflate(layoutId, this, false);
        layout.setVisibility(GONE);
        addView(layout);
        mLayouts.put(layoutId, layout);

        if (layoutId == mEmptyResId) {
            ImageView img = layout.findViewById(R.id.empty_image);
            if (img != null && mEmptyImage != NO_ID) {
                img.setImageResource(mEmptyImage);
            }
            TextView view = layout.findViewById(R.id.empty_text);
            if (view != null) {
                view.setText(mEmptyText);
                view.setTextColor(mTextColor);
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
            if (mOnEmptyInflateListener != null) {
                mOnEmptyInflateListener.onInflate(layout);
            }
        } else if (layoutId == mErrorResId) {
            ImageView img = layout.findViewById(R.id.error_image);
            if (img != null && mErrorImage != NO_ID) {
                img.setImageResource(mErrorImage);
            }
            TextView txt = layout.findViewById(R.id.error_text);
            if (txt != null) {
                txt.setText(mErrorText);
                txt.setTextColor(mTextColor);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
            TextView btn = layout.findViewById(R.id.retry_button);
            if (btn != null) {
                btn.setText(mRetryText);
                btn.setTextColor(mButtonTextColor);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mButtonTextSize);
                btn.setBackgroundDrawable(mButtonBackground);
                btn.setOnClickListener(mRetryButtonClickListener);
            }
            if (mOnErrorInflateListener != null) {
                mOnErrorInflateListener.onInflate(layout);
            }
        }
        return layout;
    }

    private void text(int layoutId, int ctrlId, CharSequence value) {
        if (mLayouts.containsKey(layoutId)) {
            TextView view = mLayouts.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setText(value);
            }
        }
    }

    private void image(int layoutId, int ctrlId, int resId) {
        if (mLayouts.containsKey(layoutId)) {
            ImageView view = mLayouts.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setImageResource(resId);
            }
        }
    }
}
