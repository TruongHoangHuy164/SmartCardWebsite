package com.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SignupPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private String baseUrl;

    private String formSel;
    private String emailSel;
    private String usernameSel;
    private String passwordSel;
    private String confirmPwdSel;
    private String termsSel;
    private String submitBtnSel;

    @BeforeEach
    public void setUp() {
        baseUrl = System.getProperty("baseUrl", "http://localhost:8088");

        formSel = "form#signupForm";
        emailSel = "#email";
        usernameSel = "#username";
        passwordSel = "#password";
        confirmPwdSel = "#confirmPassword";
        termsSel = "#terms";
        submitBtnSel = "form#signupForm button[type='submit']";

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1200");
        } else {
            options.addArguments("--disable-gpu", "--window-size=1920,1200");
        }
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        openSignupAndDisableHtml5Validation();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    // ===== Helpers =====
    private void openSignupAndDisableHtml5Validation() {
        driver.get(baseUrl + "/signup");
        waitForPageReady();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(formSel)));

        // 🔧 Disable HTML5 validation
        ((JavascriptExecutor) driver).executeScript(
                "const f=document.querySelector(arguments[0]); if(f) f.setAttribute('novalidate','');",
                formSel);
    }

    private void waitForPageReady() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
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

    private void fillSignupForm(String email, String username, String password, String confirmPassword,
            boolean acceptTerms) {
        WebElement emailField = driver.findElement(By.cssSelector(emailSel));
        WebElement userField = driver.findElement(By.cssSelector(usernameSel));
        WebElement passField = driver.findElement(By.cssSelector(passwordSel));
        WebElement confirmField = driver.findElement(By.cssSelector(confirmPwdSel));

        emailField.clear();
        emailField.sendKeys(email);
        userField.clear();
        userField.sendKeys(username);
        passField.clear();
        passField.sendKeys(password);
        confirmField.clear();
        confirmField.sendKeys(confirmPassword);

        WebElement termsCb = driver.findElement(By.cssSelector(termsSel));
        if (termsCb.isSelected() != acceptTerms)
            termsCb.click();
    }

    private void clickSubmit() {
        driver.findElement(By.cssSelector(submitBtnSel)).click();
    }

    private String[] waitForSwalAndGetTitleText() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));
        WebElement title = popup.findElement(By.cssSelector(".swal2-title"));
        WebElement text = popup.findElement(By.cssSelector(".swal2-html-container"));
        return new String[] { title.getText(), text.getText() };
    }

    private void closeSwalIfPresent() {
        try {
            WebElement okBtn = driver.findElement(By.cssSelector(".swal2-confirm"));
            okBtn.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".swal2-popup")));
        } catch (NoSuchElementException ignored) {
        }
    }

    private void logSwal(String title, String text) {
        System.out.printf("✅ [Swal] %s → %s%n", title, text);
    }

    // ===== Tests =====

    @Test
    public void testSignup_InvalidEmail_ShowsSwalError() {
        fillSignupForm("khongphaiemail", "validuser", "abcdef", "abcdef", true);
        clickSubmit();

        String[] msg = waitForSwalAndGetTitleText();
        logSwal(msg[0], msg[1]);
        assertEquals("Email không hợp lệ", msg[0]);
    }

    @Test
    public void testSignup_UsernameTooShort_ShowsSwalError() {
        fillSignupForm("user@example.com", "abc", "abcdef", "abcdef", true);
        clickSubmit();

        String[] msg = waitForSwalAndGetTitleText();
        logSwal(msg[0], msg[1]);
        assertEquals("Tên đăng nhập không hợp lệ", msg[0]);
    }

    @Test
    public void testSignup_PasswordTooShort_ShowsSwalError() {
        fillSignupForm("user@example.com", "validuser", "123", "123", true);
        clickSubmit();

        String[] msg = waitForSwalAndGetTitleText();
        logSwal(msg[0], msg[1]);
        assertEquals("Mật khẩu quá ngắn", msg[0]);
    }

    @Test
    public void testSignup_PasswordMismatch_ShowsSwalError() {
        fillSignupForm("user@example.com", "validuser", "abcdef", "abcdeg", true);
        clickSubmit();

        String[] msg = waitForSwalAndGetTitleText();
        logSwal(msg[0], msg[1]);
        assertEquals("Mật khẩu không khớp", msg[0]);
    }

    @Test
    public void testSignup_TermsUnchecked_ShowsSwalError() {
        fillSignupForm("user@example.com", "validuser", "abcdef", "abcdef", false);
        clickSubmit();

        String[] msg = waitForSwalAndGetTitleText();
        logSwal(msg[0], msg[1]);
        assertEquals("Bạn chưa đồng ý điều khoản", msg[0]);
    }

    @Test
    public void testSignup_Success_EitherSwalSuccessOrRedirect() {
        long ts = System.currentTimeMillis();
        String email = "user" + ts + "@example.com";
        String username = "user" + ts;

        fillSignupForm(email, username, "abcdef", "abcdef", true);
        clickSubmit();

        boolean successDetected = false;
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement popup = shortWait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));
            String title = popup.findElement(By.cssSelector(".swal2-title")).getText();
            String text = popup.findElement(By.cssSelector(".swal2-html-container")).getText();
            logSwal(title, text);
            if ("Thành công".equals(title))
                successDetected = true;
            closeSwalIfPresent();
        } catch (TimeoutException ignored) {
            // không thấy Swal -> thử redirect
        }

        if (!successDetected) {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("🌐 Redirect URL: " + currentUrl);
            if (!currentUrl.contains("/signup"))
                successDetected = true;
        }

        assertTrue(successDetected,
                "Không phát hiện trạng thái thành công. Kỳ vọng có SweetAlert 'Thành công' hoặc redirect khỏi /signup.");
    }
}
