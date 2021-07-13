package com.adolesce.server;

import com.adolesce.common.enums.ResultCode;
import com.adolesce.common.enums.WeekType;
import com.adolesce.common.enums.WeekTypeNum;
import org.junit.Test;

public class EnumTest {
	
	@Test
	public void test1(){
		System.out.println(WeekType.MONDAY.getCodeId());
		System.out.println(WeekType.MONDAY.getId());

		System.out.println(ResultCode.AUTH_LIMIT.toString());
		System.out.println(ResultCode.AUTH_LIMIT.name());
	}
	
	@Test
	public void test2(){
		WeekType wt = WeekType.getByCodeId("001");
		System.out.println(wt.getCodeName());
	}
	
	@Test
	public void test3(){
		test31(WeekType.FRIDAY);
	}
	
	private void test31(WeekType wt){
		switch (wt) {
		case MONDAY:
			System.out.println("星期一："+wt.getId());
			break;
		case TUESDAY:
			System.out.println("星期二："+wt.getId());
			break;
		case WEDNESDAY:
			System.out.println("星期三："+wt.getId());
			break;
		case THURSDAY:
			System.out.println("星期四："+wt.getId());
			break;
		case FRIDAY:
			System.out.println("星期五："+wt.getId());
			break;
		case SATURDAY:
			System.out.println("星期六："+wt.getId());
			break;
		case SUNDAY:
			System.out.println("星期天："+wt.getId());
			break;
		default:
			break;
		}
	}
	
	@Test
	public void test5(){
		System.out.println(WeekType.FRIDAY.eq("005"));
	}
	//---------------------------------------------------
	@Test
	public void test6(){
		System.out.println(WeekTypeNum.MONDAY.getId());
	}
	
	@Test
	public void test7(){
		WeekTypeNum wt = WeekTypeNum.getByCodeId(1);
		System.out.println(wt.getCodeName());
	}
	
	@Test
	public void test8(){
		test8_childFunction(WeekTypeNum.FRIDAY);
	}
	
	private void test8_childFunction(WeekTypeNum wt){
		switch (wt) {
		case MONDAY:
			System.out.println("星期一："+wt.getId());
			break;
		case TUESDAY:
			System.out.println("星期二："+wt.getId());
			break;
		case WEDNESDAY:
			System.out.println("星期三："+wt.getId());
			break;
		case THURSDAY:
			System.out.println("星期四："+wt.getId());
			break;
		case FRIDAY:
			System.out.println("星期五："+wt.getId());
			break;
		case SATURDAY:
			System.out.println("星期六："+wt.getId());
			break;
		case SUNDAY:
			System.out.println("星期天："+wt.getId());
			break;
		default:
			break;
		}
	}
	
	@Test
	public void test9(){
		System.out.println(WeekTypeNum.FRIDAY.eq(5));
	}

}
