package com.example.robin.papers.studentCircle.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SDCardUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

/**
 * 趣聊初始界面的列表，包括头部头像以及下方帖子列表
 */
public class PullToZoomListView extends ListView implements
		AbsListView.OnScrollListener {
	private static final int INVALID_VALUE = -1;
	private static final String TAG = "PullToZoomListView";
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float paramAnonymousFloat) {
			float f = paramAnonymousFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};
	int mActivePointerId = -1;
	private RelativeLayout mHeaderContainer;
	private int mHeaderHeight;
	private ImageView mHeaderImage;
	float mLastMotionY = -1.0F;
	float mLastScale = -1.0F;
	float mMaxScale = -1.0F;
	private AbsListView.OnScrollListener mOnScrollListener;
	private ScalingRunnalable mScalingRunnalable;
	private int mScreenHeight;
	private BackTouchEvent event;
	private Context context;
	public static View mFooterView;


	public PullToZoomListView(Context paramContext) {
		super(paramContext);
		this.context=paramContext;
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
							  AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
        this.context=paramContext;
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
							  AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
        this.context=paramContext;
		init(paramContext);
	}

	public void setTouchEvent(BackTouchEvent event){
		this.event = event;
	}

	private void endScraling() {
		if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
			this.mScalingRunnalable.startAnimation(200L);
	}

	private void init(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		this.mScreenHeight = localDisplayMetrics.heightPixels;
		//初始化底部加载动画
        this.mFooterView = View.inflate(context, R.layout.view_footer, null);

		this.mHeaderContainer = (RelativeLayout) View.inflate(paramContext, R.layout.mixshow_headview, null);
		this.mHeaderImage = (ImageView)this.mHeaderContainer.findViewById(R.id.maxshowimg);
		//趣聊头部用户头像
		ImageView userview = (ImageView)mHeaderContainer.findViewById(R.id.userimg);
//		this.mHeaderImage.setBackgroundResource(R.mipmap.ic_launcher);
		Drawable drawable=Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"avatar")));
		userview.setImageDrawable(drawable);
//		ImageLoaders.setsendimg("http://120.77.32.233/qmkl1.0.0/user/download/avatar/2ac644684a06318eb628257798d3d357.jpg", userview);
		//趣聊头部用户名称
		TextView userID=mHeaderContainer.findViewById(R.id.userID);
		String username=SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"nickname");
		userID.setText(username);

		int i = localDisplayMetrics.widthPixels;
		setHeaderViewSize(i, (int) (9.0F * (i / 12.0F)));
//		this.mHeaderContainer.addView(this.mHeaderImage);
		addHeaderView(this.mHeaderContainer);
		this.mScalingRunnalable = new ScalingRunnalable();
		super.setOnScrollListener(this);
	}

	private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
		int i = (paramMotionEvent.getAction()) >> 8;
		if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
			if (i != 0) {
				int j = 1;
				this.mLastMotionY = paramMotionEvent.getY(0);
				this.mActivePointerId = paramMotionEvent.getPointerId(0);
				return;
			}
	}

	private void reset() {
		this.mActivePointerId = -1;
		this.mLastMotionY = -1.0F;
		this.mMaxScale = -1.0F;
		this.mLastScale = -1.0F;
	}

	public ImageView getHeaderView() {
		return this.mHeaderImage;
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return super.onInterceptTouchEvent(paramMotionEvent);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
							int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.mHeaderHeight == 0)
			this.mHeaderHeight = this.mHeaderContainer.getHeight();
	}

	@Override
	public void onScroll(AbsListView paramAbsListView, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
		if ((f > 0.0F) && (f < this.mHeaderHeight)) {
			int i = (int) (0.65D * f);
			this.mHeaderImage.scrollTo(0, -i);
		} else if (this.mHeaderImage.getScrollY() != 0) {
			this.mHeaderImage.scrollTo(0, 0);
		}
		if (this.mOnScrollListener != null) {
			this.mOnScrollListener.onScroll(paramAbsListView, firstVisibleItem,
                    visibleItemCount, totalItemCount);
		}
        if(visibleItemCount+firstVisibleItem==totalItemCount){
            Log.d(TAG, "滑到底部");
        }
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int scrollState) {
//		if (this.mOnScrollListener != null)
//			this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
//                    scrollState);

        switch (scrollState) {
            // 当不滚动时
            case OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (paramAbsListView.getLastVisiblePosition() == (paramAbsListView.getCount() - 1)) {
                    if(getFooterViewsCount()==0){
                        MixShowActivity.mixlist.addFooterView(mFooterView);
                        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
                        PostRequest postRequest=new PostRequest(token,String.valueOf(++MixShowActivity.page));
                        RetrofitUtils.postAllPost(context,postRequest,MixShowActivity.data);
						Log.d(TAG, "请求");
                    }
                }
                break;
        }
	}


	public void setHeaderViewSize(int paramInt1, int paramInt2) {
		Object localObject = this.mHeaderContainer.getLayoutParams();
		if (localObject == null)
			localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
		((ViewGroup.LayoutParams) localObject).width = paramInt1;
		((ViewGroup.LayoutParams) localObject).height = paramInt2;
		this.mHeaderContainer
				.setLayoutParams((ViewGroup.LayoutParams) localObject);
		this.mHeaderHeight = paramInt2;
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}


	class ScalingRunnalable implements Runnable {
		long mDuration;
		boolean mIsFinished = true;
		float mScale;
		long mStartTime;

		ScalingRunnalable() {
		}

		public void abortAnimation() {
			this.mIsFinished = true;
		}

		public boolean isFinished() {
			return this.mIsFinished;
		}

		public void run() {
			float f2;
			ViewGroup.LayoutParams localLayoutParams;
			if ((!this.mIsFinished) && (this.mScale > 1.0D)) {
				float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
						/ (float) this.mDuration;
				f2 = this.mScale - (this.mScale - 1.0F)
						* PullToZoomListView.sInterpolator.getInterpolation(f1);
				localLayoutParams = PullToZoomListView.this.mHeaderContainer
						.getLayoutParams();
				if (f2 > 1.0F) {
					localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
					localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
					PullToZoomListView.this.mHeaderContainer
							.setLayoutParams(localLayoutParams);
					PullToZoomListView.this.post(this);
					return;
				}
				this.mIsFinished = true;
			}
		}

		public void startAnimation(long paramLong) {
			this.mStartTime = SystemClock.currentThreadTimeMillis();
			this.mDuration = paramLong;
			this.mScale = ((float) (PullToZoomListView.this.mHeaderContainer
					.getBottom()) / PullToZoomListView.this.mHeaderHeight);
			this.mIsFinished = false;
			PullToZoomListView.this.post(this);
		}
	}

	public interface BackTouchEvent {
		/**
		 * @return whether the adapter was set or not.
		 */
		public void OnTouchEvent();
	}
}
