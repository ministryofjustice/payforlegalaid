package uk.gov.laa.pfla.configuration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public class PlaywrightManager {

    private static Playwright playwright;
    private static Browser browser;

    public static Playwright getPlaywright() {
        if (playwright == null) {
            System.out.println("Creating Playwright instance");
            playwright = Playwright.create();
        }
        return playwright;
    }

    public static Browser getBrowser() {
        if (browser == null){
            System.out.println("Creating a Browser instance");
            browser = playwright.chromium().launch();
        }
        return browser;
    }

    public static void closePlaywright(){
        if (browser != null){
            System.out.println("Cleaning up Browser instance");
            browser.close();
        }
        if (playwright != null){
            System.out.println("Cleaning up Playwright instance");
            playwright.close();
        }
    }

}
