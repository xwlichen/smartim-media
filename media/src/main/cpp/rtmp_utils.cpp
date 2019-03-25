/**
 * @date : 2019/3/14 下午6:40
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
//
#include "rtmp_utils.h"
#include "log.h"
#include <jni.h>
#include <stdint.h>
#include <malloc.h>
#include <stdio.h>
#include "thread_safe_queue.h"




extern "C" {
#include "queue.h"
}


#define RTMP_HEAD_SIZE   (sizeof(RTMPPacket) + RTMP_MAX_HEADER_SIZE)

pthread_mutex_t mutex;
pthread_cond_t cond;

//子线程回调给Java需要用到JavaVM
JavaVM *javaVM;
//调用类
jobject jobject_error;

int is_pushing = FALSE;
int start_time;
RTMP *rtmp;
threadsafe_queue<RTMPPacket *> frame_queue;

RtmpUtils::RtmpUtils() {
}

RtmpUtils::~RtmpUtils() {
}


//回调异常给java
//void throw_error_to_java(int error_code){
//    JNIEnv* env;
//    (*javaVM)->AttachCurrentThread(javaVM, &env, NULL);
//    jclass jclazz = (*env)->GetObjectClass(env, jobject_error);
//    jmethodID jmethod = (*env)->GetMethodID(env, jclazz, "errorFromNative", "(I)V");
//    (*env)->CallVoidMethod(env, jobject_error, jmethod, error_code);
//    (*javaVM)->DetachCurrentThread(javaVM);
//}

void on_error(int code) {
    if (code == EPIPE) {
        if (rtmp) {
            RTMP_Close(rtmp);
            if (!RTMP_IsConnected(rtmp)) {
                if (!RTMP_Connect(rtmp, NULL)) {
                    LOGE(JNI_DEBUG, "RTMP_Connect error");
                } else {
                    LOGE(JNI_DEBUG, "RTMP_Connect success");
                    //连接到rtmp流
                    if (!RTMP_ConnectStream(rtmp, 0)) {
                        LOGE(JNI_DEBUG, "RTMP_ConnectStream error");
                    } else {
                        LOGE(JNI_DEBUG, "RTMP_ConnectStream success");
                    }
                }
            }

        }
    }
}


/**
 * 初始化RTMP数据，与rtmp连接
 * @param url
 */
void RtmpUtils::init(unsigned char *url) {
    this->rtmp_url = url;
    rtmp = RTMP_Alloc();
    RTMP_Init(rtmp);

    rtmp->Link.timeout = 5;
    RTMP_SetupURL(rtmp, (char *) url);
    RTMP_EnableWrite(rtmp);

    //建立RTMP socket连接
    if (!RTMP_Connect(rtmp, NULL)) {
        LOGE(JNI_DEBUG, "RTMP_Connect error");
    } else {
        LOGE(JNI_DEBUG, "RTMP_Connect success");
    }
    //连接到rtmp流上
    if (!RTMP_ConnectStream(rtmp, 0)) {
        LOGE(JNI_DEBUG, "RTMP_ConnectStream error");
    } else {
        LOGE(JNI_DEBUG, "RTMP_ConnectStream success");
    }
    start_time = RTMP_GetTime();
    LOGE(JNI_DEBUG, "start_time = %d", start_time);
}

void RtmpUtils::add_x264_data(x264_nal_t *nal, int nal_num) {
    //使用RTMP推流
    //关键帧（I帧）加上SPS和PPS
    int sps_len = 0, pps_len = 0;
    unsigned char sps[100];
    unsigned char pps[100];
    memset(sps, 0, 100);  //sps 清零和初始化
    memset(pps, 0, 100);
    int i = 0;
    //0x00 0x00 0x01）  0x00 0x00 0x00 0x01   都是视频帧（NALU数据单元）之间的间隔标识
    for (; i < nal_num; ++i) {
//        LOGE(JNI_DEBUG,"nal[i].i_type=%d",nal[i].i_type);
        if (nal[i].i_type == NAL_SPS) {//sps
            sps_len = nal[i].i_payload - 4;
            memcpy(sps, nal[i].p_payload + 4, (size_t) sps_len);
        } else if (nal[i].i_type == NAL_PPS) {//pps
            pps_len = nal[i].i_payload - 4;
            memcpy(pps, nal[i].p_payload + 4, (size_t) pps_len);
            add_264_header(sps, sps_len, pps, pps_len);
        } else {
            add_x264_body(nal[i].p_payload, nal[i].i_payload);
        }
    }
}

