package fun.enou.alpha.service;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fun.enou.alpha.dto.dtodb.DtoDbDictDef;
import fun.enou.alpha.dto.dtodb.DtoDbDictWord;
import fun.enou.alpha.dto.dtoweb.DtoWebWord;
import fun.enou.alpha.msg.MsgEnum;
import fun.enou.alpha.repository.DictDefRepository;
import fun.enou.alpha.repository.DictWordRepository;
import fun.enou.core.msg.EnouMessageException;
import fun.enou.core.tool.RandomUtil;

@Service
public class TWordService implements IWordService{

	@Autowired
	private DictWordRepository wordRepository;
	
	@Autowired
	private DictDefRepository defRepository;

	@Override
	public void uploadWord(DtoWebWord webWord) {
		DtoDbDictWord dictWord = webWord.toDtoDbWord();
		List<DtoDbDictDef> dictDefList = webWord.toDtoDbDef();
		List<Integer> resultDefList = new LinkedList<>();
		for(DtoDbDictDef dictDef : dictDefList) {
			DtoDbDictDef result = defRepository.save(dictDef);
			resultDefList.add(result.getId());
		}
		
		ObjectMapper objMapper = new ObjectMapper();
		String idList = "";
		try {
			idList = objMapper.writeValueAsString(resultDefList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		dictWord.setDefId(idList);
		wordRepository.save(dictWord);
	}
	
	private DtoWebWord getWebWordByDictWord(DtoDbDictWord dbDictWord) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		List<Integer> defIdList = new LinkedList<Integer>();
		try {
			defIdList = objectMapper.readValue(dbDictWord.getDefId(), new TypeReference<List<Integer>>(){});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		Iterable<DtoDbDictDef> dictDefList =  defRepository.findAllById(defIdList);
		
		return new DtoWebWord(dbDictWord, dictDefList);
	}

	@Override
	public DtoWebWord getWebWord(String spell) throws EnouMessageException {

		DtoDbDictWord dbDictWord = wordRepository.findBySpell(spell);
		if(dbDictWord == null) {
			MsgEnum.WORD_NOT_FOUND.ThrowException();
		}
		
		return getWebWordByDictWord(dbDictWord);
	}

	@Override
	public List<DtoWebWord> getWebWordList(int count) throws EnouMessageException {
		
		List<DtoWebWord> resultList = new LinkedList<>();
		long wordCountLong = wordRepository.count();
		
		HashSet<Integer> wordIdSet = new HashSet<Integer>();
		while(wordIdSet.size() < count) {

			Integer id = (int) (RandomUtil.randomLong() % wordCountLong);
			if(!wordIdSet.contains(id))
				wordIdSet.add(id);
		}

		for(Integer id : wordIdSet) {
			Optional<DtoDbDictWord> dbDictWordOptional = wordRepository.findById(id);
			if(!dbDictWordOptional.isPresent())
				continue;
			
			DtoWebWord word = getWebWordByDictWord(dbDictWordOptional.get());
			resultList.add(word);
		}
		
		return resultList;
	}

}