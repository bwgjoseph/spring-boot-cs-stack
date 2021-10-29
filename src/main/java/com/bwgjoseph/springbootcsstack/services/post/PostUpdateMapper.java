package com.bwgjoseph.springbootcsstack.services.post;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostUpdateMapper {

    @Update({
        "<script>",
        "UPDATE POST",
        "<set>",
        "<foreach item='item' index='index' collection='p.entrySet()'>",
        "${index} = #{item},",
        "</foreach>",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    public boolean update(@Param("id") Integer id, @Param("p") Map<String, Object> post);

    // @UpdateProvider(type=SQLUpdate.class, method = "update")
    // public boolean update(Integer id, Map<String, Object> post);
}
