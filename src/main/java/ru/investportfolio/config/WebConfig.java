package ru.investportfolio.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.investportfolio.database.entity.CashAction;
import ru.investportfolio.database.entity.DealType;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCashActionConverter());
        registry.addConverter(new StringToDealTypeConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    public class StringToCashActionConverter implements Converter<String, CashAction> {
        @Override
        public CashAction convert(String source) {
            return CashAction.valueOf(source.toUpperCase());
        }
    }

    public class StringToDealTypeConverter implements Converter<String, DealType> {
        @Override
        public DealType convert(String source) {
            return DealType.valueOf(source.toUpperCase());
        }
    }
}
