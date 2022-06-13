#!/bin/bash

# Streaming download and extract
# curl -sSL https://jdk.java.net/20 | grep -m1 -Eioh "https:.*osx-x64_bin.tar.gz" | xargs curl | tar xvz -

set -eu

jdk_version=${1:-20}
case "$OSTYPE" in
darwin*)
  os=macos
  ;;
msys*)
  os=windows
  ;;
*)
  os=linux
  ;;
esac

case "$(uname -m)" in
amd64 | x86_64)
  arch=x64
  ;;
aarch64 | arm64)
  arch=aarch64
  ;;
*)
  echo "Unsupported arch: $(uname -m)"
  exit 1
  ;;
esac
echo "Using OS: $os-$arch"

download_url=$(curl -sSL "https://jdk.java.net/$jdk_version" | grep -m1 -Eioh "https:.*($os-$arch).*.(tar.gz|zip)")
openjdk_file="${download_url##*/}"

echo "Openjdk-$jdk_version file: $openjdk_file"
echo "Downloading openjdk: $download_url..."
# echo $download_url | xargs curl -O
curl -O "$download_url"
cat "$openjdk_file" | tar xvz

jdk_dir=$(tar -tzf "$openjdk_file" | head -3 | tail -1 | cut -f2 -d"/")
echo "$jdk_version openjdk dir: $jdk_dir"
