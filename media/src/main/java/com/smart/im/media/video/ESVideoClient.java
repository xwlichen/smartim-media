package com.smart.im.media.video;

import com.smart.im.media.bean.ESConfig;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.enums.EncodeEnum;
import com.smart.im.media.utils.CameraUtil;

/**
 * @date : 2019/4/10 下午4:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class ESVideoClient {
    private PushConfig pushConfig;
    private ESVideoCore esVideoCore;

    private CameraUtil cameraUtil;



    public ESVideoClient(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
    }

    public boolean prepare() {
        if (pushConfig == null) {
            return false;
        }

        if (pushConfig.getEndcoderType() == EncodeEnum.HARD) {
            esVideoCore = new ESHardVideoCore(pushConfig);
        }

        if (!esVideoCore.prepare()) {
            return false;
        }

        return true;
    }
}
