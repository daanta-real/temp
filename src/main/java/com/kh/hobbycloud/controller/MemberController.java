package com.kh.hobbycloud.controller;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.hobbycloud.entity.member.MemberDto;
import com.kh.hobbycloud.entity.member.MemberProfileDto;
import com.kh.hobbycloud.repository.member.MemberDao;
import com.kh.hobbycloud.repository.member.MemberProfileDao;
import com.kh.hobbycloud.service.member.MemberService;
import com.kh.hobbycloud.vo.member.MemberJoinVO;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberProfileDao memberProfileDao;

	// 1. 로그인 페이지 - 첫 페이지
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}

	// 2. 로그인 페이지 - 입력값이 넘어왔을 때 처리
	@PostMapping("/login")
	public String login(@ModelAttribute MemberDto memberDto, HttpSession session) {
		//회원정보 단일조회 및 비밀번호 일치판정
		MemberDto findDto = memberDao.login(memberDto);
		if(findDto != null) {
			//세션에 Idx, Id, Nick, grade를 설정하고 root로 리다이렉트
			session.setAttribute("memberIdx", findDto.getMemberIdx());
			session.setAttribute("memberId", findDto.getMemberId());
			session.setAttribute("memberNick", findDto.getMemberNick());
			session.setAttribute("memberGrade",findDto.getMemberGradeName());

			return "redirect:/";
		}
		else {
			return "redirect:login?error";//상대
		}
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("ses");
		session.removeAttribute("grade");
		//session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/join")
	public String join() {
		return "member/join";
	}

	@PostMapping("/join")
	public String join(@ModelAttribute MemberJoinVO memberJoinVO) throws IllegalStateException, IOException {
		memberService.join(memberJoinVO);
		
		return "redirect:join_success";
	}

	@RequestMapping("/join_success")
	public String joinSuccess() {
		return "member/join_success";
	}

//	/*
//	 * //내정보
//	 * 
//	 * @RequestMapping("/mypage") public String mypage(HttpSession session, Model
//	 * model) { int memberIdx = (int) session.getAttribute("ses"); MemberDto
//	 * memberDto = memberDao.get(memberIdx); MemberProfileDto memberProfileDto =
//	 * memberProfileDao.getMemberProfileIdx(memberIdx);
//	 * 
//	 * model.addAttribute("memberDto", memberDto);
//	 * model.addAttribute("memberProfileDto", memberProfileDto);
//	 * 
//	 * // return "/WEB-INF/views/member/mypage.jsp"; return "member/mypage"; }
//	 */

//	비밀번호 변경
	@GetMapping("/password")
	public String password() {
		return "member/password";
	}

	@PostMapping("/password")
	public String password(
			@RequestParam String memberPw,
			@RequestParam String changePw,
			HttpSession session) {
		String memberId = (String) session.getAttribute("ses");

		boolean result = memberDao.changePassword(memberId, memberPw, changePw);
		if(result) {
			return "redirect:password_success";
		}
		else {
			return "redirect:password?error";
		}
	}

	@RequestMapping("/password_success")
	public String passwordSuccess() {
		return "member/password_success";
	}

//	@GetMapping("/edit")
//	public String edit(HttpSession session, Model model) {
//		int memberIdx = (int) session.getAttribute("ses");
//		MemberDto memberDto = memberDao.get(memberIdx);
//
//		model.addAttribute("memberDto", memberDto);
//
//		return "member/edit";
//	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute MemberDto memberDto, HttpSession session) {
		String memberId = (String)session.getAttribute("ses");
		memberDto.setMemberId(memberId);

		boolean result = memberDao.changeInformation(memberDto);
		if(result) {
			return "redirect:edit_success";
		}
		else {
			return "redirect:edit?error";
		}
	}

	@RequestMapping("/edit_success")
	public String editSuccess() {
//		return "/WEB-INF/views/member/edit_success.jsp";
		return "member/edit_success";
	}

	@GetMapping("/quit")
	public String quit() {
//		return "/WEB-INF/views/member/quit.jsp";
		return "member/quit";
	}

	@PostMapping("/quit")
	public String quit(HttpSession session, @RequestParam String memberPw) {
		String memberId = (String)session.getAttribute("ses");

		boolean result = memberDao.quit(memberId, memberPw);
		if(result) {
			session.removeAttribute("ses");
			session.removeAttribute("grade");

			return "redirect:quit_success";
		}
		else {
			return "redirect:quit?error";
		}
	}

	@RequestMapping("/quit_success")
	public String quitSuccess() {
//		return "/WEB-INF/views/member/quit_success";
		return "member/quit_success";
	}


	@GetMapping("/profile")
	@ResponseBody
	public ResponseEntity<ByteArrayResource> profile(
				@RequestParam int memberProfileIdx
			) throws IOException {

		//프로필번호(memberProfileIdx)로 프로필 이미지 파일정보를 구한다.
		MemberProfileDto memberProfileDto = memberProfileDao.getMemberProfileIdx(memberProfileIdx);

		//프로필번호(memberProfileNo)로 실제 파일 정보를 불러온다
		byte[] data = memberProfileDao.load(memberProfileIdx);
		ByteArrayResource resource = new ByteArrayResource(data);

		String encodeName = URLEncoder.encode(memberProfileDto.getMemberProfileUploadname(), "UTF-8");
		encodeName = encodeName.replace("+", "%20");

		return ResponseEntity.ok()
									//.header("Content-Type", "application/octet-stream")
									.contentType(MediaType.APPLICATION_OCTET_STREAM)
									//.header("Content-Disposition", "attachment; filename=\""+이름+"\"")
									.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+encodeName+"\"")
									//.header("Content-Encoding", "UTF-8")
									.header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
									//.header("Content-Length", String.valueOf(memberProfileDto.getmemberProfileSize()))
									.contentLength(memberProfileDto.getMemberProfileSize())
								.body(resource);
	}
}















