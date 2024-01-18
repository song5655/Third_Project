package com.spring.javagreenS_Skg.service;

import java.util.ArrayList;
import java.util.List;

import com.spring.javagreenS_Skg.vo.AsReplyVO;
import com.spring.javagreenS_Skg.vo.AsVO;
import com.spring.javagreenS_Skg.vo.NoticeVO;

public interface AsService {

	public void setAsInputOk(AsVO vo);

	public List<AsVO> getAsList(int startIndexNo, int pageSize);

	public AsVO getAsContent(int idx);

	public void imgDelete(String content);

	public void imgCheck(String content);

	public void setAsUpdate(AsVO vo);

	public void imgCheckUpdate(String content);

	public void setAsDelete(int idx);

	public String maxLevelOrder(int asIdx);

	public void setAsReplyInput(AsReplyVO replyVo);

	public ArrayList<AsReplyVO> getAsReply(int idx);

	public void setAsReplyDelete(int idx);

	public void setAsReplyUpdate(AsReplyVO replyVo);

	public void levelOrderPlusUpdate(AsReplyVO replyVo);

	public void setAsReplyInput2(AsReplyVO replyVo);

	public void swCheck(int idx);

	public ArrayList<AsVO> getAsBoardList(String mid, int startIndexNo, int pageSize);

	public void setAsDeleteReply(int idx);

	public List<AsVO> getAsSearch(int startIndexNo, int pageSize, String search, String searchString);

}
