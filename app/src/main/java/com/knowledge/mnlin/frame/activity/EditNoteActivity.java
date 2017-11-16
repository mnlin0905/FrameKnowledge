package com.knowledge.mnlin.frame.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.EditNoteAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.bean.NoteConfigBean;
import com.knowledge.mnlin.frame.bean.NoteContentBean;
import com.knowledge.mnlin.frame.contract.EditNoteContract;
import com.knowledge.mnlin.frame.presenter.EditNotePresenter;
import com.knowledge.mnlin.frame.window.ActivityMenuDialog;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@Route(path = "/activity/EditNoteActivity")
public class EditNoteActivity extends BaseActivity<EditNotePresenter> implements EditNoteContract.View, TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.xrl_content)
    XRecyclerView mXrlContent;
    @BindView(R.id.rb_album)
    RadioButton mRbAlbum;
    @BindView(R.id.camera)
    RadioButton mCamera;
    @BindView(R.id.rb_share)
    RadioButton mRbShare;
    @BindView(R.id.rb_delete)
    RadioButton mRbDelete;
    @BindView(R.id.rg_util)
    RadioGroup mRgUtil;

    @Autowired(name = "bean", required = false)
    NoteConfigBean noteConfigBean;

    //列表显示
    private EditNoteAdapter adapter;

    //takePhoto框架
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    //记录是否修改了内容
    public boolean hasModified = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_note;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //初始化takephoto
        getTakePhoto();
        takePhoto.onCreate(savedInstanceState);

        //初始化便签内容
        if (noteConfigBean == null || noteConfigBean.getContent() == null || noteConfigBean.getContent().size() == 0) {
            hasModified = true;
            noteConfigBean = new NoteConfigBean(System.currentTimeMillis(), System.currentTimeMillis(), null, new ArrayList<>());
            noteConfigBean.getContent().add(new NoteContentBean(NoteContentBean.TYPE_STRING, null));
        }

        adapter = new EditNoteAdapter(this, noteConfigBean.getContent(), (parent, view, position, id) -> {
            // TODO: 2017/11/13 进入图片详情
        });

        mXrlContent.setAdapter(adapter);
        mXrlContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mXrlContent.setLoadingMoreEnabled(false);
        mXrlContent.setPullRefreshEnabled(false);

        //设定toolbar的paddingtop值不随输入法的弹出而改变
        toolbar.setFitsSystemWindows(false);
        toolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //将对象包含内容保存到数据库
        if (noteConfigBean.getContent().size() == 1
                && TextUtils.isEmpty(noteConfigBean.getContent().get(0).getPathOrData())) {
            showToast("便签无内容");
        } else {
            noteConfigBean.save();
            for (NoteContentBean bean : noteConfigBean.getContent()) {
                bean.setNoteConfigBean(noteConfigBean);
            }
            DataSupport.saveAll(noteConfigBean.getContent());
            showToast("内容已保存");
        }

        //清除标志,表示没有最新更改的内容
        hasModified = false;
        return true;
    }

    /**
     * 使用相机选择图片
     */
    @OnClick(R.id.camera)
    public void onMCameraClicked() {
        EditNoteActivityPermissionsDispatcher.needPermissionStorageWithPermissionCheck(this);
    }

    /**
     * 分享功能
     */
    @OnClick(R.id.rb_share)
    public void onMRbShareClicked() {

    }

    /**
     * 删除该内容
     */
    @OnClick(R.id.rb_delete)
    public void onMRbDeleteClicked() {
        new ActivityMenuDialog(this, new String[]{"确认"}, (dialog, position) -> {
            if (noteConfigBean.isSaved()) {
                for (NoteContentBean noteContentBean : noteConfigBean.getContent()) {
                    if (noteContentBean.isSaved()) {
                        noteContentBean.delete();
                    }
                }
                noteConfigBean.delete();
            }
            finish();
            return false;
        }).show();
    }

    /**
     * 从相册选择内容
     */
    @OnClick(R.id.rb_album)
    public void onViewClicked() {
        takePhoto.onPickFromGallery();
    }

    /**
     * 获取TakePhoto实例
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        CompressConfig config = new CompressConfig
                .Builder()
                //.enableReserveRaw(false)//设置不保存原图
                //.setMaxPixel(1024 * 100)//最大像素值
                .create();
        // 启用图片压缩(显示压缩进度框)
        takePhoto.onEnableCompress(config, true);

        TakePhotoOptions takePhotoOptions = new TakePhotoOptions
                .Builder()
                .setWithOwnGallery(false)//使用takePhoto自带相册
                .setCorrectImage(true) //纠正图片旋转角度
                .create();
        takePhoto.setTakePhotoOptions(takePhotoOptions);
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        takePhoto.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePhoto.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
        EditNoteActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void takeSuccess(TResult result) {
        //获取uri
        String filePath = result.getImage().getOriginalPath();

        Logger.d("=====" + result.getImage().getOriginalPath());

        //如果当前没有焦点获取的位置,或者焦点位置为图片,则默认图片新添加到最后的位置
        View view = getCurrentFocus();
        Integer position = noteConfigBean.getContent().size() - 1;
        if (view instanceof EditText) position = (Integer) view.getTag();

        hasModified = true;
        noteConfigBean.getContent().add(position + 1, new NoteContentBean(NoteContentBean.TYPE_PICTURE, filePath));
        noteConfigBean.getContent().add(position + 2, new NoteContentBean(NoteContentBean.TYPE_STRING, null));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        showToast("图片选择失败:" + msg);
    }

    @Override
    public void takeCancel() {

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPermissionStorage() {
        File parent = new File(Environment.getExternalStorageDirectory() + getString(R.string.note_directory));
        if (!parent.exists())
            if (!parent.mkdirs()) {
                showToast("无法创建文件来存储图片");
                return;
            }
        File file = new File(Environment.getExternalStorageDirectory() + getString(R.string.note_directory) + System.currentTimeMillis() + ".jpg");
        takePhoto.onPickFromCapture(Uri.fromFile(file));
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onShowRationaleStorage(final PermissionRequest request) {
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onNeverAskAgainStorage() {
        showToast("无法访问外部存储路径,请前往权限中心自行开启");
    }

    @Override
    public void onBackPressed() {
        if (hasModified) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("是否保存修改?")
                    .setContentText("确保修改的内容已经保存!")
                    .setCancelText("不保存")
                    .setConfirmText("保存")
                    .showCancelButton(true)
                    .setConfirmClickListener(sDialog -> {
                        onOptionsItemSelected(null);
                        sDialog.dismissWithAnimation();
                    })
                    .setCancelClickListener(sDialog -> {
                        sDialog.cancel();
                        finish();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}