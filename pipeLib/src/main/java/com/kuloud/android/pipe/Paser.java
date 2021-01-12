package com.kuloud.android.pipe;

import androidx.annotation.NonNull;

import com.kuloud.android.pipe.Flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Paser {
    /**
     * AIDL 通信数据大小限制是1m，不易传送太大文件，按指定大小进行拆包，保证稳定性
     */
    private static final int PART_SIZE = 100 * 1024;
    /**
     * 限制一个文件拆包数量
     */
    private static final int PART_MAX_LENGTH = 500;
    private final List<Flow> messages = new ArrayList<>();

    /**
     * 将数据按PART_SIZE拆成信息列表
     *
     * @param data 数据文件
     * @return 拆分成的消息数组
     */
    static List<Flow> split(@NonNull byte[] data) {
        int partNum = ((data.length % PART_SIZE) == 0)
                ? (data.length / PART_SIZE)
                : (data.length / PART_SIZE) + 1;
        List<Flow> msgs = new ArrayList<>(partNum);
        for (int i = 0; i < partNum; i++) {
            Flow msg = new Flow(partNum, i,
                    Arrays.copyOfRange(data
                            , i * PART_SIZE
                            , Math.min(((i + 1) * PART_SIZE), data.length)
                    )
            );
            msgs.add(msg);
        }
        return msgs;
    }

    /**
     * 数据完整解析（串行），则返回数据，否则返回空
     *
     * @param msg 序列化传递的信息片段
     * @return 接收到完整数据以后，返回整段数据，否则返回空数组。
     */
    byte[] parse(@NonNull Flow msg) {
        messages.add(msg);
        if (msg.getPartNum() == msg.getPartIndex() + 1) {
            // 最后一帧数据抵达，合并数据返回
            int totalSize = 0;
            for (Flow message : messages) {
                totalSize += message.getData().length;
            }
            byte[] result = new byte[totalSize];
            int index = 0;
            for (Flow message : messages) {
                // 拼装数据
                System.arraycopy(message.getData(), 0, result, index, message.getData().length);
                index += message.getData().length;
            }

            // 重置数据
            messages.clear();
            return result;
        }

        if (messages.size() > PART_MAX_LENGTH) {
            messages.clear();
        }

        return new byte[0];
    }

}

