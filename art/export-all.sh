#!/bin/bash

cd icons;
bash export.sh;
cd ../map;
bash export.sh;
cd ../ui;
bash export.sh;
cd ../units;
bash export.sh;
cd ..;
cp map-thumbs/*.png exported/ldpi/;
cp map-thumbs/*.png exported/mdpi/;
cp map-thumbs/*.png exported/hdpi/;
cp map-thumbs/*.png exported/xhdpi/;
bash export-assets.sh;

