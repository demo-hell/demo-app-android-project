#!/bin/bash

git log -1 --pretty=%B > release_notes.txt 

gradle clean assStoreHom

gradle app:crashlyticsUploadDistributionStoreHom
