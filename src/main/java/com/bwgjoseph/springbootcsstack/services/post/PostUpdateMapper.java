package com.bwgjoseph.springbootcsstack.services.post;

import java.util.function.Supplier;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface PostUpdateMapper extends CommonUpdateMapper {

    // @Update({
    //     "<script>",
    //     "UPDATE POST",
    //     "<set>",
    //     "<foreach item='item' index='index' collection='p.entrySet()'>",
    //     "${index} = #{item},",
    //     "</foreach>",
    //     "</set>",
    //     "WHERE id = #{id}",
    //     "</script>"
    // })
    // public boolean update(@Param("id") Integer id, @Param("p") Map<String, Object> post);

    // @UpdateProvider(type=SQLUpdate.class, method = "update")
    // public boolean update(Integer id, Map<String, Object> post);

    // @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    // int update(UpdateStatementProvider updateStatement);

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, PostDynamicSqlSupport.post, completer);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(Post post, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(PostDynamicSqlSupport.title).equalToWhenPresent(validate(post))
            .set(PostDynamicSqlSupport.body).equalToWhenPresent(post::getBody)
            .set(PostDynamicSqlSupport.createdAt).equalToWhenPresent(post::getCreatedAt);
    }

    static Supplier<String> validate(Post post) {
        return () -> {
            if (post.getTitle().equalsIgnoreCase("")) return null;

            return post.getTitle();
            };
    }
}
