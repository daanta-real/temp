package com.kh.hobbycloud.repository.notice;

import java.util.List;

import com.kh.hobbycloud.entity.notice.NoticeDto;
import com.kh.hobbycloud.entity.notice.NoticeVO;

public interface NoticeDao {
	List<NoticeVO>list();
	NoticeVO get(int noticeIdx);
	void insert(NoticeDto noticeDto);
	void delete(int noticeIdx);
	boolean edit(NoticeVO noticeVO);
	int getsequences();
	
	

}
