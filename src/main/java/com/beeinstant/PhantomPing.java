package com.beeinstant;

import com.beeinstant.metrics.MetricsLogger;
import com.beeinstant.metrics.MetricsManager;
import com.beeinstant.metrics.Unit;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Measure website performance using Selenium and PhantomJS
 *
 * Original article: https://www.agilegroup.co.jp/en/technote/java-phantomjs.html
 * More timing performance: http://engineering.tripadvisor.com/html5-navigation-timing/
 */
public class PhantomPing {

    private static boolean running = true;

    public static void main(String[] args) throws InterruptedException, IOException {

        MetricsManager.init("PhantomPing");
        Runtime.getRuntime().addShutdownHook(new Thread(MetricsManager::shutdown));

        final Map<String, String> websites = loadWebsitesConfig();

        if (!websites.isEmpty()) {

            final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(websites.size());
            List<Callable<Object>> tasks = new ArrayList<>();

            websites.forEach((target, url) -> {
                tasks.add(() -> {
                    while (running) {
                        new PhantomPing().checkSitePerformance(target, url);
                        Thread.sleep(1000);
                    }
                    return null;
                });
            });

            executor.invokeAll(tasks);
        }
    }

    private PhantomPing() {
    }

    /**
     * Initialize PhantomJSDriver.
     */
    private PhantomJSDriver initDriver() {
        // set Capabilities
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setJavascriptEnabled(true);

        System.setProperty("phantomjs.binary.path", System.getProperty("phantomjs.driver"));
        PhantomJSDriver driver = new PhantomJSDriver(capabilities);
        driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS);

        return driver;
    }

    /**
     * Check Performance.
     */
    private void checkSitePerformance(String target, String url) {

        PhantomJSDriver driver = null;

        try {
            driver = initDriver();

            driver.get(url);
            waitForLoad(driver);

            // Get values of Navigation Timing
            long startTime = (Long) driver.executeScript(
                    "return window.performance.timing.navigationStart");
            long loadEndTime = (Long) driver.executeScript(
                    "return window.performance.timing.loadEventEnd");
            long responseEndTime = (Long) driver.executeScript(
                    "return window.performance.timing.responseEnd");

            MetricsLogger metricsLogger = MetricsManager.getMetricsLogger("target=" + target);
            metricsLogger.record("ResponseTime", responseEndTime - startTime, Unit.MILLI_SECOND);
            metricsLogger.record("PageLoadTime", loadEndTime - startTime, Unit.MILLI_SECOND);

            System.out.println("Target: " + target + " / url: " + url);
            System.out.format("Response Time : %d\n", responseEndTime - startTime);
            System.out.format("PageLoad Time : %d\n", loadEndTime - startTime);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Wait for page load.
     */
    private void waitForLoad(PhantomJSDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(pageLoadCondition);
    }

    /**
     * Load websites from configuration to start pinging
     *
     * Configuration file is a yaml file with a map from target name to url.
     * Target names will be used as part of metric name. Urls will be used by Phantomjs to load the websites.
     */
    private static Map<String, String> loadWebsitesConfig() throws IOException {
        Yaml yaml = new Yaml();
        Map<String, String> websites = new TreeMap<>();

        try (InputStream is = new FileInputStream(new File(System.getProperty("websites.config")))) {
            Map<String, String> websitesConfig = (Map<String, String>) yaml.load(is);
            if (websitesConfig != null) {
                websites.putAll(websitesConfig);
            }
        }

        return websites;
    }
}