/**
* 发送sps pps，即发送H264SequenceHead头
* @param sps
* @param sps_len
* @param pps
* @param pps_len
*/
void
RtmpUtils::add_264_header(unsigned char *sps, int sps_len, unsigned char *pps, int pps_len) {
    //LOGI("#######addSequenceH264Header#########pps_lem=%d, sps_len=%d", pps_len, sps_len);
    RTMPPacket *packet = (RTMPPacket *) malloc(RTMP_HEAD_SIZE + 1024);
    memset(packet, 0, RTMP_HEAD_SIZE + 1024);
    packet->m_body = (char *) packet + RTMP_HEAD_SIZE;

    unsigned char *body = (unsigned char *) packet->m_body;

    int i = 0;
    /*1:keyframe 7:AVC*/
    body[i++] = 0x17;
    /* AVC head */
    body[i++] = 0x00;

    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    /*AVCDecoderConfigurationRecord*/
    //configurationVersion版本号，1
    body[i++] = 0x01;
    //AVCProfileIndication sps[1]
    body[i++] = sps[1];
    //profile_compatibility sps[2]
    body[i++] = sps[2];
    //AVCLevelIndication sps[3]
    body[i++] = sps[3];
    //6bit的reserved为二进制位111111和2bitlengthSizeMinusOne一般为3，
    //二进制位11，合并起来为11111111，即为0xff
    body[i++] = 0xff;

    /*sps*/
    //3bit的reserved，二进制位111，5bit的numOfSequenceParameterSets，
    //sps个数，一般为1，及合起来二进制位11100001，即为0xe1
    body[i++] = 0xe1;
    //SequenceParametersSetNALUnits（sps_size + sps）的数组
    body[i++] = (sps_len >> 8) & 0xff;
    body[i++] = sps_len & 0xff;
    memcpy(&body[i], sps, sps_len);
    i += sps_len;

    /*pps*/
    //numOfPictureParameterSets一般为1，即为0x01
    body[i++] = 0x01;
    //SequenceParametersSetNALUnits（pps_size + pps）的数组
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = (pps_len) & 0xff;
    memcpy(&body[i], pps, pps_len);
    i += pps_len;

    //Message Type，RTMP_PACKET_TYPE_VIDEO：0x09
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    //Payload Length
    packet->m_nBodySize = i;
    //Time Stamp：4字节
    //记录了每一个tag相对于第一个tag（File Header）的相对时间。
    //以毫秒为单位。而File Header的time stamp永远为0。
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    //Channel ID，Audio和Vidio通道
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    add_packet(packet);

//    free(packet);

}

void RtmpUtils::add_x264_body(uint8_t *buf, int len) {
//    //去掉起始码(界定符)
//    if (buf[2] == 0x00) {
//        //00 00 00 01
//        buf += 4;
//        len -= 4;
//    } else if (buf[2] == 0x01) {
//        // 00 00 01
//        buf += 3;
//        len -= 3;
//    }
//    int body_size = len + 9;
//    RTMPPacket *packet = (RTMPPacket *) malloc(RTMP_HEAD_SIZE + 9 + len);
//    memset(packet, 0, RTMP_HEAD_SIZE);
//    packet->m_body = (char *) packet + RTMP_HEAD_SIZE;
//
//    unsigned char *body = (unsigned char *) packet->m_body;
//    //当NAL头信息中，type（5位）等于5，说明这是关键帧NAL单元
//    //buf[0] NAL Header与运算，获取type，根据type判断关键帧和普通帧
//    //00000101 & 00011111(0x1f) = 00000101
//    int type = buf[0] & 0x1f;
//    //Pframe  7:AVC
//    body[0] = 0x27;
//    //IDR I帧图像
//    //Iframe  7:AVC
//    if (type == NAL_SLICE_IDR) {
//        body[0] = 0x17;
//    }
//    //AVCPacketType = 1
//    /*nal unit,NALUs（AVCPacketType == 1)*/
//    body[1] = 0x01;
//    //composition time 0x000000 24bit
//    body[2] = 0x00;
//    body[3] = 0x00;
//    body[4] = 0x00;
//
//    //写入NALU信息，右移8位，一个字节的读取
//    body[5] = (len >> 24) & 0xff;
//    body[6] = (len >> 16) & 0xff;
//    body[7] = (len >> 8) & 0xff;
//    body[8] = (len) & 0xff;
//
//    /*copy data*/
//    memcpy(&body[9], buf, len);
//
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_nBodySize = body_size;
//    //当前packet的类型：Video
//    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
//    packet->m_nChannel = 0x04;
//    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = rtmp->m_stream_id;
//    //记录了每一个tag相对于第一个tag（File Header）的相对时间
//    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
//
//    add_packet(packet);

    if(buf[2] == 0x01){//00 00 01
        buf += 3;
        len -= 3;
    } else if (buf[3] == 0x01){//00 00 00 01
        buf += 4;
        len -= 4;
    }
    int body_size = len + 9;//
    RTMPPacket *packet = (RTMPPacket *)malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, body_size);
    RTMPPacket_Reset(packet);
    unsigned char *body = (unsigned char *) packet->m_body;
    int type = buf[0] & 0x1F;
    if(type == NAL_SLICE_IDR){//关键帧
        body[0] = 0x17;
    } else{
        body[0] = 0x27;
    }
    body[1] = 0x01;
    body[2] = 0x00;
    body[3] = 0x00;
    body[4] = 0x00;

    body[5] = (unsigned char) ((len >> 24) & 0xFF);
    body[6] = (unsigned char) ((len >> 16) & 0xFF);
    body[7] = (unsigned char) ((len >> 8) & 0xFF);
    body[8] = (unsigned char) (len & 0xFF);

    memcpy(&body[9], buf, (size_t) len);
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = (uint32_t) body_size;
    packet->m_nChannel = 0x04;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;

    add_packet(packet);
}


