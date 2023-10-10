package Parser;

import Model.Review;
import com.itextpdf.text.DocumentException;
import org.example.WriterToPdf;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private WebDriver browser;
    private String nameOfChapter;

    private Parser(String nameOfChapter) {
        browser = new ChromeDriver();
        this.nameOfChapter = nameOfChapter;
    }

    public static Parser initParser(String nameOfChapter) {
        return new Parser(nameOfChapter);
    }

    public void parse() throws DocumentException, IOException {
        openBrowser();
        try {
            getInfoFromChapter();
        } finally {
            closeBrowser();
        }
    }

    private void getInfoFromChapter() throws DocumentException, IOException {
        ArrayList<Review> allReviews = new ArrayList<>();
        int countOfPages = getCountOfPages();

        for (int i = 1; i <= countOfPages; ++i) {
            goNextPage(i);
            allReviews.addAll(getInfoFromCurrentPage());
        }

        String chapterTitle = browser.findElement(By.cssSelector("header h1")).getText();
        WriterToPdf.write(allReviews, chapterTitle);
    }

    private void goNextPage(int numberNextPage) {
        JavascriptExecutor js = (JavascriptExecutor) browser;
        WebElement nextPage = browser.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[3]/ul/li[" +
                numberNextPage +
                "]"));
        js.executeScript("arguments[0].scrollIntoView();", nextPage);

        nextPage.click();
    }

    private ArrayList<Review> getInfoFromCurrentPage() {
        ArrayList<Review> reviewsOnCurrentPage = new ArrayList<>();
        List<WebElement> reviewsOnPage = getReviewsOnPage();

        for (WebElement webElement : reviewsOnPage)
            reviewsOnCurrentPage.add(getCurrentReview(webElement));

        return reviewsOnCurrentPage;
    }

    private List<WebElement> getReviewsOnPage() {
        return browser.findElements(By.xpath("/html/body/div[1]/div/div[1]/main/div[1]/div[2]/div/div"));
    }

    private Review getCurrentReview(WebElement tagWithReview) {
        Review currentReview = new Review();
        currentReview.setText(tagWithReview.getText().substring(5));
        return currentReview;
    }

    private int getCountOfPages() {
        return browser.findElements(By.xpath("//*[@id=\"main\"]/div[1]/div[3]/ul/li")).size();
    }

    private void openBrowser() {
        browser.get(nameOfChapter);
    }

    private void closeBrowser() {
        if (browser != null) {
            browser.quit();
        }
    }
}
