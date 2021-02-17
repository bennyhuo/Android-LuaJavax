APP_ABI := all

APP_LDFLAGS += \
    -Wl,--exclude-libs=ALL


APP_OPTIM   := release

APP_MODULES := liblua luajava

NDK_TOOLCHAIN_VERSION := 4.9
APP_PLATFORM 	:= android-14