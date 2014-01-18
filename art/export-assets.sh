cd exported;
for file in *dpi; do
cd $file;
java -cp ../../../libs/gdx.jar:../../../libs/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 . ../../../assets "assets-$file";
cd ..;
done;
cd ..;
