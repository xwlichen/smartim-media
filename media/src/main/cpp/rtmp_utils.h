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
    unsigned char* rtmp_url;
     int start_time;
    int getSampleRateIndex(int sampleRate);
    RTMP *rtmp;

    RtmpUtils();
    ~RtmpUtils();

    void init(unsigned char* url);

    void addX264Data(x264_nal_t *nal, int nal_num);

    void addX264Header(unsigned char *sps, int sps_len, unsigned char *pps, int pps_len);

    void addX264Body(uint8_t *buf, int len);
};

#endif //SMARTIM_MEDIA_RTMP_UTILS_H
