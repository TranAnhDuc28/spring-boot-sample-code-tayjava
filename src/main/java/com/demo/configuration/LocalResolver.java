package com.demo.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Multiple Language (i18n): cấu hình đa ngôn ngữ
 */

@Configuration
public class LocalResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    List<Locale> localeList = List.of(
            new Locale("en"),
            new Locale("fr"),
            new Locale("vi")
            );

    /**
     * @param request => lấy giá trị của header 'Accept-Language' từ HttpServletRequest request
     * @return Locale (vùng ngôn ngữ) dựa trên thông tin từ HttpServletRequest,nếu header Accept-Language không có,
     * ứng dụng sẽ sử dụng Locale mặc định của hệ thống.
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String languageHeader = request.getHeader("Accept-Language");
        return StringUtils.hasLength(languageHeader) ? // hasLength: check languageHeader có value hay không
                Locale.lookup( // tìm kiếm languageHeader có nằm trong list language không
                        Locale.LanguageRange.parse(languageHeader), // convert languageHeader => Locale
                        localeList) // list mã ngôn ngữ
                : Locale.US; // nếu languageHeader không có value nào thì lấy value default
    }


    /**
     * cấu hình xây dựng ứng dụng đa ngôn ngữ (internationalization - i18n)
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // Spring sẽ tìm các tệp tin thuộc tính bắt đầu với tên "messages" này.
        messageSource.setBasename("messages");
        // Đảm bảo rằng các tệp tin thuộc tính được đọc với mã hóa UTF-8
        // để đảm bảo rằng các ký tự đặc biệt và các ngôn ngữ khác ngoài tiếng Anh được xử lý chính xác.
        messageSource.setDefaultEncoding("UTF-8");
        // Các tệp tin thuộc tính sẽ được tải lại sau mỗi 3000 giây,
        // giúp cập nhật các thông báo mà không cần khởi động lại ứng dụng.
        messageSource.setCacheSeconds(3000);
        return messageSource;
    }
}
