#!/bin/bash

for file in *.svg; do
  inkscape -f $file -w 24 -h 24 -e ../exported/ldpi/${file%.*}.png
  inkscape -f $file -w 32 -h 32 -e ../exported/mdpi/${file%.*}.png
  inkscape -f $file -w 64 -h 64 -e ../exported/hdpi/${file%.*}.png
  inkscape -f $file -w 128 -h 128 -e ../exported/xhdpi/${file%.*}.png
  inkscape -f $file -w 15 -h 15 -e ../exported/ldpi/${file%.*}-small.png
  inkscape -f $file -w 20 -h 20 -e ../exported/mdpi/${file%.*}-small.png
  inkscape -f $file -w 40 -h 40 -e ../exported/hdpi/${file%.*}-small.png
  inkscape -f $file -w 80 -h 80 -e ../exported/xhdpi/${file%.*}-small.png
done;

