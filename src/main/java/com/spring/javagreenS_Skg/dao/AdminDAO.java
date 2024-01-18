package com.spring.javagreenS_Skg.dao;

import java.util.List;

import com.spring.javagreenS_Skg.vo.ChartVO;

public interface AdminDAO {

	public List<ChartVO> getRecentlyVisitCount();
}
