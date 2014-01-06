#!/bin/bash

for file in *.svg; do
  inkscape -f $file -d 67.5 -e ../exported/ldpi/${file%.*}.png
  inkscape -f $file -d 90 -e ../exported/mdpi/${file%.*}.png
  inkscape -f $file -d 180 -e ../exported/hdpi/${file%.*}.png
  inkscape -f $file -d 360 -e ../exported/xhdpi/${file%.*}.png
done;
