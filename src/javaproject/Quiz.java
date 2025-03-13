package javaproject;

public class Quiz {
	String content; // 문제 내용
	String example; //문제 보기
	String answer; // 문제 답
	
	// 생성자
	public Quiz(String content, String example, String answer) {
		this.content = content;
		this.example = example;
		this.answer = answer;
	}
	public Quiz() {
		
	}
	
	
	public String getContent() {
		return content;
	}
	public String getExample() {
		return example;
	}
	public String getAnswer() {
		return answer;
	}
	
	// 답확인하는 메서드 
	boolean isCorrect(String userAnswer) {
	    return this.answer.equalsIgnoreCase(userAnswer.trim()); //대소문자, 앞뒤 공백 처리
	}
}
