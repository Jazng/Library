package com.self.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@ServletComponentScan("com.self.library.filter")
@EnableOpenApi
public class LibraryApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
