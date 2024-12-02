package org.example;

import io.appium.java_client.AppiumDriver;
//import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class MobileBaseTest {
    protected AppiumDriver driver;
    private String platform;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        platform = System.getProperty("platform", "Android"); // Define la plataforma por propiedad del sistema
        DesiredCapabilities caps = new DesiredCapabilities();

        if ("Android".equalsIgnoreCase(platform)) {
            caps.setCapability("platformName", "Android");
            caps.setCapability("deviceName", "AF2SVB3828022994");
            caps.setCapability("appPackage", "com.google.android.youtube");
            caps.setCapability("appPackage", "com.google.android.youtube");
            caps.setCapability("appActivity","com.google.android.youtube.app.honeycomb.Shell$HomeActivity");
            //caps.setCapability("app", "/path/to/app.apk");
            caps.setCapability("automationName", "UiAutomator2");
            driver = new AndroidDriver(new URL("http://localhost:4723"), caps);
        } else if ("iOS".equalsIgnoreCase(platform)) {
            caps.setCapability("platformName", "iOS");
            caps.setCapability("deviceName", "iPhone Simulator");
            caps.setCapability("app", "/path/to/app.app");
            caps.setCapability("automationName", "XCUITest");
            driver = new IOSDriver(new URL("http://localhost:4723"), caps);
        } else {
            throw new IllegalArgumentException("Plataforma no soportada: " + platform);
        }
    }

    @Test
    public void testLogin() throws InterruptedException {
        // Esperamos a que el campo de nombre de usuario esté visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement closeButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.android.permissioncontroller:id/permission_deny_button")));
            closeButton.click();
            System.out.println("Pop-up cerrado exitosamente.");
        } catch (Exception e) {
            System.out.println("El pop-up no apareció, continuando con la ejecución normal.");
        }
        WebElement buscar= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.ImageView[@content-desc=\"Buscar\"]")));
        buscar.click();

        WebElement buscador = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.EditText[@resource-id=\"com.google.android.youtube:id/search_edit_text\"]")));
        buscador.sendKeys("Ben 10 intro");
        WebElement lupa = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//android.widget.ImageView[@resource-id='com.google.android.youtube:id/search_type_icon'])[1]")
        ));
        lupa.click();
        //buscador.sendKeys(Keys.ENTER);

        Thread.sleep(3000);

        /*WebElement passwordField = driver.findElement(By.id("com.example.app:id/password"));
        passwordField.sendKeys("password");

        WebElement loginButton = driver.findElement(By.id("com.example.app:id/login"));
        loginButton.click();

        // Verificación
        WebElement homePage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.app:id/home")));
        assert homePage.isDisplayed();*/
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}