package selenium_test.vo;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AirplainDepartVO {
	
	private String flightName;
	private Date depDay;
	private Timestamp depTime;
	private String fullDeparture;
	private String depCode;
	private String depName;
	private String fullArrival;
	private String arrCode;
	private String arrName;
	
}
