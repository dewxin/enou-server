package fun.enou.alpha.service;

import fun.enou.alpha.dto.dtoweb.DtoWebArticle;
import fun.enou.core.msg.EnouMessageException;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    DtoWebArticle saveArticle(DtoWebArticle webArticle);
    List<DtoWebArticle> getArticles(Pageable pageable);
    List<DtoWebArticle> getArticles(int page, int size);

}
