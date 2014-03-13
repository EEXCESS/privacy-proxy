#!/bin/bash

for x in privacy profiles users; 
do
  echo -n "Deleting $x index: "
  curl -XDELETE http://localhost:9200/$x/
  echo
done

