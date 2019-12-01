package run.halo.app.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.PostMetaDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.post.BasePostDetailDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.Tag;

import java.util.List;
import java.util.Set;

/**
 * Post vo.
 *
 * @author johnniang
 * @author guqing
 * @date 2019-03-21
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDetailVO extends BasePostDetailDTO {

    private Set<Integer> tagIds;

    private List<TagDTO> tags;

    private Set<Integer> categoryIds;

    private List<CategoryDTO> categories;

    private Set<Long> postMetaIds;

    private List<PostMetaDTO> postMetas;
}

