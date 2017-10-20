package com.knowledge.mnlin.frame.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.QRUtilContract;
import com.knowledge.mnlin.frame.presenter.QRUtilPresenter;
import com.knowledge.mnlin.frame.util.ActivityUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class QRUtilActivity extends BaseActivity<QRUtilPresenter> implements QRUtilContract.View, TakePhoto.TakeResultListener, InvokeListener {
    //从图库中扫描二维码
    private static int QR_SCAN_REQUEST = 10000;
    //请求打开相机权限
    private static int OPEN_CAMERA_REQUEST=10001;
    //请求存储文件
    private static int OPEN_STORAGE_REQUEST=10002;

    //takePhoto框架
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @BindView(R.id.et_qr)
    EditText mEtQr;
    @BindView(R.id.tv_create)
    TextView mTvCreate;
    @BindView(R.id.iv_qr)
    ImageView mIvQr;
    @BindView(R.id.tv_from_picture)
    TextView mTvFromPicture;
    @BindView(R.id.tv_from_photo)
    TextView mTvFromPhoto;

    //二维码图像
    private Bitmap bitmap;

    @Override
    protected void initData(Bundle savedInstanceState) {
        //初始化takephoto
        getTakePhoto();
        takePhoto.onCreate(savedInstanceState);

    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_qr_util;
    }

    /**
     * 生成二维码图片
     */
    @OnClick(R.id.tv_create)
    public void createQRImage() {
        String str_qr = mEtQr.getText().toString();
        if (!TextUtils.isEmpty(str_qr)) {
            bitmap = CodeUtils.createImage(str_qr, 400, 400, null);
            mIvQr.setImageBitmap(bitmap);
        } else {
            showToast("请输入二维码内容");
        }
    }

    /**
     * 点击图片进行保存
     */
    @OnLongClick(R.id.iv_qr)
    public boolean saveQRImage() {
        if(!showRequestPermissionDialog("需要打开存储权限才能保存图片!",Manifest.permission.READ_EXTERNAL_STORAGE,OPEN_STORAGE_REQUEST)){
            return true;
        }

        if (bitmap != null) {
            try {
                String filePath = Environment.getExternalStorageDirectory() + "/qr_image/" + System.currentTimeMillis() + ".jpg";
                File file = new File(filePath);
                if (!new File(file.getParent()).exists()) {
                    new File(file.getParent()).mkdirs();
                }
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
                showSnackbar("二维码已保存,文件路径为:\n" + filePath, "确定", null);
            } catch (IOException e) {
                showToast("无存储权限,无法保存二维码");
            }
        } else {
            showToast("二维码不存在");
        }
        return true;
    }

    /**
     * 从图库中选择图片并进行解析
     */
    @OnClick(R.id.tv_from_picture)
    public void parseFromPicture() {
        CompressConfig config = new CompressConfig
                .Builder()
                .enableReserveRaw(false)//设置不保存原图
                .setMaxPixel(1024 * 100)//最大像素值
                .create();
        takePhoto.onEnableCompress(config, true);//显示压缩进度对话框

        TakePhotoOptions takePhotoOptions = new TakePhotoOptions
                .Builder()
                .setWithOwnGallery(false)//使用takePhoto自带相册
                .setCorrectImage(true) //纠正图片旋转角度
                .create();
        takePhoto.setTakePhotoOptions(takePhotoOptions);

        File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        if (false) {
            //拍照来选择图片
            takePhoto.onPickFromCapture(Uri.fromFile(file));
        } else {
            //使用相册
            takePhoto.onPickFromGallery();
        }
    }

    /**
     * 拍照并进行解析
     */
    @OnClick(R.id.tv_from_photo)
    public void parseFromPhoto() {
        if(showRequestPermissionDialog("需要打开相机权限才能扫描二维码!",Manifest.permission.CAMERA,OPEN_CAMERA_REQUEST)){
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, QR_SCAN_REQUEST);
        }
    }

    /**
     * 返回boolean,为true表示已经有了相应权限
     */
    private boolean showRequestPermissionDialog(String title,String permission,int requestCode){
        if (!ActivityUtil.checkPermission(this,permission )) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showSnackbar(title,"去设置", view -> {
                    Intent intent1 = new Intent();
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent1.setData(Uri.fromParts("package", getPackageName(), null));
                    ComponentName componentName = intent1.resolveActivity(getPackageManager());
                    if (componentName != null) {
                        startActivity(intent1);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePhoto.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QR_SCAN_REQUEST) {
                //扫描二维码成功,处理扫描结果
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                            String qr = bundle.getString(CodeUtils.RESULT_STRING);
                            showSnackbar(qr, "复制", view -> {
                                ActivityUtil.saveMsgToClipboard(QRUtilActivity.this, qr);
                                showToast("内容已复制");

                                //再将获取到的图片转换为二维码进行显示
                                mEtQr.setText(qr);
                                mTvCreate.performClick();
                            });
                        } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                            showToast("无法解析二维码内容");
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        takePhoto.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
        if(requestCode==OPEN_CAMERA_REQUEST){
            if(grantResults.length==1&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                showToast("相机权限已打开");
            }
        }
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
        String filePath = result.getImage().getCompressPath();
        Glide.with(this).asBitmap().load(filePath).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                QRUtilActivity.this.bitmap = resource;
                mIvQr.setImageBitmap(QRUtilActivity.this.bitmap);

                CodeUtils.analyzeBitmap(filePath, new CodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        showSnackbar("解析成功,二维码内容为:\n" + result, "复制", view -> {
                            ActivityUtil.saveMsgToClipboard(QRUtilActivity.this, result);
                            showToast("已复制到粘贴板");
                        });
                    }

                    @Override
                    public void onAnalyzeFailed() {
                        showToast("无法解析图片");
                    }
                });
            }
        });
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }
}