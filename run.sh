#!/bin/sh
echo "********************************************************"
echo "Starting company-profile-servicee "
echo "********************************************************"
java -jar -Dspring.profiles.active=dev company-profile-service.jar
