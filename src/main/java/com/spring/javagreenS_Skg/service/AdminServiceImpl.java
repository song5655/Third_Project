package com.spring.javagreenS_Skg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.javagreenS_Skg.dao.AdminDAO;
import com.spring.javagreenS_Skg.vo.ChartVO;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	AdminDAO adminDAO;

	@Override
	public List<ChartVO> getRecentlyVisitCount() {
		return adminDAO.getRecentlyVisitCount();
	}
}
