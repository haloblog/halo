package run.halo.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.Comment;
import run.halo.app.model.enums.CommentStatus;
import run.halo.app.model.projection.CommentCountProjection;
import run.halo.app.repository.base.BaseCommentRepository;

import java.util.List;

/**
 * Comment repository.
 *
 * @author johnniang
 * @date 3/21/19
 */
public interface CommentRepository extends BaseCommentRepository<Comment> {

    /**
     * Finds all comments by post ids.
     *
     * @param postIds post ids must not be null
     * @return a list of comment
     */
    @NonNull
    List<Comment> findAllByPostIdIn(@NonNull Iterable<Integer> postIds);

    /**
     * Finds all comments by post id.
     *
     * @param postId post id must not be null
     * @return a list of comment
     */
    @NonNull
    List<Comment> findAllByPostId(@NonNull Integer postId);

    /**
     * Counts comment count by post id collection.
     *
     * @param postIds post id collection must not be null
     * @return a list of comment count
     */
    @Query("select new run.halo.app.model.projection.CommentCountProjection(count(comment.id), comment.postId) from Comment comment where comment.postId in ?1 group by comment.postId")
    @NonNull
    List<CommentCountProjection> countByPostIds(@NonNull Iterable<Integer> postIds);

    /**
     * Finds comments by post id, comment status.
     *
     * @param postId post id must not be null
     * @param status comment status must not be null
     * @return a list of comment
     */
    List<Comment> findAllByPostIdAndStatus(Integer postId, CommentStatus status);

    /**
     * Finds comments by post id, comment status.
     *
     * @param postId   post id must not be null
     * @param status   comment status must not be null
     * @param pageable page info must not be null
     * @return a page of comment
     */
    Page<Comment> findAllByPostIdAndStatus(Integer postId, CommentStatus status, Pageable pageable);
}
