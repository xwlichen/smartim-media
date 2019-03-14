/**
 * @date : 2019/3/14 下午6:40
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#ifndef SMARTIM_MEDIA_RTMP_UTILS_H
#define SMARTIM_MEDIA_RTMP_UTILS_H


#include <stdint.h>
#include <string.h>
#include <pthread.h>
#include <malloc.h>

extern "C" {
#include <libavutil/pixfmt.h>
#include <libavcodec/avcodec.h>
#include <x264.h>
#include <librtmp/rtmp.h>
}


#define RTMP_MAX_HEADER_SIZE 18

class RtmpUtils {


public :
    unsigned char *rtmp_url;
    int start_time;

    int getSampleRateIndex(int sampleRate);

    RTMP *rtmp;

    RtmpUtils();

    ~RtmpUtils();

    void init(unsigned char *url);

    void add_x264_data(x264_nal_t *nal, int nal_num);

    void add_264_header(unsigned char *sps, int sps_len, unsigned char *pps, int pps_len);

    void add_x264_body(uint8_t *buf, int len);

    void add_packet(RTMPPacket *rtmpPacket);

    void init_thread();

    void *push_thread(void * args);
};

#endif //SMARTIM_MEDIA_RTMP_UTILS_H
