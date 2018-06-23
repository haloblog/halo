package cc.ryanc.halo.service.impl;

import cc.ryanc.halo.model.domain.Comment;
import cc.ryanc.halo.model.domain.Post;
import cc.ryanc.halo.repository.CommentRepository;
import cc.ryanc.halo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author : RYAN0UP
 * @date : 2018/1/22
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 新增评论
     *
     * @param comment comment
     */
    @Override
    public void saveByComment(Comment comment) {
        commentRepository.save(comment);
    }

    /**
     * 删除评论
     *
     * @param commentId commentId
     * @return Optional
     */
    @Override
    public Optional<Comment> removeByCommentId(Long commentId) {
        Optional<Comment> comment = this.findCommentById(commentId);
        commentRepository.delete(comment.get());
        return comment;
    }

    /**
     * 查询所有的评论，用于后台管理
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    public Page<Comment> findAllComments(Integer status, Pageable pageable) {
        return commentRepository.findCommentsByCommentStatus(status, pageable);
    }

    /**
     * 根据评论状态查询评论
     *
     * @param status 评论状态
     * @return List
     */
    @Override
    public List<Comment> findAllComments(Integer status) {
        return commentRepository.findCommentsByCommentStatus(status);
    }

    /**
     * 查询所有评论，不分页
     *
     * @return List<Comment></>
     */
    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    /**
     * 更改评论的状态
     *
     * @param commentId commentId
     * @param status    status
     * @return Comment
     */
    @Override
    public Comment updateCommentStatus(Long commentId, Integer status) {
        Optional<Comment> comment = findCommentById(commentId);
        comment.get().setCommentStatus(status);
        return commentRepository.save(comment.get());
    }

    /**
     * 根据评论编号查询评论
     *
     * @param commentId commentId
     * @return Optional
     */
    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    /**
     * 根据文章查询评论
     *
     * @param post     post
     * @param pageable pageable
     * @return Page
     */
    @Override
    public Page<Comment> findCommentsByPost(Post post, Pageable pageable) {
        return commentRepository.findCommentsByPost(post, pageable);
    }

    /**
     * 根据文章和评论状态查询评论
     *
     * @param post     post
     * @param pageable pageable
     * @param status   status
     * @return Page
     */
    @Override
    public Page<Comment> findCommentsByPostAndCommentStatus(Post post, Pageable pageable, Integer status) {
        return commentRepository.findCommentsByPostAndCommentStatus(post, pageable, status);
    }

    /**
     * 查询最新的前五条评论
     *
     * @return List
     */
    @Override
    public List<Comment> findCommentsLatest() {
        return commentRepository.findTopFive();
    }
}
