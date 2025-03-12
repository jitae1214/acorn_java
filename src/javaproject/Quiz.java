package javaproject;

public class Quiz {
	String content; // 문제 내용
	String answer; // 문제 답
	
	// 생성자
	public Quiz(String content, String answer) {
		this.content = content;
		this.answer = answer;
	}
	// 지워도 됌
	public Quiz() {
		
	}
	
	// 답확인하는 메서드 
	boolean isCorrect(String answer) {
		return false;
	}
}
