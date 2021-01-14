package com.kuloud.android.pipe;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 通道传输的数据切片
 */
public final class Flow implements Parcelable {
    public static final Creator<Flow> CREATOR = new Creator<Flow>() {
        @Override
        public Flow createFromParcel(Parcel source) {
            return new Flow(source);
        }

        @Override
        public Flow[] newArray(int size) {
            return new Flow[size];
        }
    };
    /**
     * 切片数
     */
    private int partNum;
    /**
     * 当前切片索引
     */
    private int partIndex;
    /**
     * 当前切片数据
     */
    private byte[] data;

    public Flow() {
    }

    public Flow(byte[] data) {
        this(1, 0, data);
    }

    public Flow(int partNum, int partIndex, byte[] data) {
        this.partNum = partNum;
        this.partIndex = partIndex;
        this.data = data;
    }

    private Flow(Parcel in) {
        this.partNum = in.readInt();
        this.partIndex = in.readInt();
        this.data = in.createByteArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.partNum);
        dest.writeInt(this.partIndex);
        dest.writeByteArray(this.data);
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public int getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(int partIndex) {
        this.partIndex = partIndex;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}

