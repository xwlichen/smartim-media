/**
 * @date : 2019/2/27 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */


#include <jni.h>
#include <string>
#include "frame_x264.h"
#include "img_utils.h"
#include "rtmp_utils.h"

using namespace imgt;


extern "C" {
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
//                                                         jobject instance,
//                                                         jint width,
//                                                         jint height,
//                                                         jint bitRate,
//                                                         jint frameRate);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env,
//                                                        jobject instance,
//                                                        jint sampleRate,
//                                                        jint numChannels);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env,
//                                                        jobject instance,
//                                                        jstring url_);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env,
//                                                        jobject instance,
//                                                        jbyteArray data_);
}

//------------------------------------------------------------------------------------------------------------------------


Frame_X264 *frame_x264;
RtmpUtils *rtmpUtils;


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint width,
                                                          jint height,
                                                          jint bitRate,
                                                          jint frameRate) {

    frame_x264 = new Frame_X264();
    frame_x264->setInWidth(width);
    frame_x264->setInHeight(height);
    frame_x264->setBitrate(bitRate);
    frame_x264->setFps(frameRate);
    frame_x264->open_x264_Encode();


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env, jobject instance,
                                                          jint sampleRate, jint numChannels) {


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env, jobject instance,
                                                   jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    rtmpUtils = new RtmpUtils();
    rtmpUtils->init((unsigned char *) url);

    // TODO

    env->ReleaseStringUTFChars(url_, url);
}

char *dst_i420_data;
int fts = 0;
char *dst_h264_data;

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);


    nav21ToI420((char *) data, dst_i420_data, frame_x264->getInWidth(), frame_x264->getInHeight());
    fts++;
    int nal_num = frame_x264->encode_frame(dst_i420_data, fts);
    rtmpUtils->add_x264_data(frame_x264->get_x264_nal_t(), nal_num);


    env->ReleaseByteArrayElements(data_, data, 0);
}


