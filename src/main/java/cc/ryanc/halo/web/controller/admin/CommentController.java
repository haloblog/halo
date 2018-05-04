package cc.ryanc.halo.web.controller.admin;

import cc.ryanc.halo.model.domain.Comment;
import cc.ryanc.halo.model.domain.Post;
import cc.ryanc.halo.model.domain.User;
import cc.ryanc.halo.model.dto.HaloConst;
import cc.ryanc.halo.service.CommentService;
import cc.ryanc.halo.service.MailService;
import cc.ryanc.halo.service.PostService;
import cc.ryanc.halo.service.UserService;
import cc.ryanc.halo.util.HaloUtil;
import cc.ryanc.halo.web.controller.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : RYAN0UP
 * @date : 2017/12/10
 * @version : 1.0
 * description : 评论系统管理
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/comments")
public class CommentController extends BaseController{

    @Autowired
    private CommentService commentService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    /**
     * 渲染评论管理页面
     *
     * @param model model
     * @param status status 评论状态
     * @param page page 当前页码
     * @param size size 每页显示条数
     * @return 模板路径admin/admin_comment
     */
    @GetMapping
    public String comments(Model model,
                           @RequestParam(value = "status",defaultValue = "0") Integer status,
                           @RequestParam(value = "page",defaultValue = "0") Integer page,
                           @RequestParam(value = "size",defaultValue = "10") Integer size){
        Sort sort = new Sort(Sort.Direction.DESC,"commentDate");
        Pageable pageable = new PageRequest(page,size,sort);
        Page<Comment> comments = commentService.findAllComments(status,pageable);
        model.addAttribute("comments",comments);
        model.addAttribute("publicCount",commentService.findAllComments(0,pageable).getTotalElements());
        model.addAttribute("checkCount",commentService.findAllComments(1,pageable).getTotalElements());
        model.addAttribute("trashCount",commentService.findAllComments(2,pageable).getTotalElements());
        model.addAttribute("status",status);
        return "admin/admin_comment";
    }

    /**
     * 将评论移到回收站
     *
     * @param commentId 评论编号
     * @param status 评论状态
     * @return 重定向到/admin/comments
     */
    @GetMapping(value = "/throw")
    public String moveToTrash(@PathParam("commentId") Long commentId,
                              @PathParam("status") String status){
        try {
            commentService.updateCommentStatus(commentId,2);
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
        }
        return "redirect:/admin/comments?status="+status;
    }

    /**
     * 将评论改变为发布状态
     *
     * @param commentId 评论编号
     * @param status 评论状态
     * @param session session
     * @return 重定向到/admin/comments
     */
    @GetMapping("/revert")
    public String moveToPublish(@PathParam("commentId") Long commentId,
                                @PathParam("status") Integer status){
        Comment comment = commentService.updateCommentStatus(commentId,0);

        //判断评论者的邮箱是否符合规则
        Pattern patternEmail = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
        Matcher matcher = patternEmail.matcher(comment.getCommentAuthorEmail());

        //判断是否启用邮件服务
        if(StringUtils.equals(HaloConst.OPTIONS.get("smtp_email_enable"),"true") && StringUtils.equals(HaloConst.OPTIONS.get("comment_pass_notice"),"true")) {
            try {
                if (status == 1 && matcher.find()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("pageUrl", HaloConst.OPTIONS.get("blog_url")+"/archives/"+comment.getPost().getPostUrl()+"#comment-id-"+comment.getCommentId());
                    map.put("pageName", comment.getPost().getPostTitle());
                    map.put("commentContent", comment.getCommentContent());
                    map.put("blogUrl", HaloConst.OPTIONS.get("blog_url"));
                    map.put("blogTitle", HaloConst.OPTIONS.get("blog_title"));
                    map.put("author", userService.findUser().getUserDisplayName());
                    mailService.sendTemplateMail(
                            comment.getCommentAuthorEmail(),
                            "您在" + HaloConst.OPTIONS.get("blog_title") + "的评论已审核通过！", map, "common/mail/mail_passed.ftl");
                }
            } catch (Exception e) {
                log.error("邮件服务器未配置：{0}",e.getMessage());
            }
        }
        return "redirect:/admin/comments?status="+status;
    }

