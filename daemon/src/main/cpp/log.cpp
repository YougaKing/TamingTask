//
// Created by Administrator on 2017/9/25 0025.
//

#include "log.h"
#include <android/log.h>


void log_d(const char *tag, const char *text, ...) {
    __android_log_print(ANDROID_LOG_DEBUG, tag, text);
}

void log_i(const char *tag, const char *text, ...) {
    __android_log_print(ANDROID_LOG_INFO, tag, text);
}

void log_e(const char *tag, const char *text, ...) {
    __android_log_print(ANDROID_LOG_ERROR, tag, text);
}