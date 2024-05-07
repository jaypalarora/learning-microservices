#! /bin/bash

echo 'Cleaning and building projects'

echo 'AppDiscoveryService'
cd AppDiscoveryService
./mvnw clean install -DskipTests

echo 'ApiGateway'
cd ../ApiGateway
./mvnw clean install -DskipTests

echo 'AppConfigServer'
cd ../AppConfigServer
./mvnw clean install -DskipTests

echo 'AppAlbumService'
cd ../AppAlbumService
./mvnw clean install -DskipTests

echo 'AppUserService'
cd ../AppUserService
./mvnw clean install -DskipTests

cd ..
echo 'Build Successful'



