package wandledi.example.models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Markus Kahl
 */
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String author;
    private String email;

    @Lob
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Comment() {

        date = new Date();
    }

    public Comment(String author, String email, String content) {

        this();
        this.author = author;
        this.email = email;
        this.content = content;
    }

    public boolean validate() {

        return ok(author) && ok(content); // email optional
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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
