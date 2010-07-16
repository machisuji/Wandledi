package wandledi.example.models;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Markus Kahl
 */
@Entity
public class BlogEntry {

    @Id
    @GeneratedValue
    private Integer id;

    private String author;
    private String title;

    @Lob
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany
    private Collection<Comment> comments;

    public BlogEntry() {

        date = new Date();
    }

    public BlogEntry(String author, String title, String content) {

        this();
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public boolean validate() {

        return author != null && title != null && content != null;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the comments
     */
    public Collection<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }
}
