<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form method="post">

<div class="container-400 container-center">
	<div class="row center">
		<h2>비밀번호 확인</h2>
	</div>
	<div class="row flex-container">
		<input type="password" name="memberPw" placeholder="비밀번호 입력" required class="form-input">
		<input type="submit" value="회원탈퇴" class="form-btn">
	</div>
	
	<c:if test="${param.error != null}">
	<div class="row center">
		<h4 class="error">입력하신 정보가 일치하지 않습니다</h4>
	</div>
	</c:if>
</div>
	
</form>



