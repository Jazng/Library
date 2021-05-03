package com.self.library.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author Administrator
 * @Title: 请求提取器
 * @Description:
 * @Date 2021-04-11 13:59
 * @Version: 1.0
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    private final byte[] body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
        String sessionStream = getBodyContent(request);
        body = sessionStream.getBytes(StandardCharsets.UTF_8);
    }

    public String getBodyContent(final ServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try
        {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public InputStream cloneInputStream(ServletInputStream inputStream)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try
        {
            while ((len = inputStream.read(buffer)) > -1)
            {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return super.getReader();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {

            }

            @Override
            public int read() throws IOException
            {
                return bais.read();
            }
        };
    }
}
