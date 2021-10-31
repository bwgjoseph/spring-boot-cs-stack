package com.bwgjoseph.springbootcsstack.services.post;

import java.sql.JDBCType;
import java.time.LocalDateTime;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PostDynamicSqlSupport {
    private PostDynamicSqlSupport() {}

    public static final Post post = new Post();
    public static final SqlColumn<Integer> id = post.id;
    public static final SqlColumn<String> title = post.title;
    public static final SqlColumn<String> body = post.body;
    public static final SqlColumn<LocalDateTime> createdAt = post.createdAt;
    public static final SqlColumn<String> createdBy = post.createdBy;
    public static final SqlColumn<LocalDateTime> updatedAt = post.updatedAt;
    public static final SqlColumn<String> updatedBy = post.updatedBy;

    public static final class Post extends SqlTable {
        public final SqlColumn<Integer> id = column("ID", JDBCType.INTEGER);
        public final SqlColumn<String> title = column("TITLE", JDBCType.VARCHAR);
        public final SqlColumn<String> body = column("BODY", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> createdAt = column("CREATEDAT", JDBCType.TIMESTAMP);
        public final SqlColumn<String> createdBy = column("CREATEDBY", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> updatedAt = column("UPDATEDAT", JDBCType.TIMESTAMP);
        public final SqlColumn<String> updatedBy = column("UPDATEDBY", JDBCType.VARCHAR);

        public Post() {
            super("Post");
        }
    }
}
