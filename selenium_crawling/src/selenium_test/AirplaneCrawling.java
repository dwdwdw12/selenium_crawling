package selenium_test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

public class AirplaneCrawling {
	 
	private WebDriver driver;
	private WebElement element, elementDay, elementAirport, elementFrame, elementTd;
	private List<WebElement> elements;
	private List<WebElement> elementsTd;
	
	private String url;
	
 	// 드라이버 설치 경로
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH="D:\\chromedriver_win32\\chromedriver.exe";
	
	public AirplaneCrawling (){
		// WebDriver 경로 설정
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
		// WebDriver 옵션 설정
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
        
		driver = new ChromeDriver(options);
		
		//동적 페이지에서 url은 jsp 파일로 정하기.
		url = "https://www.airportal.go.kr/life/airinfo/RbHanFrm.jsp";
	}
	
	public void activateBot() {
		try {
			driver.get(url);
			Thread.sleep(2000); // 3. 페이지 로딩 대기 시간
			
			//아시아나 편명으로 검색(OZ-nnnn)
			element = driver.findElement(By.name("fp_id"));
			element.sendKeys("OZ");
			
			//도착(끝에 /input[1]), 출발(끝에 /input[2]) 전환
			String arrPath = "/html/body/form/table/tbody/tr/td[2]/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[1]/td[2]/input[1]";
			String depPath = "/html/body/form/table/tbody/tr/td[2]/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[1]/td[2]/input[2]";
			String curSearch = depPath;
			element = driver.findElement(By.xpath(curSearch));
			element.click();
			
			//날짜설정
			elementDay = driver.findElement(By.id("current_date"));
			
			int startDay = 20231015;
			int endDay = 20231031;
			
			//지역 공항 선택
			elementAirport = driver.findElement(By.name("airport"));
			Select airport = new Select(elementAirport);

			System.out.println("        		  항공사  편명   출발지  계획  예상 도착 구분 현황");	//출력 항목
			
			// 날짜를 하나씩 증가시키며, 검색 후 콘솔에 목록 출력
			for (int i = startDay; i <= endDay; i++) {
				// 검색창에 날짜 입력
				elementDay.clear();
				elementDay.sendKeys(i + "");
				Thread.sleep(1000);
				
				for (int m = 0; m < 15; m++) {

					// 공항 선택
					airport.selectByIndex(m);
					String[] airportName = { "인천", "김포", "청주", "양양", "군산", "원주", "김해", "제주", "대구", "광주", "여수", "울산",
							"포항", "사천", "무안" };
					
					String state = "";
					if(curSearch.equals(arrPath)) {
						state = "도착";
					} else if(curSearch.equals(depPath)) {
						state = "출발";
					}
					
					// 검색 버튼 누르기(javascript)
					elements = driver.findElements(By.tagName("a"));
					elements.get(1).click();

					// frame 전환. html 안에 다른 html이 들어있을 때
					elementFrame = driver.findElement(By.tagName("iframe"));
					driver.switchTo().frame(elementFrame);
					element = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody"));
					elements = element.findElements(By.tagName("tr"));
					elementTd = element.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr[1]"));
					elementsTd = elementTd.findElements(By.tagName("td"));

					int trNum = elements.size();
					if (trNum % 2 == 1)
						trNum = trNum - 1; // 1은 빼는 이유 : 공백이 들어간 tr을 피하기 위해.
					int tdNum = elementsTd.size();

					// 대기시간
					Thread.sleep(1000);

					// tr은 1번째부터 시작. 여기에서는 짝수번째 tr에 공백이 들어있으므로 출력하지 않음.
					for (int j = 1; j <= trNum; j = j + 2) {
						elementTd = element
								.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr[" + j + "]"));
						elementsTd = elementTd.findElements(By.tagName("td"));
						
						if(trNum<=4) continue;	//일정이 없는 공항을 건너뛰기 위해서(최소 trNum=4).
						
						System.out.print((j / 2 + 1) + "번째 : " + i + "일 " + airportName[m] + "공항 " + state);
						for (int k = 0; k < tdNum - 1; k++) {
							System.out.print(elementsTd.get(k).getText()); //항공사  편명   출발지  계획  예상 도착 구분 현황
						}
						System.out.println();
					}
					driver.switchTo().defaultContent();
				}
			}
			
						
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			//driver.close(); // 브라우저 종료
		}
	}
	
	public static void main(String[] args) {
		AirplaneCrawling bot1 = new AirplaneCrawling();
		bot1.activateBot();
	}
}
