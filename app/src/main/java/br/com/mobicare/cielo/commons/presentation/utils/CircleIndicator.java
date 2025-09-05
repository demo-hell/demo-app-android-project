package br.com.mobicare.cielo.commons.presentation.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.ViewPager;


/**
 * Created by benhur.souza on 26/04/2017.
 */

public class CircleIndicator  extends LinearLayout {

    private final static int VISIBLE_WINDOW = 5;
    private int mWindowFirst = 0;
    private int mWindowLast = mWindowFirst + VISIBLE_WINDOW - 1;

    private final static int DEFAULT_INDICATOR_WIDTH = 5;
    private ViewPager mViewpager;
    private int mIndicatorMargin = -1;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mAnimatorResId = me.relex.circleindicator.R.animator.scale_with_alpha;
    private int mAnimatorReverseResId = 0;
    private int mIndicatorBackgroundResId = me.relex.circleindicator.R.drawable.white_radius;
    private int mIndicatorUnselectedBackgroundResId = me.relex.circleindicator.R.drawable.white_radius;
    private Animator mAnimatorOut;
    private Animator mAnimatorIn;
    private Animator mImmediateAnimatorOut;
    private Animator mImmediateAnimatorIn;

    private int mLastPosition = -1;

    public CircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, me.relex.circleindicator.R.styleable.CircleIndicator);
        mIndicatorWidth = typedArray.getDimensionPixelSize(me.relex.circleindicator.R.styleable.CircleIndicator_ci_width, -1);
        mIndicatorHeight = typedArray.getDimensionPixelSize(me.relex.circleindicator.R.styleable.CircleIndicator_ci_height, -1);
        mIndicatorMargin = typedArray.getDimensionPixelSize(me.relex.circleindicator.R.styleable.CircleIndicator_ci_margin, -1);

        mAnimatorResId = typedArray.getResourceId(me.relex.circleindicator.R.styleable.CircleIndicator_ci_animator, me.relex.circleindicator.R.animator.scale_with_alpha);
        mAnimatorReverseResId = typedArray.getResourceId(me.relex.circleindicator.R.styleable.CircleIndicator_ci_animator_reverse, 0);
        mIndicatorBackgroundResId = typedArray.getResourceId(me.relex.circleindicator.R.styleable.CircleIndicator_ci_drawable, me.relex.circleindicator.R.drawable.white_radius);
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(me.relex.circleindicator.R.styleable.CircleIndicator_ci_drawable_unselected, mIndicatorBackgroundResId);

        int orientation = typedArray.getInt(me.relex.circleindicator.R.styleable.CircleIndicator_ci_orientation, -1);
        setOrientation(orientation == VERTICAL ? VERTICAL : HORIZONTAL);

        int gravity = typedArray.getInt(me.relex.circleindicator.R.styleable.CircleIndicator_ci_gravity, -1);
        setGravity(gravity >= 0 ? gravity : Gravity.CENTER);

        typedArray.recycle();
    }

    /**
     * Create and configure Indicator in Java code.
     */
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin,
                me.relex.circleindicator.R.animator.scale_with_alpha, 0, me.relex.circleindicator.R.drawable.white_radius, me.relex.circleindicator.R.drawable.white_radius);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight = (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mIndicatorMargin = (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;

        mAnimatorResId = (mAnimatorResId == 0) ? me.relex.circleindicator.R.animator.scale_with_alpha : mAnimatorResId;

        mAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn.setDuration(0);

        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? me.relex.circleindicator.R.drawable.white_radius : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId = (mIndicatorUnselectedBackgroundResId == 0) ? mIndicatorBackgroundResId : mIndicatorUnselectedBackgroundResId;
    }

    private Animator createAnimatorOut(Context context) {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId);
    }

    private Animator createAnimatorIn(Context context) {
        Animator animatorIn;
        if (mAnimatorReverseResId == 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
            animatorIn.setInterpolator(new ReverseInterpolator());
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
        }
        return animatorIn;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            mLastPosition = -1;
            createIndicators();
            mViewpager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }

    private void showBullet(int index) {
        toggleBulletVisibility(index, true);
    }

    private void hideBullet(int index) {
        toggleBulletVisibility(index, false);
    }

    private void toggleBulletVisibility(final int index, boolean visible) {
        final View indicator = getChildAt(index);
        if (indicator != null) {
            if (visible) {
                indicator.setVisibility(VISIBLE);
            } else {
                indicator.setVisibility(GONE);
            }
        }
    }

    private void expandBullet(int index) {
        toggleBulletSize(index, true);
    }

    private void shrinkBullet(int index) {
        toggleBulletSize(index, false);
    }

    private void toggleBulletSize(int index, boolean expand) {
        View indicator = getChildAt(index);
        if (indicator != null) {
            if (expand) {
                indicator.getLayoutParams().width = mIndicatorWidth;
                indicator.getLayoutParams().height = mIndicatorHeight;
            } else {
                indicator.getLayoutParams().width = (int) (mIndicatorWidth * .7);
                indicator.getLayoutParams().height = (int) (mIndicatorHeight * .7);
            }
        }
    }

    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //This return onPage
        }

        @Override public void onPageSelected(int position) {

            if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
                return;
            }

            int lastPage = mViewpager.getAdapter().getCount() - 2;

            if (mAnimatorIn.isRunning()) {
                mAnimatorIn.end();
                mAnimatorIn.cancel();
            }

            if (mAnimatorOut.isRunning()) {
                mAnimatorOut.end();
                mAnimatorOut.cancel();
            }

            // BEGIN

            if (position > mLastPosition) { // Avancando
                if (position >= mWindowLast) {
                    if (position < lastPage) {
                        shrinkBullet(mWindowFirst);
                        hideBullet(mWindowFirst - 1);
                    }

                    mWindowLast++;
                    mWindowFirst++;

                    expandBullet(mWindowLast);
                    showBullet(mWindowLast+1);
                }

            } else { // Voltando

                if (position <= mWindowFirst) {
                    if (position > 0) {
                        shrinkBullet(mWindowLast);
                        hideBullet(mWindowLast + 1);
                    }

                    mWindowFirst--;
                    mWindowLast--;

                    expandBullet(mWindowFirst);
                    showBullet(mWindowFirst-1);
                }
            }

            // END

            View currentIndicator;
            if (mLastPosition >= 0 && (currentIndicator = getChildAt(mLastPosition)) != null) {
                currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
                mAnimatorIn.setTarget(currentIndicator);
                mAnimatorIn.start();
            }

            View selectedIndicator = getChildAt(position);
            if (selectedIndicator != null) {
                selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
                mAnimatorOut.setTarget(selectedIndicator);
                mAnimatorOut.start();
            }
            mLastPosition = position;
        }

        @Override public void onPageScrollStateChanged(int state) {
            //This return onPageScroll
        }
    };

    public DataSetObserver getDataSetObserver() {
        return mInternalDataSetObserver;
    }

    private final DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override public void onChanged() {
            super.onChanged();
            if (mViewpager == null) {
                return;
            }

            int newCount = mViewpager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {  // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = mViewpager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }

            createIndicators();
        }
    };

    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void createIndicators() {
        removeAllViews();

        //Serão apresentados 2 cards juntos, logo deve-se retirar um indicador
        int count = mViewpager.getAdapter().getCount() - 1;

        //Caso só tenha 2 cards não exibir o indicador
        if (count <= 1) {
            this.setVisibility(GONE);
            return;
        }
        int currentItem = mViewpager.getCurrentItem();
        int orientation = getOrientation();

        for (int i = 0; i < count; i++) {
            boolean visible = i < VISIBLE_WINDOW;
            if (currentItem == i) {
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateAnimatorOut, visible);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId, mImmediateAnimatorIn, visible);
            }
        }

        showBullet(mWindowLast+1);

    }

    private void addIndicator(int orientation, @DrawableRes int backgroundDrawableId, Animator animator, boolean visible) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View indicator = new View(getContext());
        indicator.setBackgroundResource(backgroundDrawableId);
        addView(indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) indicator.getLayoutParams();

        if (orientation == HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin;
            lp.rightMargin = mIndicatorMargin;
        } else {
            lp.topMargin = mIndicatorMargin;
            lp.bottomMargin = mIndicatorMargin;
        }

        indicator.setLayoutParams(lp);

        if (!visible) {
            indicator.setVisibility(GONE);
            indicator.getLayoutParams().width = (int) (mIndicatorWidth * .7);
            indicator.getLayoutParams().height = (int) (mIndicatorHeight * .7);
        }

        animator.setTarget(indicator);
        animator.start();
    }

    private class ReverseInterpolator implements Interpolator {
        @Override public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
