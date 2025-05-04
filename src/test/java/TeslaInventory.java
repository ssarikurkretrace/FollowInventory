import com.microsoft.playwright.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeslaInventory {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

//        getBrowserCookies();
        getInventoryFromUI();

    }

    public static void getBrowserCookies() throws InterruptedException {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));

//            var stealthContext = Stealth4j.newStealthContext(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/115.0")
                .setViewportSize(1920, 1080)

        );

        Page page = context.newPage();
        page.navigate("https://www.tesla.com/tr_TR/inventory/new/my?arrangeby=plh&zip=06200&range=0&lat=41.0082&lng=28.9784");
        BrowserUtils.waitFor(3);

//            page.locator("#tsla-accept-cookie").click();

        page.locator(".tds-link.tds-link--secondary.tds-locale-selector-language.tds-lang--tr").click();
        context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("storageState.json")));

        page.close();
    }

    public static void getInventoryFromUI(){

        try (Playwright playwright = Playwright.create()) {
//            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
//                    .setHeadless(false));

            Browser browser = playwright.firefox().connect(
                    "ws://playwright-server:3000/ws",  // Docker container adı ile
                    new BrowserType.ConnectOptions()
                            .setTimeout(120000)  // Timeout süresini artır
            );

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setStorageStatePath(Paths.get("storageState.json"))
                    .setIgnoreHTTPSErrors(true)
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/115.0")
                    .setViewportSize(1600, 900)
            );

            Page page = context.newPage();
            page.navigate("https://www.tesla.com/tr_TR/inventory/new/my?arrangeby=plh&zip=06200&range=0&lat=41.0082&lng=28.9784");
//            page.navigate("https://www.tesla.com/en_GB/inventory/new/my?arrangeby=plh&range=0");
            BrowserUtils.waitFor(3);
            Files.createDirectories(Paths.get("screenshots"));
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/login-success1.png"))
                    .setFullPage(true));
            BrowserUtils.waitFor(1);

////            page.locator("#tsla-accept-cookie").click();
////
//            page.locator(".tds-link.tds-link--secondary.tds-locale-selector-language.tds-lang--tr").click();
//
//            BrowserUtils.waitFor(2);

            // Sayfadaki tüm envanter kartlarını bul
            Locator envanterLocator = page.locator(".result.card");
            int envanterSize = envanterLocator.count();
            System.out.println("envanterSize = " + envanterSize);

            List<Map<String, Object>> inventoryList = new ArrayList<>();

            if (envanterSize > 0) {
//                Files.createDirectories(Paths.get("screenshots"));
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("screenshots/login-success2.png"))
                        .setFullPage(true));
                BrowserUtils.waitFor(1);
                for (int i = 0; i < envanterSize; i++) { // Playwright'ta 0'dan başlar
                    Map<String, Object> inventoryMap = new HashMap<>();

                    // Her bir kart içinde model ve fiyatı bul
                    Locator modelLocator = envanterLocator.nth(i).locator(".tds-text_color--10");
                    Locator priceLocator = envanterLocator.nth(i).locator(".result-price .tds-text--h4");

                    String modelStr = modelLocator.textContent().trim();
                    String priceStr = priceLocator.textContent().trim();

                    inventoryMap.put("ModelType", modelStr);
                    inventoryMap.put("Price", priceStr);

                    inventoryList.add(inventoryMap);
                }

                // Tüm listeyi CSV'ye yaz Downloads klasörüne kaydet.
                String fileName = "envanterList.csv";
                ExportUtils.toCSV(fileName, inventoryList);

                // CSV'yi e-posta ile gönder
                BrowserUtils.waitFor(3);
                GmailUtils.sendAttach(
                        "Envanterde Arac Var \nPlease See attachment.",
                        "Alarm Envanterde Arac Var!!!!!",
                        ConfigurationReader.get("toMail"),
                        ConfigurationReader.get("fromMail"),
                        BrowserUtils.getDownloadPath(fileName),
                        "screenshots/login-success1.png",
                        "screenshots/login-success2.png"
                );
            }

            page.waitForTimeout(1000); // 1 saniye bekle

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
