package com.bwgjoseph.springbootcsstack.services.post;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

public class SQLUpdate {
    public String update(Integer id, Map<String, String> post) {

        StringBuilder sb = new StringBuilder();
        post.entrySet().stream().forEach(v -> sb.append(v.getKey() + " = '" + v.getValue() + "', "));
        String str = sb.toString().replaceAll(", $", "");

        return new SQL().UPDATE("POST").SET(str).WHERE("id = " + id).toString();
    }
}
