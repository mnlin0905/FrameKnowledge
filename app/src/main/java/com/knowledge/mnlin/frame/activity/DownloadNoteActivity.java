package com.knowledge.mnlin.frame.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.ChapterAdapter;
import com.knowledge.mnlin.frame.arouter.ARouterConst;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.base.BaseRecyclerViewHolder;
import com.knowledge.mnlin.frame.bean.ChapterBean;
import com.knowledge.mnlin.frame.bean.NovelBean;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@Route(path = ARouterConst.Activity_DownloadNoteActivity)
public class DownloadNoteActivity extends BaseActivity<BasePresenter<DownloadNoteActivity>> implements XRecyclerView.LoadingListener, BaseRecyclerViewHolder.OnViewClickListener {
    /**
     * 列表信息
     */
    @BindView(R.id.xrv_record)
    XRecyclerView mXrvRecord;
    ArrayList<ChapterBean> dataBeans;
    NovelBean singleNovel;
    ChapterAdapter chapterAdapter;

    @BindView(R.id.tv_info)
    TextView mTvInfo;

    /**
     * 记录顺序
     */
    volatile int count = 0;
    long startTime;
    private Observable<ChapterBean> observable;
    private Observer<ChapterBean> observer;

    /**
     * 使用dagger注入自身
     */
    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    /**
     * @return 获取布局文件
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_download_note;
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData(Bundle savedInstanceState) {
        dataBeans = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(this, dataBeans, this);
        mXrvRecord.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mXrvRecord.setAdapter(chapterAdapter);
        mXrvRecord.setEmptyView(findViewById(R.id.empty_view));
        mXrvRecord.setLoadingMoreEnabled(false);
        mXrvRecord.setPullRefreshEnabled(false);
        mXrvRecord.setLoadingListener(this);

        //设置textview可滑动
        mTvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        loadInThread();
    }

    /**
     * 异步加载数据
     */
    private void loadInThread() {
        String url = "43_43821/";
        DataSupport.where(String.format("url =\"%s\"", url))
                .findAsync(NovelBean.class, true)
                .listen(new FindMultiCallback() {
                    @Override
                    public <T> void onFinish(List<T> t) {
                        if (t == null || t.size() == 0 || ((NovelBean) t.get(0)).getChapters() == null) {
                            DownloadNoteActivity.this.showToast("没有数据,即将下载");
                            DownloadNoteActivityPermissionsDispatcher.downloadNovelWithPermissionCheck(DownloadNoteActivity.this, url);
                            return;
                        }
                        dataBeans.clear();
                        singleNovel = (NovelBean) t.get(0);
                        dataBeans.addAll(singleNovel.getChapters());
                        chapterAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    /**
     * @param v        被点击的view
     * @param position 所在的position
     */
    @Override
    public void onViewClick(View v, int position) {
        //小说内容
        mTvInfo.setText(dataBeans.get(position).getContent());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void downloadNovel(String url) {
        NovelBean novelBean = new NovelBean();
        novelBean.setUrl(url);
        List<ChapterBean> chapterBeans = new LinkedList<>();

        observer=new Observer<ChapterBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 提示:开始进行下载操作
                startTime = System.currentTimeMillis();
                mTvInfo.append(String.format(Locale.CHINA, "开始下载小说%s\n,地址:%s\n", new Date().toString(), novelBean.getUrl()));
            }

            @Override
            public void onNext(ChapterBean bean) {
                // 打印文字/给用户提示: %s 加载完毕
                mTvInfo.append(String.format(Locale.CHINA, "%s 下载完成\n", bean.getTitle()));
            }

            @Override
            public void onError(Throwable e) {
                //  章节加载出错
                mTvInfo.append(String.format(Locale.CHINA, "错误:\n"));
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                // 全部章节下载完毕
                mTvInfo.append(String.format(Locale.CHINA, "%s 下载完成 %s,共耗时%d秒,准备存储到数据库\n", novelBean.getTitle(), new Date().toString(), (System.currentTimeMillis() - startTime) / 1000));
                novelBean.saveAsync().listen(success -> {
                    if (success) {
                        DataSupport.saveAllAsync(novelBean.getChapters()).listen(new SaveCallback() {
                            @Override
                            public void onFinish(boolean success) {
                                if (success) {
                                    mTvInfo.append(String.format(Locale.CHINA, "已存储到数据库中...准备再次加载数据\n"));
                                    loadInThread();
                                } else {
                                    mTvInfo.append(String.format(Locale.CHINA, "章节表存储到数据库中失败...\n"));
                                }
                            }
                        });
                    } else {
                        mTvInfo.append(String.format(Locale.CHINA, "小说表存储到数据库中失败...\n"));
                    }
                });
            }
        };

        Observable.just(url)
                .subscribeOn(Schedulers.single())
                .flatMap(str_url -> {
                    //从本页获取所有的章节url
                    String result = mPresenter.httpInterface
                            .getUrlData(str_url)
                            .blockingFirst()
                            .replaceAll("\\s", "");
                    Matcher matcher = Pattern
                            .compile("<metaproperty=\"og:title\"content=\"(.+?)\"/>")
                            .matcher(result = result.substring(result.indexOf("正文</dt>")));
                    if (matcher.find()) {
                        novelBean.setTitle(matcher.group(1));
                    }
                    matcher = Pattern.compile("<dd><ahref=\"/(.+?)\">(.+?)</a></dd>").matcher(result);
                    while (matcher.find()) {
                        chapterBeans.add(new ChapterBean(matcher.group(1), ++count, matcher.group(2)).setNovelBean(novelBean));
                    }
                    novelBean.setChapters(chapterBeans);
                    novelBean.setChapterLength(chapterBeans.size());
                    return Observable.fromIterable(chapterBeans);
                })
                .observeOn(Schedulers.newThread())
                .filter((ChapterBean bean) -> {
                    //过滤可能为其他广告的url
                    return !TextUtils.isEmpty(bean.getUrl());
                })
                .map((ChapterBean bean) -> {
                    // 从每个章节url获取整个网页的内容
                    String result = mPresenter.httpInterface
                            .getUrlData(bean.getUrl())
                            .blockingFirst()
                            .replaceAll("\\s", "");
                    Matcher matcher = Pattern.compile("<script>readx\\(\\);</script>(.+)<script>chaptererror\\(\\);</script>").matcher(result);
                    if (matcher.find()) {
                        bean.setContent(matcher.group(1).replaceAll("<br/>", "\n"));
                        bean.setCharLength(bean.getContent().length());
                    }
                    return bean;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DownloadNoteActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        observer.onError(new RuntimeException("界面退出,停止加载"));
    }
}
