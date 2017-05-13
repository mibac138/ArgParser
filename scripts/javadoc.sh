#!/usr/bin/env bash
set -v

if [ "$TRAVIS_REPO_SLUG" == "mibac138/ArgParser" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Generating javadoc\n"

  ./gradlew dokka
  
  echo -e "Generated javadoc\n"
  echo -e "Publishing javadoc\n"

  for entry in build/javadoc/core/*
  do
    echo "$entry"
  done

  cp -R "build/javadoc/" $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/mibac138/ArgParser gh-pages > /dev/null

  for entry in $HOME/javadoc-latest/core/*
  do
    echo "$entry"
  done

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  
  for entry in ./javadoc/core/*
  do
    echo "$entry"
  done
  
  git add -f .
  git commit -m ":sparkles: Auto pushed commit $TRAVIS_COMMIT"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"

fi
