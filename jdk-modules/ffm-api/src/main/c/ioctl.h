#include <stdio.h>
#include <string.h>    // strerror
#include <errno.h>     // errno
#include <fcntl.h>     // open(), O_EVTONLY, O_NONBLOCK
#include <unistd.h>    // close()
#include <sys/ioctl.h> // ioctl()