    /**
     * 删除评论
     *
     * @param commentId commentId 评论编号
     * @param status status 评论状态
     * @param session session session
     * @return string 重定向到/admin/comments
     */
    @GetMapping("/remove")
    public String moveToAway(@PathParam("commentId") Long commentId,
                             @PathParam("status") Integer status){
        try{
            commentService.removeByCommentId(commentId);
        }catch (Exception e){
            log.error("删除评论失败：{0}",e.getMessage());
        }
        return "redirect:/admin/comments?status="+status;
    }


    /**
     * 管理员回复评论
     *
     * @param commentId 被回复的评论
     * @param commentContent 回复的内容
     * @return 重定向到/admin/comments
     */
    @PostMapping("/reply")
    public String replyComment(@RequestParam("commentId") Long commentId,
                               @RequestParam("postId") Long postId,
                               @RequestParam("commentContent") String commentContent,
                               @RequestParam("userAgent") String userAgent,
                               HttpServletRequest request,
                               HttpSession session){
        try {
            Post post = postService.findByPostId(postId).get();

            //博主信息
            User user = (User) session.getAttribute(HaloConst.USER_SESSION_KEY);

            //被回复的评论
            Comment lastComment = commentService.findCommentById(commentId).get();

            //修改被回复的评论的状态
            lastComment.setCommentStatus(0);
            commentService.saveByComment(lastComment);

            //保存评论
            Comment comment = new Comment();
            comment.setPost(post);
            comment.setCommentAuthor(user.getUserDisplayName());
            comment.setCommentAuthorEmail(user.getUserEmail());
            comment.setCommentAuthorUrl(HaloConst.OPTIONS.get("blog_url"));
            comment.setCommentAuthorIp(HaloUtil.getIpAddr(request));
            comment.setCommentAuthorAvatarMd5(HaloUtil.getMD5(userService.findUser().getUserEmail()));
            comment.setCommentDate(new Date());
            String lastContent = " //<a href='#comment-id-"+lastComment.getCommentId()+"'>@"+lastComment.getCommentAuthor()+"</a>:"+lastComment.getCommentContent();
            comment.setCommentContent(commentContent+lastContent);
            comment.setCommentAgent(userAgent);
            comment.setCommentParent(commentId);
            comment.setCommentStatus(0);
            comment.setIsAdmin(1);
            commentService.saveByComment(comment);

            //正则表达式判断对方的邮箱是否是正确的格式
            Pattern patternEmail = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
            Matcher matcher = patternEmail.matcher(lastComment.getCommentAuthorEmail());

            //邮件通知
            if(StringUtils.equals(HaloConst.OPTIONS.get("smtp_email_enable"),"true") && StringUtils.equals(HaloConst.OPTIONS.get("comment_reply_notice"),"true")) {
                if(matcher.find()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("blogTitle",HaloConst.OPTIONS.get("blog_title"));
                    map.put("commentAuthor",lastComment.getCommentAuthor());
                    map.put("pageName",lastComment.getPost().getPostTitle());
                    map.put("pageUrl",HaloConst.OPTIONS.get("blog_url")+"/archives/"+post.getPostUrl()+"#comment-id-"+comment.getCommentId());
                    map.put("commentContent",lastComment.getCommentContent());
                    map.put("replyAuthor",user.getUserDisplayName());
                    map.put("replyContent",commentContent);
                    map.put("blogUrl",HaloConst.OPTIONS.get("blog_url"));
                    mailService.sendTemplateMail(
                            lastComment.getCommentAuthorEmail(),"您在"+HaloConst.OPTIONS.get("blog_title")+"的评论有了新回复",map,"common/mail/mail_reply.ftl");
                }
            }
        }catch (Exception e){
            log.error("回复评论失败！{0}",e.getMessage());
        }
        return "redirect:/admin/comments";
    }
}
