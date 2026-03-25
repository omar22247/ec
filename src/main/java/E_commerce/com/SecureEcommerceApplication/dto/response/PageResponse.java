package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

// Generic wrapper للـ pagination
// بدل ما كل response يعيد شكل مختلف للـ pagination
// بنوحّد الشكل: { content, page, size, totalElements, totalPages, last }
@Data
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    // Static factory method — بناءً على Page من Spring
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
