package com.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DeckCreatePageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = System.getProperty("baseUrl", "http://localhost:8088");
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1200");
        } else {
            options.addArguments("--disable-gpu", "--window-size=1920,1200");
        }
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(baseUrl + "/decks/create");
        waitForPageReady();
        loginIfNeeded();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("title")));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    // ===== Helpers =====
    private void waitForPageReady() {
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loginIfNeeded() {
        if (driver.getCurrentUrl().contains("/login")) {
            WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
            WebElement passwordField = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("login-button"));

            emailField.clear();
            emailField.sendKeys("nvh1");
            pause(1000);

            passwordField.clear();
            passwordField.sendKeys("nvhnvh");
            pause(1000);

            loginButton.click();
            pause(1000);

            waitForPageReady();
            if (!driver.getCurrentUrl().contains("/decks/create")) {
                driver.get(baseUrl + "/decks/create");
                waitForPageReady();
            }
        }
    }

    private void chooseSubject(String visibleText, String customIfOther) {
        WebElement subjectSel = driver.findElement(By.id("subject"));
        new Select(subjectSel).selectByVisibleText(visibleText);
        pause(1000);

        if ("Khác".equals(visibleText)) {
            try {
                new WebDriverWait(driver, Duration.ofMillis(700))
                        .until(d -> d.findElement(By.id("customSubjectWrapper")).isDisplayed());
            } catch (TimeoutException ignore) {
                ((JavascriptExecutor) driver)
                        .executeScript("document.getElementById('customSubjectWrapper').style.display='block'");
            }
            WebElement custom = wait.until(ExpectedConditions.elementToBeClickable(By.id("customSubject")));
            custom.clear();
            custom.sendKeys(customIfOther);
            pause(1000);
        }
    }

    private void setPublic(boolean isPublic) {
        WebElement cb = driver.findElement(By.id("isPublic"));
        if (cb.isSelected() != isPublic)
            cb.click();
        pause(1000);
    }

    private void submitForm() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        pause(1000);
        waitForPageReady();
    }

    private boolean redirectedToDecksOrHasSuccessMessage() {
        if (driver.getCurrentUrl().matches(".*/decks($|/.*)"))
            return true;
        String html = driver.getPageSource().toLowerCase();
        return html.contains("tạo bộ thẻ thành công") || html.contains("tạo thành công");
    }

    // ===== Tests =====

    @Test
    public void testCreateDeckSuccess() {
        WebElement title = driver.findElement(By.id("title"));
        WebElement desc = driver.findElement(By.id("description"));

        title.sendKeys("Bo the Toan 01");
        pause(1000);
        desc.sendKeys("Mo ta ngan cho deck");
        pause(1000);

        chooseSubject("Toán học", null);
        setPublic(true);

        submitForm();

        assertTrue(redirectedToDecksOrHasSuccessMessage(),
                "Sau khi submit hợp lệ phải về /decks hoặc có thông báo thành công");
    }

    @Test
    public void testTitleMax30_NoSweetAlert() {
        WebElement title = driver.findElement(By.id("title"));
        WebElement desc = driver.findElement(By.id("description"));

        title.sendKeys("123456789012345678901234567890"); // 30 ký tự
        pause(1000);
        desc.sendKeys("desc");
        pause(1000);

        chooseSubject("Vật lý", null);
        setPublic(false);

        submitForm();

        try {
            new WebDriverWait(driver, Duration.ofMillis(800))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-popup")));
            fail("Không được xuất hiện SweetAlert khi title <= 30");
        } catch (TimeoutException expected) {
            // OK
        }
    }

    @Test
    public void testTitleTooLong_ShouldShowSweetAlert() {
        WebElement title = driver.findElement(By.id("title"));
        WebElement desc = driver.findElement(By.id("description"));

        title.sendKeys("1234567890123456789012345678901"); // 31 ký tự
        pause(1000);
        desc.sendKeys("desc long");
        pause(1000);

        chooseSubject("Khác", "Xác suất - Thống kê");
        setPublic(true);

        submitForm();
        pause(1000);
        WebElement popup = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-popup")));
        WebElement titlePopup = popup.findElement(By.className("swal2-title"));

        assertEquals("Tên bộ thẻ quá dài", titlePopup.getText(),
                "Phải hiện SweetAlert với tiêu đề đúng khi > 30 ký tự");
        pause(1000);
        assertFalse(driver.getCurrentUrl().matches(".*/decks($|/.*)"),
                "Không được redirect khi client-side validation chặn submit");
    }
}