void RtmpUtils::add_packet(RTMPPacket *rtmpPacket) {
//    frame_queue.push(rtmpPacket);

    pthread_mutex_lock(&mutex);
    if(is_pushing){
        queue_append_last(rtmpPacket);
    }
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);


}

int RtmpUtils::getSampleRateIndex(int sampleRate) {
    int sampleRateArray[] = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000,
                             11025, 8000, 7350};
    int i = 0;
    for (; i < 13; ++i) {
        if (sampleRateArray[i] == sampleRate) {
            return i;
        }
    }
    return -1;
}


/**
 * 发送AAC Sequence HEAD 头数据
 * @param sampleRate
 * @param channel
 * @param timestamp
 */
void RtmpUtils::add_acc_header(int sampleRate, int channel, int timestamp) {
    int body_size = 2 + 2;
    RTMPPacket *packet = (RTMPPacket *) malloc(RTMP_HEAD_SIZE + 4);
    memset(packet, 0, RTMP_HEAD_SIZE);

    packet->m_body = (char *) packet + RTMP_HEAD_SIZE;
    unsigned char *body = (unsigned char *) packet->m_body;
    //头信息配置
    /*AF 00 + AAC RAW data*/
    body[0] = 0xAF;
    //AACPacketType:0表示AAC sequence header
    body[1] = 0x00;

    //5bit audioObjectType 编码结构类型，AAC-LC为2 二进制位00010
    //4bit samplingFrequencyIndex 音频采样索引值，44100对应值是4，二进制位0100
    //4bit channelConfiguration 音频输出声道，对应的值是2，二进制位0010
    //1bit frameLengthFlag 标志位用于表明IMDCT窗口长度 0 二进制位0
    //1bit dependsOnCoreCoder 标志位，表面是否依赖与corecoder 0 二进制位0
    //1bit extensionFlag 选择了AAC-LC,这里必须是0 二进制位0
    //上面都合成二进制0001001000010000
    uint16_t audioConfig = 0;
    //这里的2表示对应的是AAC-LC 由于是5个bit，左移11位，变为16bit，2个字节
    //与上一个1111100000000000(0xF800)，即只保留前5个bit
    audioConfig |= ((2 << 11) & 0xF800);

    int sampleRateIndex = getSampleRateIndex(sampleRate);
    if (-1 == sampleRateIndex) {
        free(packet);
        packet = NULL;
        LOGE(JNI_DEBUG, "addSequenceAacHeader: no support current sampleRate[%d]", sampleRate);
        return;
    }

    //sampleRateIndex为4，二进制位0000001000000000 & 0000011110000000(0x0780)（只保留5bit后4位）
    audioConfig |= ((sampleRateIndex << 7) & 0x0780);
    //sampleRateIndex为4，二进制位000000000000000 & 0000000001111000(0x78)（只保留5+4后4位）
    audioConfig |= ((channel << 3) & 0x78);
    //最后三个bit都为0保留最后三位111(0x07)
    audioConfig |= (0 & 0x07);
    //最后得到合成后的数据0001001000010000，然后分别取这两个字节

    body[2] = (audioConfig >> 8) & 0xFF;
    body[3] = (audioConfig & 0xFF);

    //参数设置
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet->m_nInfoField2 = rtmp->m_stream_id;


    add_packet(packet);

//    //send rtmp aac head
//    if (RTMP_IsConnected(rtmp)) {
//        RTMP_SendPacket(rtmp, packet, TRUE);
//        //LOGD("send packet sendAacSpec");
//    }
//    free(packet);
}

