package org.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.example.Funciones.FuncionesGlobales;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;

public class MobileBaseTest {
    protected AppiumDriver driver;
    private String platform;
    private String currentTestName;

    @BeforeEach
    public void setUp(TestInfo testInfo) throws MalformedURLException {
        currentTestName = testInfo.getDisplayName(); // Obtiene el nombre del test actual
        platform = System.getProperty("platform", "Android"); // Define la plataforma por propiedad del sistema
        DesiredCapabilities caps = new DesiredCapabilities();

        if ("Android".equalsIgnoreCase(platform)) {
            caps.setCapability("platformName", "Android");
            caps.setCapability("deviceName", "AF2SVB3828022994");
            caps.setCapability("appPackage", "com.google.android.youtube");
            caps.setCapability("appActivity", "com.google.android.youtube.app.honeycomb.Shell$HomeActivity");
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

        startRecording(currentTestName);
    }

    @Test
    public void testLogin() throws InterruptedException {
        FuncionesGlobales f = new FuncionesGlobales(driver);
        try {
            f.ClickMixto("id", "com.android.permissioncontroller:id/permission_deny_button", 1);
        } catch (Exception e) {
            System.out.println("El pop-up no apareció, continuando con la ejecución normal.");
        }
        f.ClickMixto("xpath", "//android.widget.ImageView[@content-desc=\"Buscar\"]", 1);
        f.TextoMixto("xpath", "//android.widget.EditText[@resource-id=\"com.google.android.youtube:id/search_edit_text\"]", "Ben 10 intro", 1);
        f.ClickMixto("xpath", "(//android.widget.ImageView[@resource-id='com.google.android.youtube:id/search_type_icon'])[1]", 1);
        f.Tiempo(2.5);
    }

    @AfterEach
    public void tearDown() {
        stopRecording(currentTestName);
        if (driver != null) {
            driver.quit();
        }
    }

    private void startRecording(String testName) {
        if ("Android".equalsIgnoreCase(platform)) {
            try {
                String sanitizedTestName = sanitizeFileName(testName);
                //String videoFile = "recording_" + sanitizedTestName + ".mp4";
                //String command = String.format("adb shell screenrecord /sdcard/%s", videoFile);
                String command = "scrcpy --record ./videos/recording_" + sanitizedTestName + ".mp4";
                // ---- Inicia la grabación con scrcpy
                Process process = Runtime.getRuntime().exec(command);
                Runtime.getRuntime().exec(command);
                String videoFile = "recording_" + sanitizedTestName + ".mp4";
                System.out.println("Grabación iniciada para Android: " + videoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if ("iOS".equalsIgnoreCase(platform)) {
            try {
                ((IOSDriver) driver).startRecordingScreen();
                System.out.println("Grabación iniciada para iOS.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording(String testName) {
        if ("Android".equalsIgnoreCase(platform)) {
            try {
                // Detiene la grabación, finalizando el proceso de scrcpy
                Runtime.getRuntime().exec("taskkill /F /IM scrcpy.exe");
                System.out.println("Grabación detenida para Android.");

                /*String sanitizedTestName = sanitizeFileName(testName);
                //String videoFile = "recording_" + sanitizedTestName + ".mp4";
                //Runtime.getRuntime().exec("adb shell killall -2 screenrecord");

                File localFile = new File("./videos/" + videoFile);
                localFile.getParentFile().mkdirs();
                Runtime.getRuntime().exec("adb pull /sdcard/" + videoFile + " ./videos/");
                Runtime.getRuntime().exec("adb shell rm /sdcard/" + videoFile);*/
                //System.out.println("Grabación guardada para Android: " + localFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("iOS".equalsIgnoreCase(platform)) {
            try {
                String sanitizedTestName = sanitizeFileName(testName);
                String videoBase64 = ((IOSDriver) driver).stopRecordingScreen();
                byte[] videoBytes = java.util.Base64.getDecoder().decode(videoBase64);
                String filePath = "./videos/recording_" + sanitizedTestName + ".mp4";
                Files.write(Paths.get(filePath), videoBytes);
                System.out.println("Grabación guardada para iOS: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
}
