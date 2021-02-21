APP_ABI := all

APP_LDFLAGS += -Wl,--exclude-libs=ALL

APP_OPTIM   := release

APP_MODULES := liblua luajava

APP_PLATFORM 	:= android-16