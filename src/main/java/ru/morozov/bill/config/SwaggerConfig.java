package ru.morozov.bill.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.morozov.bill.controller.AccountController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${app.basePath:/}")
    private String basePath;

    @Autowired
    private BuildProperties buildProperties;

    @Bean
    public Docket api(ServletContext servletContext) {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);

        if (basePath != null) {
            docket
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return basePath;
                    }
                });
        }

        return (docket)
                .useDefaultResponseMessages(false)
                .select()
                .apis(Predicates.or(RequestHandlerSelectors.basePackage(AccountController.class.getPackage().getName())))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(this.apiInfo());
    }

    /**
     * Настройка информации по API
     * @return Настройка информации по API
     */
    protected ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                buildProperties.getName(),
                buildProperties.getName(),
                buildProperties.getVersion(),
                null,
                null,
                null,
                null,
                new ArrayList<>());
        return apiInfo;
    }
}