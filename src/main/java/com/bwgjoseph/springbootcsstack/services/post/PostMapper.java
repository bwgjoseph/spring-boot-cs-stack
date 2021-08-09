package com.bwgjoseph.springbootcsstack.services.post;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper {
    @Select("SELECT * FROM POST")
    public List<Post> find();

    @Select("SELECT * FROM POST WHERE id = #{id}")
    public Post get(Integer id);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO POST(title, body, createdAt, createdBy, updatedAt, updatedBy) VALUES(#{title}, #{body}, #{createdAt}, #{createdBy}, #{updatedAt}, #{updatedBy})")
    public Integer create(Post post);

    @Update("UPDATE POST SET title = #{title}, body = #{body}, createdAt = #{createdAt}, createdBy = #{createdBy}, updatedAt = #{updatedAt}, updatedBy = #{updatedBy} WHERE id = #{id}")
    public boolean update(Post post);

    @Update("UPDATE POST SET title = #{p.title}, body = #{p.body}, createdAt = #{p.createdAt}, createdBy = #{p.createdBy}, updatedAt = #{p.updatedAt}, updatedBy = #{p.updatedBy} WHERE id = #{id}")
    public boolean updateById(@Param("id") Integer id, @Param("p") Post post);

    @Delete("DELETE FROM POST WHERE id = #{id}")
    public boolean delete(Integer id);
}