/**
 * 发送rtmp AAC data
 * @param buf
 * @param len
 * @param timeStamp
 */
void RtmpUtils::add_acc_body(unsigned char *buf, int len, long timeStamp) {
    int body_size = 2 + len;
    RTMPPacket *packet = (RTMPPacket *) malloc(RTMP_HEAD_SIZE + len + 2);
    memset(packet, 0, RTMP_HEAD_SIZE);

    packet->m_body = (char *) packet + RTMP_HEAD_SIZE;
    unsigned char *body = (unsigned char *) packet->m_body;

    //头信息配置
    /*AF 00 + AAC RAW data*/
    body[0] = 0xAF;
    //AACPacketType:1表示AAC raw
    body[1] = 0x01;
    /*spec_buf是AAC raw数据*/
    memcpy(&body[2], buf, len);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
    LOGE(JNI_DEBUG,"aac m_nTimeStamp = %d", packet->m_nTimeStamp);
    packet->m_nInfoField2 = rtmp->m_stream_id;

    add_packet(packet);

//    //send rtmp aac data
//    if (RTMP_IsConnected(rtmp)) {
//        RTMP_SendPacket(rtmp, packet, TRUE);
//        //LOGD("send packet sendAccBody");
//    }
//    free(packet);
}


void *push_thread(void *args) {

    if (!rtmp) {
        LOGE(JNI_DEBUG, "RTMP_Alloc fail...");
//        goto end;
    }
    if (!RTMP_IsConnected(rtmp)) {
        LOGE(JNI_DEBUG, "RTMP_Connect fail...");
        RTMP_Connect(rtmp, NULL);
//        throw_error_to_java(ERROR_RTMP_CONNECT);
//        goto end;
    }
    LOGI(JNI_DEBUG, "RTMP_Connect success...");
    if (!RTMP_ConnectStream(rtmp, 0)) {
        LOGE(JNI_DEBUG, "RTMP_ConnectStream fail...");
        goto end;
    }
    LOGI(JNI_DEBUG, "RTMP_ConnectStream success...");

    is_pushing = TRUE;
    //发送一个ACC HEADER
//    add_aac_header();
    //循环推流
    while (is_pushing) {
//        RTMPPacket *packet;
//        frame_queue.wait_and_pop(packet);

        pthread_mutex_lock(&mutex);
        pthread_cond_wait(&cond, &mutex);
        //从队头去一个RTMP包出来
        RTMPPacket *packet = (RTMPPacket *)queue_get_first();

        if (packet) {
//            LOGE(JNI_DEBUG, "RTMP_SendPacket m_nTimeStamp: %d", packet->m_nTimeStamp);

            //发送rtmp包，true代表rtmp内部有缓存
            int code;
            queue_delete_first();

            code = RTMP_SendPacketWithCode(rtmp, packet, TRUE);
            LOGE(JNI_DEBUG, "RTMP_SendPacket code: %d", code);
            RTMPPacket_Free(packet);



            on_error(code);

//                if (!ret) {
//                    LOGE(JNI_DEBUG, "RTMP_SendPacket fail...");
//                    RTMPPacket_Free(packet);
//                    goto end;
//                }
        }
        pthread_mutex_unlock(&mutex);

    }
    end:
    LOGI(JNI_DEBUG, "free all the thing about rtmp...");

//    RTMP_Close(rtmp);
//    free(rtmp);
    return 0;
}

void RtmpUtils::init_thread() {

    //创建队列
    create_queue();
    //初始化互斥锁和条件变量
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);
    pthread_t push_thread_id;
    //创建消费线程推流
    pthread_create(&push_thread_id, NULL, push_thread, NULL);

//    pthread_t push_thread_id;
//    //创建消费线程推流
//    pthread_create(&push_thread_id, NULL, push_thread, this);
}






