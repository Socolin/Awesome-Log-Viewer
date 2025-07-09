set -xe
find . -name 'Structured*.kt' -delete
find . -name 'Waterfall*.kt' -delete
find . -name 'CheckLicense.kt' -delete
for file in $(grep StartRemoveOSS -rl pluginCore/)
do
    sed -i '/StartRemoveOSS/,/EndRemoveOSS/d' "$file"
done
for file in $(grep StartRemoveOSS -rl processorSimpleConsole/)
do
    sed -i '/StartRemoveOSS/,/EndRemoveOSS/d' "$file"
done
rm sync_oss.sh
