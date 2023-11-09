package selenium_test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

//출발하는 모든 아시아나 항공편
public class AirplaneCrawling2 {
	 
	private WebDriver driver;
	private WebElement element, elementPurpose, elementFrame, elementTd, elementDataNum;
	private WebElement elementDepDay, elementArrDay;
	private List<WebElement> elements;
	private List<WebElement> elementsTd, elementsPage;
	private List<WebElement> elementsReg;
	
	private String url;
	
 	// 드라이버 설치 경로
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH="D:\\chromedriver_win32\\chromedriver.exe";
	
	public AirplaneCrawling2 (){
		// WebDriver 경로 설정
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
		// WebDriver 옵션 설정
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
        
		driver = new ChromeDriver(options);
		
		//동적 페이지에서 url은 jsp 파일로 정하기.
		url = "https://www.airportal.go.kr/life/airinfo/RaSkeFrm.jsp";
	}
	
	//지저분...
	public void activateBot() {
		try {
			driver.get(url);
			Thread.sleep(1000); // 3. 페이지 로딩 대기 시간
			
			//아시아나 편명으로 검색(OZ-nnnn)
			element = driver.findElement(By.name("fp_iata"));
			element.sendKeys("OZ");
			
			//용도 선택->여객
			elementPurpose = driver.findElement(By.name("regCls"));
			Select purpose = new Select(elementPurpose);
			purpose.selectByIndex(1);

			//프레임 전환
			elementFrame = driver.findElement(By.name("airportframe"));
			driver.switchTo().frame(elementFrame);
			
			elementsReg = driver.findElements(By.name("ser_region"));			
			Select depRegion = new Select(elementsReg.get(0));
			depRegion.selectByIndex(3);
			//프레임 복귀
			driver.switchTo().defaultContent();		
			
			//날짜설정
			int startDay = 20230801;
			int endDay = 20230802;
			elementDepDay = driver.findElement(By.id("current_dt_from"));
			elementDepDay.clear();
			elementDepDay.sendKeys(startDay+"");
			
			elementArrDay = driver.findElement(By.id("current_dt_to"));
			elementArrDay.clear();
			elementArrDay.sendKeys(endDay+"");
			
			// 검색 버튼 누르기(javascript)
			elements = driver.findElements(By.tagName("a"));
			elements.get(2).click();
			
			//프레임 전환, 세부 데이터 접근
			elementFrame = driver.findElement(By.name("sframe"));
			driver.switchTo().frame(elementFrame);
			
			//페이지 갯수 구하기
			elementDataNum = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[4]/td/table/tbody/tr[4]/td/table/tbody/tr/td[3]/font[1]"));
			int dataNum = Integer.parseInt(elementDataNum.getText());
			int page = dataNum%10==0 ? dataNum/10: dataNum/10+1;

			//페이지 선택해서 자료 조회
			
			element = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody"));
			elements = element.findElements(By.tagName("tr"));
			elementTd = element.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody/tr[1]"));
			elementsTd = elementTd.findElements(By.tagName("td"));
			
			for (int i = 1; i < elements.size(); i=i+2) {
				elementTd = element
						.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody/tr[" + i + "]"));
				elementsTd = elementTd.findElements(By.tagName("td"));
				for (int j = 0; j < elementsTd.size(); j++) {
					System.out.print(elementsTd.get(j).getText() + " "); //항공사  편명   출발지  계획  예상 도착 구분 현황
				}
				System.out.println();
			}
			//do while문 쓰기?
			int n=0;
			for(int m=0;m<page;m++) {					
				element = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[4]/td/table/tbody/tr[4]/td/table/tbody/tr/td[2]/font"));
				if(m>=10) {
					element = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[4]/td/table/tbody/tr[4]/td/table/tbody/tr/td[2]/font/font"));;
				}
				elementsPage = element.findElements(By.tagName("a"));

				n=m%10;
				elementsPage.get(n).click();
				Thread.sleep(500);
				
				element = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody"));
				elements = element.findElements(By.tagName("tr"));
				elementTd = element.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody/tr[1]"));
				elementsTd = elementTd.findElements(By.tagName("td"));
				
				for (int i = 1; i < elements.size(); i=i+2) {
					elementTd = element
							.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody/tr[" + i + "]"));
					elementsTd = elementTd.findElements(By.tagName("td"));
					for (int j = 0; j < elementsTd.size(); j++) {
						System.out.print(elementsTd.get(j).getText() + " "); //항공사  편명   출발지  계획  예상 도착 구분 현황
					}
					System.out.println();
				}
			}		
			
			//프레임 복귀
			driver.switchTo().defaultContent();	
						
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			//driver.close(); // 브라우저 종료
		}
	}
	
	
	public static void main(String[] args) {
		AirplaneCrawling2 bot1 = new AirplaneCrawling2();
		bot1.activateBot();
	}
}
