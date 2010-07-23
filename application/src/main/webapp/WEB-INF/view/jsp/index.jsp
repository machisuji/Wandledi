<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="w" uri="/wandledi/taglib/core" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <link href="${css}" rel="stylesheet" type="text/css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Wandledi Blog</title>
    </head>
    <body>
        <a href="${homeLink}" class="homelink"><div id="top">Wandledi Blog</div></a>
        <br style="clear: left;"/>
        <div id="content">
            <div id="left">
                <c:forEach items="${entries}" var="entry">
                    <div class="entry">
                        <span class="heading">${entry.title}</span><br/>
                        <span class="subheading">
                            Posted by <span class="user">${entry.author}</span>
                            at <span class="date">${entry.date}</span>
                        </span>
                        <p class="text">
                            ${entry.content}
                        </p>
                        <span class="footer">
                            ${fn:length(entry.comments)}&nbsp;<a href="comments">comments</a>
                        </span>
                    </div>
                </c:forEach>
            </div>
            <div id="right">
                ${msg}
                <br/>
                <a href="${href}"><button style="background-color: white;">${label}</button></a>
            </div>
        </div>
    </body>
</html>