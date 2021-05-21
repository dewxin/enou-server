package fun.enou.alpha.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fun.enou.alpha.dto.dtodb.DtoDbArticle;
import fun.enou.alpha.dto.dtoweb.DtoWebArticle;
import fun.enou.alpha.misc.SessionHolder;
import fun.enou.alpha.repository.ArticleRepository;
import fun.enou.alpha.repository.UserWordRepository;
import fun.enou.alpha.msg.MsgEnum;
import fun.enou.core.msg.EnouMessageException;
import redis.clients.jedis.Jedis;
import fun.enou.core.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: nagi
 * @Description: args(webUser) function's parameter password will be encoded in
 *               aspect and passed to the function
 * @Date Created in 2020-09-20 17:18
 * @Modified By:
 */
@Service
public class TArticleService implements IArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private IUserWordService userWordService;


	@Override
	public DtoWebArticle saveArticle(DtoWebArticle webArticle) {
		DtoDbArticle dbArticle = webArticle.toDtoDb();

		DtoDbArticle resDb = articleRepository.save(dbArticle);

		DtoWebArticle resWeb = resDb.toDtoWeb();
		return resWeb;
	}

	public List<DtoWebArticle> getArticles(Pageable pageable) {

		List<DtoWebArticle> returnValue = new ArrayList<>();

		List<DtoDbArticle> dbArticleList = articleRepository.findAll(pageable).getContent();

		dbArticleList.forEach(dtoDbArticle -> {
			returnValue.add(dtoDbArticle.toDtoWeb());
		});

		return returnValue;
	}

	public List<DtoWebArticle> getArticles(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		List<DtoWebArticle> result = this.getArticles(pageRequest);
		return result;
	}


	@Override
	public List<String> getUnknownWords(Long articleId) throws EnouMessageException {
		Optional<DtoDbArticle> articleOptional = articleRepository.findById(articleId);
		if(!articleOptional.isPresent()){
			MsgEnum.ARTICLE_NOT_FOUND.ThrowException();
		}
		
		DtoDbArticle dtoDbArticle = articleOptional.get();
		return userWordService.getUnknownWords(dtoDbArticle.getContent());
	}

}
