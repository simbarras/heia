#!/bin/bash
/usr/bin/x11vnc -forever -usepw -create &
cd javanco
export DISPLAY=:0
ant build
xvfb-run ant run_cockpit