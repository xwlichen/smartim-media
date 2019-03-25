/**
 * @date : 2019/2/27 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */


#include <jni.h>
#include <string>
#include "log.h"
#include "frame_x264.h"
#include "audio_acc.h"
#include "rtmp_utils.h"
#include "img_utils.h"


extern "C" {
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint width,
                                                          jint height,
                                                          jint bitRate,
                                                          jint frameRate);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint numChannels,
                                                          jint sampleRate,
                                                          jint bitRate);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env,
                                                   jobject instance,
                                                   jstring url_);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env,
                                                        jobject instance,
                                                        jbyteArray data_);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushAudioData(JNIEnv *env,
                                                        jobject instance,
                                                        jbyteArray data_);
}

//------------------------------------------------------------------------------------------------------------------------



Frame_X264 *frame_x264;
Audio_ACC *audio_acc;
RtmpUtils *rtmp_tils;
ImgUtils *img_utils;

int video_fts = 0;
int audio_fts=0;
int audio_buffer_size;
int audio_valid_size;
bool first_spec=false;

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
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint numChannels,
                                                          jint sampleRate,
                                                          jint bitRate) {
    audio_acc=new Audio_ACC(numChannels,sampleRate,bitRate);
    audio_buffer_size=audio_acc->init();

}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env, jobject instance,
                                                   jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    rtmp_tils = new RtmpUtils();
    rtmp_tils->init((unsigned char *) url);
    rtmp_tils->init_thread();

    img_utils = new ImgUtils();

    // TODO

    env->ReleaseStringUTFChars(url_, url);
}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {

    jbyte *data = env->GetByteArrayElements(data_, NULL);
    char *dst_i420_data = (char *) malloc(
            sizeof(char) * frame_x264->getInWidth() * frame_x264->getInHeight() * 3 / 2);
    img_utils->nav21ToI420((char *) data, dst_i420_data, frame_x264->getInWidth(),
                          frame_x264->getInHeight());


    video_fts++;
//    frame_x264->set_x264_nal_t(NULL);
    int nal_num = frame_x264->encode_frame(dst_i420_data, video_fts);

    rtmp_tils->add_x264_data(frame_x264->get_x264_nal_t(), nal_num);


    free(dst_i420_data);
    dst_i420_data = NULL;
    env->ReleaseByteArrayElements(data_, data, 0);

}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushAudioData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    unsigned char *dst_acc_data = (unsigned char *)malloc(1024);
    audio_valid_size=audio_acc->encodeAudio((unsigned char*)data,audio_buffer_size,dst_acc_data,1024);

    audio_fts++;
    if (!first_spec){
        rtmp_tils->add_acc_header(44100,2,0);
        first_spec= true;
    }
    rtmp_tils->add_acc_body(dst_acc_data,audio_valid_size,audio_fts);

    free(dst_acc_data);
    dst_acc_data=NULL;

    env->ReleaseByteArrayElements(data_, data, 0);
}