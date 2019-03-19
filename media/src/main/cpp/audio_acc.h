/**
 * @date : 2019/3/19 下午2:59
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#ifndef SMARTIM_MEDIA_AUDIO_ENCODER_H
#define SMARTIM_MEDIA_AUDIO_ENCODER_H


#include "log.h"

extern "C" {
#include <fdk-aac/aacenc_lib.h>
}

class Audio_ACC{
private:
    int sampleRate;
    int channels;
    int bitRate;
    HANDLE_AACENCODER handle;

public:
    Audio_ACC(int channels, int sampleRate, int bitRate);
    ~Audio_ACC();
    int init();

    int encodeAudio(unsigned char* inBytes, int length, unsigned char* outBytes, int outlength);
    int encodeWAVAudioFile();
    int encodePCMAudioFile();
    bool close();

};

#endif //SMARTIM_MEDIA_AUDIO_ENCODER_H
