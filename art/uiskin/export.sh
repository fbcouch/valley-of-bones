#!/bin/bash

for file in *.svg; do
  inkscape -f $file -d 90 -e ./drawable/${file%.*}.png
done;
