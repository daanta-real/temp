package com.kh.hobbycloud.service.notice;

import java.io.IOException;

import com.kh.hobbycloud.vo.notice.NoticeVO;

public interface NoticeService {
	void insert(NoticeVO noticeVO) throws IllegalStateException, IOException;

}
