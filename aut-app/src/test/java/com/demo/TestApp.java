package com.demo;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestApp {

    WebDriver driver;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterAll
    void tearDown() {
        driver.quit();
    }

    @Test
    void testLogin() throws Exception {

        driver.get("http://localhost:8080");
        Thread.sleep(2000);

        driver.findElement(By.id("user")).sendKeys("admin");
        driver.findElement(By.id("pass")).sendKeys("1234");
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        Thread.sleep(2000);

        Assertions.assertTrue(driver.getPageSource().contains("Issue Tracker"));
    }

    @Test
    void testAddTicket() throws Exception {

        driver.get("http://localhost:8080");
        Thread.sleep(2000);

        // Login
        driver.findElement(By.id("user")).sendKeys("admin");
        driver.findElement(By.id("pass")).sendKeys("1234");
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        Thread.sleep(2000);

        // Add Ticket
        driver.findElement(By.xpath("//button[contains(text(),'Add Ticket')]")).click();
        Thread.sleep(1000);

        driver.findElement(By.id("ticket")).sendKeys("JUnit Ticket");
        driver.findElement(By.xpath("//button[contains(text(),'Submit')]")).click();

        Thread.sleep(2000);

        Assertions.assertTrue(driver.getPageSource().contains("Added to DB"));
    }

    @Test
    void testViewTickets() throws Exception {

        driver.get("http://localhost:8080");
        Thread.sleep(2000);

        // Login
        driver.findElement(By.id("user")).sendKeys("admin");
        driver.findElement(By.id("pass")).sendKeys("1234");
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        Thread.sleep(2000);

        // View Tickets
        driver.findElement(By.xpath("//button[contains(text(),'View Tickets')]")).click();

        Thread.sleep(2000);

        Assertions.assertTrue(driver.getPageSource().contains("["));
    }
}