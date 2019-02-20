package cc.ryanc.halo.web.controller.api;

import cc.ryanc.halo.exception.NotFoundException;
import cc.ryanc.halo.model.domain.Tag;
import cc.ryanc.halo.model.dto.JsonResult;
import cc.ryanc.halo.model.enums.ResponseStatusEnum;
import cc.ryanc.halo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     标签API
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2018/6/6
 */
@CrossOrigin
@RequestMapping(value = "/api/tags")
public class ApiTagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     *
     * <p>
     * result json:
     * <pre>
     * {
     *     "code": 200,
     *     "msg": "OK",
     *     "result": [
     *         {
     *             "tagId": ,
     *             "tagName": "",
     *             "tagUrl": ""
     *         }
     *     ]
     * }
     *     </pre>
     * </p>
     *
     * @return JsonResult
     */
    @GetMapping
    public JsonResult tags() {
        final List<Tag> tags = tagService.listAll();
        if (null != tags && tags.size() > 0) {
            return new JsonResult(ResponseStatusEnum.SUCCESS.getCode(), ResponseStatusEnum.SUCCESS.getMsg(), tags);
        } else {
            return new JsonResult(ResponseStatusEnum.EMPTY.getCode(), ResponseStatusEnum.EMPTY.getMsg());
        }
    }

    /**
     * 获取单个标签的信息
     *
     * <p>
     * result json:
     * <pre>
     * {
     *     "code": 200,
     *     "msg": "OK",
     *     "result": {
     *         "tagId": ,
     *         "tagName": "",
     *         "tagUrl": ""
     *     }
     * }
     *     </pre>
     * </p>
     *
     * @param tagUrl tagUrl
     * @return JsonResult
     */
    @GetMapping(value = "/{tagUrl}")
    public Tag tags(@PathVariable("tagUrl") String tagUrl) {
        final Tag tag = tagService.findByTagUrl(tagUrl);

        if (tag == null) {
            throw new NotFoundException("Tag with url: " + tagUrl + " was not found");
        }

        return tag;
    }
}
