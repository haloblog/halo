package run.halo.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.annotation.DisableOnCondition;
import run.halo.app.model.support.BaseResponse;

/**
 * DisableApi注解测试类
 * @author guqing
 * @date 2020-02-14 17:51
 */
@RestController
@RequestMapping("/api/admin/test/disableOnCondition")
public class DisableOnConditionController {

    @GetMapping("/no")
    @DisableOnCondition
    public String blockAccess() {
        return "测试静止访问";
    }

    @GetMapping("/yes")
    public BaseResponse ableAccess() {
        return BaseResponse.ok("成功");
    }
}
