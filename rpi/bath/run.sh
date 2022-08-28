#!/bin/bash
sudo dtoverlay w1-gpio gpiopin=4 pullup=0
sudo dtoverlay w1-gpio gpiopin=17 pullup=0
/home/pi/.sdkman/candidates/java/17.0.1-librca/bin/java -Xmx160m -jar -Daws.secretAccessKey="" -Daws.accessKeyId="" -Daws.region="eu-north-1" -Dspring.profiles.active=internet-access /var/bath/bathtub-2.0.jar

echo "Failed to run with bath profile and real database, trying H2"

/home/pi/.sdkman/candidates/java/17.0.1-librca/bin/java -Xmx160m -jar -Dspring.profiles.active=no-internet /var/bath/bathtub-2.0.jar

echo "Will shutdown in 60 seconds due to failure"
sleep 60
reboot
