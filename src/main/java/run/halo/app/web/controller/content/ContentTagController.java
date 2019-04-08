package run.halo.app.web.controller.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.vo.PostListVO;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostService;
import run.halo.app.service.TagService;
import run.halo.app.service.ThemeService;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Tag Controller
 *
 * @author : RYAN0UP
 * @date : 2019-03-21
 */
@Controller
@RequestMapping(value = "/tags")
public class ContentTagController {

    private final TagService tagService;

    private final PostService postService;

    private final OptionService optionService;

    private final ThemeService themeService;

    public ContentTagController(TagService tagService,
                                PostService postService,
                                OptionService optionService,
                                ThemeService themeService) {
        this.tagService = tagService;
        this.postService = postService;
        this.optionService = optionService;
        this.themeService = themeService;
    }

    /**
     * All of tags
     *
     * @return template path: themes/{theme}/tags
     */
    @GetMapping
    public String tags() {
        return themeService.render("tags");
    }

    /**
     * List tags by tag slug
     *
     * @param model    model
     * @param slugName slug name
     * @return template path: themes/{theme}/tag
     */
    @GetMapping(value = "{slugName}")
    public String tags(Model model,
                       @PathVariable("slugName") String slugName) {
        return this.tags(model, slugName, 1, Sort.by(DESC, "postDate"));
    }

    /**
     * List tags by tag slug
     *
     * @param model    model
     * @param slugName slug name
     * @param page     current page
     * @return template path: themes/{theme}/tag
     */
    @GetMapping(value = "{slugName}/page/{page}")
    public String tags(Model model,
                       @PathVariable("slugName") String slugName,
                       @PathVariable("page") Integer page,
                       @SortDefault(sort = "postDate", direction = DESC) Sort sort) {
        Tag tag = tagService.getBySlugNameOfNonNull(slugName);

        int size = optionService.getPostPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<PostListVO> posts;
        // TODO get posts by tag
        //final int[] rainbow = PageUtil.rainbow(page, posts.getTotalPages(), 3);
//        model.addAttribute("is_tags", true);
//        model.addAttribute("posts", posts);
//        model.addAttribute("rainbow", rainbow);
//        model.addAttribute("tag", tag);
        return themeService.render("tag");
    }
}
