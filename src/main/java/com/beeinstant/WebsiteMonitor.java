package com.beeinstant;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebsiteMonitor {

    public static void main(String[] args) {

        // launch the browser
        WebDriver driver = new FirefoxDriver();

        // visit a page
        driver.get("http://stackoverflow.com");

        // get the  page load time
        Long loadtime = (Long)((JavascriptExecutor)driver).executeScript(
                "return performance.timing.loadEventEnd - performance.timing.navigationStart;");

        System.out.println(loadtime);

        driver.quit();
    }

}
