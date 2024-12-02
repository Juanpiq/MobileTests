package org.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class MobileBaseTest {
    protected AppiumDriver<MobileElement> driver;
    private String platform;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        platform = System.getProperty("platform", "Android"); // Define la plataforma por propiedad del sistema
        DesiredCapabilities caps = new DesiredCapabilities();

        if ("Android".equalsIgnoreCase(platform)) {
            caps.setCapability("platformName", "Android");
            caps.setCapability("deviceName", "Pixel_4_Emulator");
            caps.setCapability("app", "/path/to/app.apk");
            caps.setCapability("automationName", "UiAutomator2");
            driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
        } else if ("iOS".equalsIgnoreCase(platform)) {
            caps.setCapability("platformName", "iOS");
            caps.setCapability("deviceName", "iPhone Simulator");
            caps.setCapability("app", "/path/to/app.app");
            caps.setCapability("automationName", "XCUITest");
            driver = new IOSDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
        } else {
            throw new IllegalArgumentException("Plataforma no soportada: " + platform);
        }
    }

    @Test
    public void testLogin() {
        // Esperamos a que el campo de nombre de usuario esté visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.app:id/username")));
        usernameField.sendKeys("testuser");

        WebElement passwordField = driver.findElement(By.id("com.example.app:id/password"));
        passwordField.sendKeys("password");

        WebElement loginButton = driver.findElement(By.id("com.example.app:id/login"));
        loginButton.click();

        // Verificación
        WebElement homePage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.app:id/home")));
        assert homePage.isDisplayed();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}