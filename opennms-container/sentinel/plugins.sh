#!/bin/bash

apt-get update
apt-get install -y python3-pip wget curl jq
pip3 install --upgrade cloudsmith-cli 

mkdir ~/test
cd ~/test || exit
urls=$(cloudsmith list packages --query="sentinel-alec-plugin tag:latest" opennms/common -F json  | jq -r '.data[].cdn_url')
for url in $urls; do 
 wget "$url"
done
dpkg-deb -R *-alec-plugin_*_all.deb ./
find . -name '*.kar' -exec mv {} /usr/share/opennms/deploy \;


urls=$(cloudsmith list packages --query="sentinel-plugin-cloud tag:latest" opennms/common -F json  | jq -r '.data[].cdn_url')
for url in $urls; do
    wget "$url"
done
dpkg-deb -R *-plugin-cloud_*_all.deb ./
find . -name '*.kar' -exec mv {} /usr/share/opennms/deploy \;

cd ..
rm -r test

apt-get remove -y python3-pip wget curl jq 
apt-get clean
rm -rf /var/cache/apt /var/lib/apt/lists/* /tmp/security.sources.list 
rm -rf ~/.cache/pip
