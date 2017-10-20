package com.knowledge.mnlin.frame.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 功能----基础model
 * <p>
 * Created by MNLIN on 2017/9/25.
 */

public class BaseBean implements Parcelable {

    public String data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };

    @Override
    public String toString() {
        return "BaseBean:"+this.hashCode()+"数据:"+data;
    }
}
