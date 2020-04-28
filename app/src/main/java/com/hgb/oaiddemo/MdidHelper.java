package com.hgb.oaiddemo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdk;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;

/**
 * @author mt
 */
public class MdidHelper implements IIdentifierListener {

    private MsaIdListener listener;

    MdidHelper(MsaIdListener callback) {
        listener = callback;
    }

    /**
     * 可以看错误码
     * 暂未处理
     *
     * @param cxt context
     */
    void getDeviceIds(Context cxt) {
        int result = callFromReflect(cxt);
        switch (result) {
            case ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT:
                break;
            case ErrorCode.INIT_ERROR_LOAD_CONFIGFILE:
                break;
            case ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT:
                break;
            case ErrorCode.INIT_ERROR_RESULT_DELAY:
                break;
            case ErrorCode.INIT_HELPER_CALL_ERROR:
                break;
            default:
                break;
        }
    }

    /**
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     *
     * @param cxt context
     * @return result
     */
    private int callFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

    /**
     * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
     * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
     * A和B不能存在直接和间接依赖关系，否则也会报错
     *
     * @param cxt context
     * @return result
     */
    @Deprecated
    private int directCall(Context cxt) {
        MdidSdk sdk = new MdidSdk();
        return sdk.InitSdk(cxt, this);
    }

    @Override
    public void OnSupport(boolean isSupport, IdSupplier supplier) {
        if (supplier == null) {
            return;
        }

        if (listener != null) {
            listener.onMsaIdSupport(supplier.getOAID(), supplier.getVAID(), supplier.getAAID(), isSupport);
        }
    }

    public interface MsaIdListener {
        void onMsaIdSupport(@NonNull String oaid, String vaid, String aaid, boolean isSupport);
    }
}
