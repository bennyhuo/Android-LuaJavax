//
// Created by leon on 16/3/10.
//

#ifndef LUA_LOG_H
#define LUA_LOG_H

#include <android/log.h>

#define  LOG_TAG "luajava"

#define ANDROID_LOG(level, format, ...) \
do {                           \
  __android_log_print(level, LOG_TAG,"(%s:%d) %s: " format "\n", strrchr(__FILE__, '/') + 1, __LINE__, __FUNCTION__ , ##__VA_ARGS__); \
} while(0)

#define LOG_INFO(format, ...) ANDROID_LOG(ANDROID_LOG_INFO, format, ##__VA_ARGS__)
#define LOG_ERROR(format, ...) ANDROID_LOG(ANDROID_LOG_ERROR, format, ##__VA_ARGS__)
#define LOG_WARN(format, ...) ANDROID_LOG(ANDROID_LOG_WARN, format, ##__VA_ARGS__)

#define PRINTLNF(format, ...) ANDROID_LOG(ANDROID_LOG_DEBUG, format, ##__VA_ARGS__)

#define PRINT_CHAR(char_value) PRINTLNF(#char_value": %c", char_value)
#define PRINT_WCHAR(char_value) PRINTLNF(#char_value": %lc", char_value)
#define PRINT_INT(int_value) PRINTLNF(#int_value": %d", int_value)
#define PRINT_LONG(long_value) PRINTLNF(#long_value": %ld", long_value)
#define PRINT_LLONG(long_value) PRINTLNF(#long_value": %lld", long_value)
#define PRINT_HEX(int_value) PRINTLNF(#int_value": %#x", int_value)
#define PRINT_BOOL(bool_value) PRINTLNF(#bool_value": %s", bool_value ? "true" : "false")
#define PRINT_DOUBLE(double_value) PRINTLNF(#double_value": %g", double_value)
#define PRINT_STRING(string_value) PRINTLNF(#string_value": %s", string_value)

#define PRINT_ARRAY(format, array, length) \
{ int array_index; \
for (array_index = 0; array_index < length; ++array_index) { \
  printf(format, array[array_index]); \
};\
printf("\n"); }

#define PRINT_INT_ARRAY_LN(array, length) \
{ int i; \
for (i = 0; i < length; ++i) { \
  PRINTLNF(#array"[%d]: %d", i, array[i]); \
}}

#define PRINT_INT_ARRAY(array, length) PRINT_ARRAY("%d, ", array, length)
#define PRINT_CHAR_ARRAY(array, length) PRINT_ARRAY("%c, ", array, length)
#define PRINT_DOUBLE_ARRAY(array, length) PRINT_ARRAY("%g, ", array, length)

#define PRINT_IF_ERROR(format, ...) \
if (errno != 0) { \
  fprintf(stderr, format, ##__VA_ARGS__); \
  fprintf(stderr, ": %s\n", strerror(errno)); \
}

#endif //LUA_LOG_H
