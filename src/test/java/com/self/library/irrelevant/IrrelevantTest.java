package com.self.library.irrelevant;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

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
    }
}
