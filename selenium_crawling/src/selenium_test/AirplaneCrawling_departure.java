package selenium_test;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import selenium_test.dao.DataDAO;
import selenium_test.vo.AirplainArriveVO;
import selenium_test.vo.AirplainDepartVO;

//출발하는 모든 아시아나 항공편 크롤링
public class AirplaneCrawling_departure {
	 
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
	
	public AirplaneCrawling_departure (){
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
	

	public void activateBot() {
		DataDAO dao = new DataDAO();
		try {
			driver.get(url);
			Thread.sleep(1000); // 페이지 로딩 대기 시간
			
			//아시아나 편명으로 검색(OZ-nnnn)
			element = driver.findElement(By.name("fp_iata"));
			element.sendKeys("OZ");
			
			//용도 선택->여객
			elementPurpose = driver.findElement(By.name("regCls"));
			Select purpose = new Select(elementPurpose);
			//purpose.selectByVisibleText("여객");
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
			int startDay = 20221201;
			int endDay = 20230228;
			
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
			
			//1페이지 크롤링
			for (int i = 1; i < elements.size(); i=i+2) {	
				elementTd = element
						.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/table/tbody/tr[" + i + "]"));
				elementsTd = elementTd.findElements(By.tagName("td"));
				
				System.out.print("날짜 : "+elementsTd.get(0).getText());	
				System.out.print(" 편명 : "+elementsTd.get(4).getText());
				System.out.print(" 계획 : "+elementsTd.get(8).getText());
				System.out.print(" 예상 : "+elementsTd.get(10).getText());
				System.out.print(" 출발 : "+elementsTd.get(12).getText());	//실제출발
				
				String depArr = elementsTd.get(6).getText();
				String[] deps = depArr.split("->");
				
				System.out.print(" 출발지 : "+deps[0]);
				String depCode = deps[0].substring(deps[0].length()-4, deps[0].length()-1);
				System.out.print(" 출발지 코드 : " + depCode);
				String[] depSplit = deps[0].split("\\(");
				if(depSplit.length==3) {
					String depName = depSplit[1].substring(0,depSplit[1].length()-1);
					System.out.print(" 출발지 명칭 : " + depName);
				} else {
					String depName = depSplit[0];
					System.out.print(" 출발지 명칭 : " + depName);
				}
				
				System.out.print(" 도착지 : "+deps[1]);
				String arrCode = deps[1].substring(deps[1].length()-4, deps[1].length()-1);
				System.out.print(" 도착지 코드 : " + arrCode);
				String[] arrSplit = deps[1].split("\\(");
				if(arrSplit.length==3) {
					String arrName = arrSplit[1].substring(0,arrSplit[1].length()-1);
					System.out.print(" 출발지 명칭 : " + arrName);
				} else {
					String arrName = arrSplit[0];
					System.out.print(" 출발지 명칭 : " + arrName);
				}
				
				System.out.println();
				
				try {
					AirplainDepartVO depVo = new AirplainDepartVO();
					depVo.setFullDeparture(deps[0]);	//출발지
					depVo.setFullArrival(deps[1]);		//도착지
					String depTime = elementsTd.get(0).getText()+" "+elementsTd.get(12).getText()+":00";	//실제출발시간
					depVo.setFlightName(elementsTd.get(4).getText());	//항공편명
					depVo.setDepDay(Date.valueOf(elementsTd.get(0).getText()));	//출발일
					depVo.setDepTime(Timestamp.valueOf(depTime));	//출발시간
					depVo.setDepCode(depCode);
					if(depSplit.length==3) {
						String depName = depSplit[1].substring(0,depSplit[1].length()-1);
						depVo.setDepName(depName);
					} else {
						String depName = depSplit[0];
						depVo.setDepName(depName);
					}
					depVo.setArrCode(arrCode);
					if(arrSplit.length==3) {
						String arrName = arrSplit[1].substring(0,arrSplit[1].length()-1);
						depVo.setArrName(arrName);
					} else {
						String arrName = arrSplit[0];
						depVo.setArrName(arrName);
					}
					
					dao.departInsert(depVo);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
			}
			
			//나머지 페이지 크롤링
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

					System.out.print("날짜 : "+elementsTd.get(0).getText());	
					System.out.print(" 편명 : "+elementsTd.get(4).getText());
					System.out.print(" 계획 : "+elementsTd.get(8).getText());
					System.out.print(" 예상 : "+elementsTd.get(10).getText());
					System.out.print(" 출발 : "+elementsTd.get(12).getText());	//실제출발
					
					String depArr = elementsTd.get(6).getText();
					String[] deps = depArr.split("->");
					
					System.out.print(" 출발지 : "+deps[0]);
					String depCode = deps[0].substring(deps[0].length()-4, deps[0].length()-1);
					System.out.print(" 출발지 코드 : " + depCode);
					String[] depSplit = deps[0].split("\\(");
					if(depSplit.length==3) {
						String depName = depSplit[1].substring(0,depSplit[1].length()-1);
						System.out.print(" 출발지 명칭 : " + depName);
					} else {
						String depName = depSplit[0];
						System.out.print(" 출발지 명칭 : " + depName);
					}
					
					System.out.print(" 도착지 : "+deps[1]);
					String arrCode = deps[1].substring(deps[1].length()-4, deps[1].length()-1);
					System.out.print(" 도착지 코드 : " + arrCode);
					String[] arrSplit = deps[1].split("\\(");
					if(arrSplit.length==3) {
						String arrName = arrSplit[1].substring(0,arrSplit[1].length()-1);
						System.out.print(" 출발지 명칭 : " + arrName);
					} else {
						String arrName = arrSplit[0];
						System.out.print(" 출발지 명칭 : " + arrName);
					}
					
					System.out.println();
					
					try {
						AirplainDepartVO depVo = new AirplainDepartVO();
						depVo.setFullDeparture(deps[0]);	//출발지
						depVo.setFullArrival(deps[1]);		//도착지
						String depTime = elementsTd.get(0).getText()+" "+elementsTd.get(12).getText()+":00";	//실제출발시간
						depVo.setFlightName(elementsTd.get(4).getText());	//항공편명
						depVo.setDepDay(Date.valueOf(elementsTd.get(0).getText()));	//출발일
						depVo.setDepTime(Timestamp.valueOf(depTime));	//출발시간
						depVo.setDepCode(depCode);
						if(depSplit.length==3) {
							String depName = depSplit[1].substring(0,depSplit[1].length()-1);
							depVo.setDepName(depName);
						} else {
							String depName = depSplit[0];
							depVo.setDepName(depName);
						}
						depVo.setArrCode(arrCode);
						if(arrSplit.length==3) {
							String arrName = arrSplit[1].substring(0,arrSplit[1].length()-1);
							depVo.setArrName(arrName);
						} else {
							String arrName = arrSplit[0];
							depVo.setArrName(arrName);
						}
						
						dao.departInsert(depVo);
					} catch (Exception e) {
						e.printStackTrace();
					} 
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
		AirplaneCrawling_departure bot1 = new AirplaneCrawling_departure();
		bot1.activateBot();
	}
}
