//
// Created by Administrator on 2017/9/25 0025.
//

#ifndef TAMINGTASK_COMMON_H
#define TAMINGTASK_COMMON_H

#include <sys/types.h>

char *str_stitching(const char *str1, const char *str2);

int get_version();

void open_browser(char *url);

int find_pid_by_name(char *pid_name, int *pid_list);

char *get_name_by_pid(pid_t pid);

void select_sleep(long sec, long msec);

#endif //TAMINGTASK_COMMON_H
