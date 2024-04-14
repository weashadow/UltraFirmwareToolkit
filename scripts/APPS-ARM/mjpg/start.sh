#!/bin/sh
export LD_LIBRARY_PATH=/media/mmcblk0p2/mjpg
./mjpg_streamer -i input_uvc.so -o output_http.so
