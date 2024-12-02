package org.example.Funciones;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URL;
import java.time.Duration;

public class FuncionesGlobales {
    private AppiumDriver<MobileElement> driver;

    public FuncionesGlobales(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    public void Tiempo(double tie) throws InterruptedException {
        Thread.sleep((int) (tie * 1000));
    }

    public void Navegar(String appUrl, double tiempo) throws InterruptedException {
        driver.get(appUrl); // Navegamos a la app usando su URL
        Tiempo(tiempo);
    }

    public MobileElement SEX(String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
    }

    public void TextoMixto(String tipo, String selector, String texto, double tiempo) throws InterruptedException {
        try {
            MobileElement val = SEX(selector);
            val.clear();
            val.sendKeys(texto);
            Tiempo(tiempo);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void ClickMixto(String tipo, String selector, double tiempo) throws InterruptedException {
        try {
            MobileElement val = SEX(selector);
            val.click();
            Tiempo(tiempo);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void CheckXpath(String xpath, double tiempo) throws InterruptedException {
        try {
            MobileElement val = SEX(xpath);
            val.click();
            Tiempo(tiempo);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}