#!/bin/bash
export -p spring_profiles_active=bath
sudo dtoverlay w1-gpio gpiopin=4 pullup=0
sudo dtoverlay w1-gpio gpiopin=17 pullup=0
java -Xmx160m -jar /var/bath/bathtub-2.0.jar
echo "Failed to run with bath profile and real database, trying H2"

export -p spring_profiles_active=default
java -Xmx160m -jar /var/bath/bathtub-2.0.jar

echo "Will shutdown in 60 seconds due to failure"
sleep 60
reboot