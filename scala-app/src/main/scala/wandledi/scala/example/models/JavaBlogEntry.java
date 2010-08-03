package wandledi.scala.example.models;

import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
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
public class JavaBlogEntry {

    @Id
    @GeneratedValue
    private Long id;

    private String author;
    private String title;

    @Lob
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<Comment> comments;

    public JavaBlogEntry() {

        date = new Date();
    }

    public JavaBlogEntry(String author, String title, String content) {

        this();
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public boolean validate() {

        return ok(author) && ok(title) && ok(content);
    }

    private boolean ok(String field) {

        return field != null && !field.isEmpty();
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
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
}