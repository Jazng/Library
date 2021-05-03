package com.self.library.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.self.library.constant.ErrorConstant.CREATE_TOKEN_ERROR;
import static com.self.library.constant.ErrorConstant.VERIFY_TOKEN_ERROR;
import static com.self.library.constant.LibraryConstant.TOKEN_KEY;

/**
 * @Author Administrator
 * @Title: JWT
 * @Description: JWT工具类
 * @Date 2021-04-17 11:31
 * @Version: 1.0
 */
@Slf4j
public class JWTUtils
{
    /**
     * 生成JWT Token
     *
     * @param map JWT Payload使用的数据
     * @return JWT Token
     */
    public static String getToken(Map<String, Object> map)
    {
        String token = null;
        //定义时间
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);

        try
        {
            //创建JWT生成器
            JWTCreator.Builder builder = JWT.create();
            //生成JWT Payload（有效负载）
            map.forEach((key, value) ->
            {
                if (value instanceof String)
                {
                    builder.withClaim(key, (String) value);
                }
                else if (value instanceof Integer)
                {
                    builder.withClaim(key, (Integer) value);
                }
                else if (value instanceof Long)
                {
                    builder.withClaim(key, (Long) value);
                }
                else if (value instanceof Double)
                {
                    builder.withClaim(key, (Double) value);
                }
                else if (value instanceof Boolean)
                {
                    builder.withClaim(key, (Boolean) value);
                }
                else if (value instanceof Date)
                {
                    builder.withClaim(key, (Date) value);
                }
                else if (value instanceof List)
                {
                    builder.withClaim(key, (List<?>) value);
                }
                else if (value instanceof Map)
                {
                    builder.withClaim(key, (Map<String, ?>) value);
                }
            });
            //设置过期时间
            builder.withExpiresAt(instance.getTime());
            /*
             * 根据秘钥设置JWT Signature（签名）
             * JWT Header（标头）会根据使用到的算法和生成token类型自动生成，此处token类型为jwt，算法为HMAC256
             */
            token = builder.sign(Algorithm.HMAC256(TOKEN_KEY));
        }
        catch (Exception e)
        {
            log.error(CREATE_TOKEN_ERROR, e);
            token = "";
        }

        return token;
    }

    /**
     * 校验JWT Token
     *
     * @param token Token
     * @return 校验是否正确，若正确，则返回解码器；否则，返回null
     */
    public static Pair<Boolean, DecodedJWT> verify(String token)
    {
        boolean check = false;
        DecodedJWT decodedJWT = null;
        try
        {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_KEY)).build();
            decodedJWT = jwtVerifier.verify(token);
            check = true;
        }
        catch (Exception e)
        {
            log.error(VERIFY_TOKEN_ERROR, e);
        }

        return Pair.of(check, decodedJWT);
    }
}
