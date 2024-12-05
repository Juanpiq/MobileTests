package org.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.example.Funciones.FuncionesGlobales;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

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
    private int t = 1;

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
            f.ClickMixto("id", "com.android.permissioncontroller:id/permission_deny_button", t);
        } catch (Exception e) {
            System.out.println("El pop-up no apareció, continuando con la ejecución normal.");
        }
        f.ClickMixto("xpath", "//android.widget.ImageView[@content-desc=\"Buscar\"]", t);
        f.TextoMixto("xpath", "//android.widget.EditText[@resource-id=\"com.google.android.youtube:id/search_edit_text\"]", "Ben 10 intro", t);
        f.ClickMixto("xpath", "(//android.widget.ImageView[@resource-id='com.google.android.youtube:id/search_type_icon'])[1]", t);
        String screens = "./" + sanitizeFileName(currentTestName) + "/screen1";
        takeScreenshot(driver, screens);
        f.Tiempo(2.5);
    }

    @AfterEach
    public void tearDown() throws IOException {
        stopRecording(currentTestName);
        if (driver != null) {
            driver.quit();
        }


    }

    private void startRecording(String testName) {
        if ("Android".equalsIgnoreCase(platform)) {
            try {
                // Sanitiza el nombre del archivo de video
                String sanitizedTestName = sanitizeFileName(testName);
                String videoFile = "recording_" + sanitizedTestName + ".mp4";

                // Verifica y crea el directorio si no existe
                File videoDirectory = new File("./videos/");
                if (!videoDirectory.exists()) {
                    if (videoDirectory.mkdirs()) {
                        System.out.println("Directorio creado: " + videoDirectory.getAbsolutePath());
                    } else {
                        System.err.println("No se pudo crear el directorio: " + videoDirectory.getAbsolutePath());
                    }
                }

                // Comando para iniciar la grabación
                String command = "scrcpy --record ./videos/" + videoFile;
                Runtime.getRuntime().exec(command, null, null);
                System.out.println("Grabación iniciada para Android: " + videoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if ("iOS".equalsIgnoreCase(platform)) {
            try {
                // Inicia la grabación de pantalla en iOS
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
                Runtime.getRuntime().exec("taskkill /IM scrcpy.exe");
                System.out.println("Grabación detenida para Android.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if ("iOS".equalsIgnoreCase(platform)) {
            try {
                // Sanitiza el nombre del archivo de video
                String sanitizedTestName = sanitizeFileName(testName);

                // Define la ruta del archivo de video
                String directoryPath = "./videos/";
                String filePath = directoryPath + "recording_" + sanitizedTestName + ".mp4";

                // Verifica y crea el directorio si no existe
                File videoDirectory = new File(directoryPath);
                if (!videoDirectory.exists()) {
                    if (videoDirectory.mkdirs()) {
                        System.out.println("Directorio creado: " + videoDirectory.getAbsolutePath());
                    } else {
                        System.err.println("No se pudo crear el directorio: " + videoDirectory.getAbsolutePath());
                    }
                }

                // Detiene la grabación y guarda el archivo
                String videoBase64 = ((IOSDriver) driver).stopRecordingScreen();
                byte[] videoBytes = java.util.Base64.getDecoder().decode(videoBase64);
                Files.write(Paths.get(filePath), videoBytes);
                System.out.println("Grabación guardada para iOS: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "");
    }

    private static void takeScreenshot(AppiumDriver driver, String fileName) {
        // Captura la pantalla y guarda la imagen en un archivo temporal
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Define el destino del archivo
        File destFile = new File("./screenshots/" + fileName + ".png");

        try {
            // Crea el directorio si no existe
            destFile.getParentFile().mkdirs();

            // Copia la captura de pantalla al destino
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Captura de pantalla guardada en: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al guardar la captura de pantalla.");
        }
    }
}
