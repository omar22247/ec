package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long   id;
    private String name;
    private String slug;
    private Long   parentId;
    private String parentName;
    private List<CategoryResponse> subCategories;

    // constructor for JPQL (sub-categories only — no subCategories list)
    public CategoryResponse(Long id, String name, String slug,
                            Long parentId, String parentName) {
        this.id         = id;
        this.name       = name;
        this.slug       = slug;
        this.parentId   = parentId;
        this.parentName = parentName;
    }
}