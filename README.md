WebsiteMonitoring
=================
WebsiteMonitoring is a set of monitoring tools which measure website performance.

PhantomPing
-----------
PhantomPing is a website monitoring tool. It browses websites by using Selenium and PhantomJS. For each website, the tool publishes two metrics `PageLoadTime` and `ResponseTime` by using [BeeInstant Java SDK](https://github.com/beeinstant-dev/beeinstant-java-sdk).

* Download PhantomJS from here [http://phantomjs.org/download.html](http://phantomjs.org/download.html).
* Config websites you want to monitor in `config.yaml`. This config file is a map from a website name to its url.
* Run the tool `nohup java -Dwebsites.config=config.yaml -Dphantomjs.driver=<path-to-your-downloaded-phantomjs> -Dbeeinstant.host=<beeinstant-agent-or-backend-ip> -DpublicKey=<your-public-key> -DsecretKey=<your-secret-key> -jar ./monitorwebsites.jar >>monitorwebsites.log 2>&1 &`

*Happy Monitoring!*
