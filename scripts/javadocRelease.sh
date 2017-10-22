#!/usr/bin/env bash
set -v

# The 2 step generation is because Binder references Core's documentation and if it's not available
# when generating javadoc it fails

if [ "$TRAVIS_REPO_SLUG" == "mibac138/ArgParser" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_TAG" != "" ]; then

  home2=$(pwd)
  echo -e "Generating Core Javadoc\n"

  ./gradlew :argparser-core:dokka

  echo -e "Generated Core Javadoc\n"
  echo -e "Publishing Core Javadoc\n"

  cp -R "build/javadoc/" $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/mibac138/ArgParser gh-pages > /dev/null


  cd gh-pages
  mkdir -p "./docs/$TRAVIS_TAG" && cp -Rf $HOME/javadoc-latest/. "$_"
  mkdir -p "./docs/stable" && cp -Rf $HOME/javadoc-latest/. "$_"


  git add -f .
  git commit -m ":bookmark: 1/2 Auto pushed tag $TRAVIS_TAG (commit $TRAVIS_COMMIT)"
  git push -fq origin HEAD:gh-pages > /dev/null
  echo -e "Published Core Javadoc to gh-pages.\n"

#-----------------
  cd ${home2}
  echo -e "Generating Javadoc\n"

  ./gradlew dokka -x :argparser-core:dokka

  echo -e "Generated Javadoc\n"
  echo -e "Publishing Javadoc\n"

  cp -R "build/javadoc/" $HOME/javadoc-latest

  cd gh-pages
  mkdir -p "./docs/$TRAVIS_TAG" && cp -Rf $HOME/javadoc-latest/. "$_"
  mkdir -p "./docs/stable" && cp -Rf $HOME/javadoc-latest/. "$_"


  git add -f .
  git commit -m ":bookmark: 2/2 Auto pushed tag $TRAVIS_TAG (commit $TRAVIS_COMMIT)"
  git push -fq origin HEAD:gh-pages > /dev/null
  echo -e "Published Javadoc to gh-pages.\n"
fi
