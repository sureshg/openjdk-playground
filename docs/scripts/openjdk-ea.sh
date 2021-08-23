#!/bin/bash

# Streaming download and extract
# curl -sSL https://jdk.java.net/loom | grep -m1 -Eioh "https:.*osx-x64_bin.tar.gz" | xargs curl | tar xvz -

set -eu

jdk_version=${1:-loom}
case "$OSTYPE" in
darwin*)
  os=osx
  ;;
msys*)
  os=windows
  ;;
*)
  os=linux
  ;;
esac

arch=$(uname -m)
echo "Using OS: $os-$arch"

download_url=$(curl -sSL "https://jdk.java.net/$jdk_version" | grep -m1 -Eioh "https:.*($os-$arch).*.(tar.gz|zip)")
openjdk_file="${download_url##*/}"

echo "$jdk_version openjdk file: $openjdk_file"
echo "Downloading openjdk: $download_url..."
# echo $download_url | xargs curl -O
curl -O "$download_url"
cat "$openjdk_file" | tar xvz

jdk_dir=$(tar -tzf "$openjdk_file" | head -3 | tail -1 | cut -f2 -d"/")
echo "$jdk_version openjdk dir: $jdk_dir"
