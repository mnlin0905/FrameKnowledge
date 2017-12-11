package com.knowledge.mnlin.frame.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knowledge.mnlin.frame.dagger.component.DaggerFragmentComponent;
import com.knowledge.mnlin.frame.dagger.component.FragmentComponent;
import com.knowledge.mnlin.frame.dagger.module.FragmentModule;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 3/9/17.
 */
public abstract class BaseFragment extends Fragment {
	private static final String TAG = "BaseFragment";

	//上下文对象
	@Inject
	protected BaseActivity baseActivity;

	//根部局
	@Inject
	protected ViewGroup viewGroup;

	private FragmentComponent fragmentComponent;

	/**
	 * 数据处理
	 */
	protected  abstract  void initData() ;

	/**
	 * 获取xml布局文件
	 */
	protected abstract @LayoutRes int getContentViewId();

	@Override
	public void onAttach(Context context) {
		Log.v(TAG, "onAttach: " + getClass().getSimpleName());
		super.onAttach(context);
	}

	@Override
	final public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.v(TAG, "onCreate: " + getClass().getSimpleName());
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView: " + getClass().getSimpleName());
		return inflater.inflate(getContentViewId(),null,false);
	}

	@Override
	final public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.v(TAG, "onActivityCreated: " + getClass().getSimpleName());
		super.onActivityCreated(savedInstanceState);

		fragmentComponent= DaggerFragmentComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).fragmentModule(new FragmentModule(this)).build();
		fragmentComponent.inject(this);
		ButterKnife.bind(this, viewGroup);
		initData();
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart: " + getClass().getSimpleName());
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume: " + getClass().getSimpleName());
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.v(TAG, "onPause: " + getClass().getSimpleName());
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop: " + getClass().getSimpleName());
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView: " + getClass().getSimpleName());
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy: " + getClass().getSimpleName());
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.v(TAG, "onDetach: " + getClass().getSimpleName());
		super.onDetach();
	}
}
