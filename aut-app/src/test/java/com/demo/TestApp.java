//Students demo 
package com.demo;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestApp {

    WebDriver driver;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
    }

    @AfterAll
    void tearDown() {
        driver.quit();
    }

    @Test
    void testLogin() {
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("submit")).click();
        Assertions.assertTrue(driver.getTitle().contains("Dashboard"));
    }

    @Test
    void testAddTicket() {
        driver.get("http://localhost:8080/addTicket");
        driver.findElement(By.id("title")).sendKeys("Test Ticket");
        driver.findElement(By.id("description")).sendKeys("This is a test ticket");
        driver.findElement(By.id("submit")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Ticket added successfully"));
    }

    @Test
    void testViewTickets() {
        driver.get("http://localhost:8080/tickets");
        Assertions.assertTrue(driver.getPageSource().contains("Test Ticket"));
    }

}