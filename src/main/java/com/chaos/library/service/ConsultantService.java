package com.chaos.library.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService(
    contentRetriever = "contentRetriever" //配置向量数据库检索对象
)
public interface ConsultantService {
    @SystemMessage("你是一个图书推荐 AI 助手，名字叫做小书，你现在拥有一个书籍信息的向量数据库，你需要根据该数据库中的数据为用户推荐书籍。")
    public String chat(String message);
}