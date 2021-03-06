package com.bwgjoseph.springbootcsstack.services.post;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.executor.BatchResult;

@Mapper
public interface PostMapper {
    @Select("SELECT * FROM POST")
    public List<Post> find();

    @Select("SELECT * FROM POST WHERE id = #{id}")
    public Post get(Integer id);

    // Specifying `keyProperty` would set the `id` property to the `Post` object
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO POST(title, body, createdAt, createdBy, updatedAt, updatedBy) VALUES(#{title}, #{body}, #{createdAt}, #{createdBy}, #{updatedAt}, #{updatedBy})")
    // `Integer` that is returned is not the `id` of the inserted object but the number of inserted rows
    public Integer create(Post post);

    @Update("UPDATE POST SET title = #{title}, body = #{body}, createdAt = #{createdAt}, createdBy = #{createdBy}, updatedAt = #{updatedAt}, updatedBy = #{updatedBy} WHERE id = #{id}")
    public boolean update(Post post);

    @Update("UPDATE POST SET title = #{p.title}, body = #{p.body}, createdAt = #{p.createdAt}, createdBy = #{p.createdBy}, updatedAt = #{p.updatedAt}, updatedBy = #{p.updatedBy} WHERE id = #{id}")
    public boolean updateById(@Param("id") Integer id, @Param("p") Post post);

    @Delete("DELETE FROM POST WHERE id = #{id}")
    public boolean delete(Integer id);

    @Flush
    List<BatchResult> flush();
}
