#!/bin/bash

set -eu

project_dir="$(dirname "$(realpath "$0")")"/../..

# Make sure mkdocs plugin is already installed.
# pip3 install mkdocs-macros-plugin

pushd "$project_dir" >/dev/null

./gradlew dokkaGfm

cp docs/mkdocs.yml build/dokka
cp ./*.md build/dokka/my-app
cp docs/*.md build/dokka/my-app
cp -R docs/images build/dokka/my-app
mv build/dokka/my-app/{README.md,Overview.md}
sed -i "" 's/"docs\/images/"..\/images/g' build/dokka/my-app/Overview.md

pushd build/dokka >/dev/null
mkdocs build
popd >/dev/null

echo "Copying API doc"
rm -rf docs/apidoc
cp -R build/dokka/docs docs/apidoc

echo "Committing changes"
git add docs/apidoc
git commit -m "doc: site update" -- docs/apidoc || echo "Nothing to commit"

popd >/dev/null
