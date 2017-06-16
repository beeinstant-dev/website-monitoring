MonitorWebsites
===============
MonitorWebsites is a monitoring tool which measures website performance. It uses Selenium with a headless Firefox to browse websites and publish performance metrics. Metrics are published to BeeInstant using [BeeInstant Java SDK](https://github.com/beeinstant-dev/beeinstant-java-sdk).

Setup headless Firefox
----------------------
We are going to setup headless Firefox in Ubuntu. Selenium will use FirefoxDriver to test websites.

**Install the official Firefox Beta PPA**
```
sudo apt-add-repository ppa:mozillateam/firefox-next
sudo apt-get update
```

**Install firefox and xvfb (the X windows virtual framebuffer)**
```
sudo apt-get install firefox xvfb
```

**Config Xvfb**
```
Xvfb :10 -ac &
export DISPLAY=:10
```

**Test that the setup works**
We should be able to start firefox now. As this is a headless firefox, we will not expect to see anything.
```
firefox
```
Ctrl-C to kill it.

The full original tutorial can be found [here](https://medium.com/@griggheo/running-selenium-webdriver-tests-using-firefox-headless-mode-on-ubuntu-d32500bb6af2).

Run MonitorWebsites
-------------------
First, checkout [BeeInstant Java SDK](https://github.com/beeinstant-dev/beeinstant-java-sdk) if you don't have it yet. BeeInstant Java SDK allows us to publish performance time-series metrics in real-time.
```
git clone git@github.com:beeinstant-dev/beeinstant-java-sdk.git
cd beeinstant-java-sdk
mvn clean install
```

**Build the big fat jar**
```
mvn package
```
The jar file will be in `target/monitorwebsites.jar` folder.

**Download Gecko WebDriver**

From https://github.com/mozilla/geckodriver/releases

**Run MonitorWebsites**
```
java -Dwebdriver.gecko.driver=./geckodriver -jar target/monitorwebsites.jar
```

*Happy Monitoring!*
