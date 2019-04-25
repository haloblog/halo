package run.halo.app.model.dto.post;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.enums.PostCreateFrom;
import run.halo.app.model.enums.PostType;

/**
 * Page simple output dto.
 *
 * @author johnniang
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class PostSimpleDTO extends PostMinimalDTO {

    private PostType type;

    private String summary;

    private String thumbnail;

    private Long visits = 0L;

    private Boolean disallowComment;

    private String template;

    private Integer topPriority = 0;

    private PostCreateFrom createFrom;

    private Long likes = 0L;
}
