/**
 * @date : 2019/3/14 下午6:40
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#include "rtmp_utils.h"
#include "log.h"

#define RTMP_HEAD_SIZE   (sizeof(RTMPPacket) + RTMP_MAX_HEADER_SIZE)


RtmpUtils::RtmpUtils() {
}

RtmpUtils::~RtmpUtils() {
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

void RtmpUtils::addX264Data(x264_nal_t *nal, int nal_num) {
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
        if (nal[i].i_type == NAL_SPS) {//sps
            sps_len = nal[i].i_payload - 4;
            memcpy(sps, nal[i].p_payload + 4, (size_t) sps_len);
        } else if (nal[i].i_type == NAL_PPS) {//pps
            pps_len = nal[i].i_payload - 4;
            memcpy(pps, nal[i].p_payload + 4, (size_t) pps_len);
            addX264Header(sps, pps, sps_len, pps_len);
        } else {
            addX264Body(nal[i].p_payload, nal[i].i_payload);
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
RtmpUtils::addX264Header(unsigned char *sps, int sps_len, unsigned char *pps, int pps_len) {
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

    //send rtmp
    if (RTMP_IsConnected(rtmp)) {
        RTMP_SendPacket(rtmp, packet, TRUE);
        //LOGD("send packet sendSpsAndPps");
    }
    free(packet);
}

