package com.self.library.irrelevant;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author Administrator
 * @Title: 无关测试
 * @Description:
 * @Date 2021-04-17 16:40
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
public class IrrelevantTest
{
    @Test
    public void Test()
    {
        System.out.println(StandardCharsets.UTF_8.name());

        LocalDate one = LocalDate.now();
        System.out.println(one);
        DayOfWeek oneDay = one.getDayOfWeek();
        System.out.println(oneDay);
        System.out.println(oneDay.getValue());

        System.out.println("----------");
        LocalDate oneStart = LocalDate.now();
        System.out.println(oneStart);
        Integer oneWeek = oneStart.getDayOfWeek().getValue();
        LocalDate oneEnd = oneStart.withDayOfYear(1);
        System.out.println(oneEnd);
        System.out.println(oneEnd.toString());

        //System.out.println("----------");
        //LocalDateTime now = LocalDateTime.now();
        //System.out.println(now);
        //ZoneId zoneId = ZoneId.systemDefault();
        //System.out.println(zoneId);
        //ZonedDateTime zonedDateTime = now.atZone(zoneId);
        //System.out.println(zonedDateTime);
        //Instant instant = zonedDateTime.toInstant();
        //System.out.println(instant);
        //Date date = Date.from(instant);
        //System.out.println(date);

        System.out.println("----------");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        LocalDateTime nowPlusOne = now.plusDays(1);
        System.out.println(nowPlusOne);
    }
}
