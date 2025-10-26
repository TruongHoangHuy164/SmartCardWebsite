package com.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LoginPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private String baseUrl;
    private String userFieldSel;

    @BeforeEach
    public void setUp() {
        baseUrl = System.getProperty("baseUrl", "http://localhost:8088");
        userFieldSel = System.getProperty("userFieldSel", "#email,[name='username'],[name='email']");

        // Cho phép chọn chế độ headless qua command line:
        // mvn test -Dheadless=true (ẩn) hoặc -Dheadless=false (hiện)
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            // Chạy ẩn (không hiện cửa sổ)
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1200");
        } else {
            // 🧭 Hiện giao diện thật
            options.addArguments("--disable-gpu", "--window-size=1920,1200");
        }
        // Nếu Chrome lỗi “remote origins”, thêm dòng này
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(baseUrl + "/login");
        waitForPageReady();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(userFieldSel)),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("input")),
                ExpectedConditions.presenceOfElementLocated(By.id("login-button"))));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    // ===== Helpers =====
    private void waitForPageReady() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
    }

    private void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ===== Tests =====
    @Test
    public void testLoginSuccess() {
        // Nhập thông tin đăng nhập hợp lệ
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        emailField.sendKeys("nvh1");
        pause(1000); // Chờ 1 giây để xem thao tác
        passwordField.sendKeys("nvhnvh");
        pause(1000);
        loginButton.click();
        pause(2000); // Chờ 2 giây để xem kết quả

        // Kiểm tra điều hướng sau khi đăng nhập thành công
        String currentUrl = driver.getCurrentUrl();
        assertEquals("http://localhost:8088/", currentUrl);
    }

    @Test
    public void testLoginFailure() {
        // Nhập thông tin đăng nhập không hợp lệ
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        emailField.sendKeys("invalidUser");
        pause(1000);
        passwordField.sendKeys("invalidPassword");
        pause(1000);
        loginButton.click();
        pause(2000);

        // Kiểm tra thông báo lỗi
        WebElement errorMessage = driver.findElement(By.className("alert-danger"));
        assertEquals("Lỗi", errorMessage.getText());
    }

    @Test
    public void testEmptyFields() {
        // Để trống các trường và nhấn nút đăng nhập
        WebElement loginButton = driver.findElement(By.id("login-button"));
        pause(1000);
        loginButton.click();
        pause(2000);

        // Kiểm tra thông báo lỗi
        WebElement errorMessage = driver.findElement(By.className("alert-danger"));
        assertEquals("Lỗi", errorMessage.getText());
    }
}
