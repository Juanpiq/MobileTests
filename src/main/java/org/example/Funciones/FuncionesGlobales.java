package org.example.Funciones;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

public class FuncionesGlobales {
    private AppiumDriver driver;

    public FuncionesGlobales(AppiumDriver driver) {
        this.driver = driver;
    }

    // Método para esperar un tiempo determinado
    public void Tiempo(double tie) throws InterruptedException {
        Thread.sleep((int) (tie * 1000));
    }

    // Método para navegar a una URL en la app
    public void Navegar(String appUrl, double tiempo) throws InterruptedException {
        driver.get(appUrl); // Navegamos a la app usando su URL
        Tiempo(tiempo);
    }

    // Método para esperar un elemento visible por XPath
    public WebElement SEX(String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
    }

    // Método para esperar un elemento visible por ID
    public WebElement SEI(String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(selector)));
    }

    // Método para introducir texto en un campo (mixto: Xpath o ID)
    public void TextoMixto(String tipo, String selector, String texto, double tiempo) throws InterruptedException {
        try {
            WebElement val = tipo.equals("xpath") ? SEX(selector) : SEI(selector);
            val.clear();
            val.sendKeys(texto);
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + selector);
        }
    }

    // Método para hacer clic en un elemento (mixto: Xpath o ID)
    public void ClickMixto(String tipo, String selector, double tiempo) throws InterruptedException {
        try {
            WebElement val = tipo.equals("xpath") ? SEX(selector) : SEI(selector);
            val.click();
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + selector);
        }
    }

    // Método para seleccionar un valor de un dropdown (por texto visible) usando XPath
    public void SelectXpathText(String xpath, String texto, double tiempo) throws InterruptedException {
        try {
            WebElement val = SEX(xpath);
            Select select = new Select(val);
            select.selectByVisibleText(texto);
            System.out.println("El campo seleccionado es " + texto);
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + xpath);
        }
    }

    // Método para seleccionar un valor de un dropdown (por texto, índice o valor) usando XPath
    public void SelectXpathType(String xpath, String tipo, String dato, double tiempo) throws InterruptedException {
        try {
            WebElement val = SEX(xpath);
            Select select = new Select(val);
            switch (tipo) {
                case "text":
                    select.selectByVisibleText(dato);
                    break;
                case "index":
                    select.selectByIndex(Integer.parseInt(dato));
                    break;
                case "value":
                    select.selectByValue(dato);
                    break;
            }
            System.out.println("El campo seleccionado es " + dato);
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + xpath);
        }
    }

    // Método para cargar un archivo usando XPath
    public void UploadXpath(String xpath, String ruta, double tiempo) throws InterruptedException {
        try {
            WebElement val = SEX(xpath);
            val.sendKeys(ruta);
            System.out.println("Se carga la imagen " + ruta);
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + xpath);
        }
    }

    // Método para verificar si un elemento existe y dar feedback
    public String Existe(String tipo, String selector, double tiempo) throws InterruptedException {
        try {
            WebElement val = tipo.equals("xpath") ? SEX(selector) : SEI(selector);
            System.out.println("Elemento encontrado: " + selector);
            Tiempo(tiempo);
            return "Existe";
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + selector);
            return "No Existe";
        }
    }

    // Método para verificar la existencia y hacer clic en un elemento
    public void CheckXpath(String xpath, double tiempo) throws InterruptedException {
        try {
            WebElement val = SEX(xpath);
            val.click();
            System.out.println("Click en el elemento " + xpath);
            Tiempo(tiempo);
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se encontró el Elemento " + xpath);
        }
    }

    // Método de salida
    public void Salida() {
        System.out.println("Se termina la prueba exitosamente");
    }
}