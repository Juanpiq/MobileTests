package org.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.example.Funciones.FuncionesGlobales;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
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
    private static Process appiumProcess; // Proceso de Appium

    @BeforeAll
    public static void startAppiumServer() {
        freePort(4723);
        try {
            // Iniciar Appium directamente con el comando 'appium', ya que está en el PATH
            ProcessBuilder processBuilder = new ProcessBuilder("appium.cmd");
            processBuilder.inheritIO(); // Mostrar la salida del servidor en la consola
            appiumProcess = processBuilder.start(); // Guardar referencia al proceso
            System.out.println("Appium Server iniciado.");
            Thread.sleep(10000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al iniciar Appium Server.");
        }
    }

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

    @AfterAll
    public static void cleanup(){
        stopAppiumServer();
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

        // Verificar si el archivo ya existe y darle un nuevo nombre si es necesario
        int counter = 1;
        while (destFile.exists()) {
            destFile = new File("./screenshots/" + fileName + "_" + counter + ".png");
            counter++;
        }

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

    private static void freePort(int port) {
        try {
            Process process = Runtime.getRuntime().exec("netstat -ano | findstr :" + port);
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("LISTENING")) {
                    String[] parts = line.split("\\s+");
                    String pid = parts[parts.length - 1];
                    Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
                    System.out.println("Puerto " + port + " liberado.");
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al liberar el puerto.");
        }
    }

    public static void stopAppiumServer() {
        if (appiumProcess != null && appiumProcess.isAlive()) {
            try {
                // Destruye el proceso de Appium
                appiumProcess.destroy();
                // Espera a que se cierre completamente
                appiumProcess.waitFor();

                System.out.println("Appium Server detenido correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error al detener Appium Server.");
            } finally {
                // Comando para forzar la liberación del puerto en caso de error
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM node.exe");
                    System.out.println("Procesos relacionados con Appium cerrados.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

