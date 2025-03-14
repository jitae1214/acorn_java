package javaproject;

import java.util.Timer;
import java.util.TimerTask;

public class QuizTimer {
	Timer timer = new Timer(); //타이머 객체
	boolean timeUp = false; //시간 초과 표시 변수
	
	//타이머 시작 매서드
	public void startTimer(int timeLimit) {
		// TimerTask를 사용하여 시간 제한(timeLimit)이 경과하면 시간 초과 처리
		timer.schedule(new TimerTask() {
			//타이머가 끝난 후 실행될 작업을 정의
			public void run() {
				timeUp = true; //타이머가 종료되었음을 알리기 윟 true 저장
				System.out.println("\n시간 종료(엔터키를 눌러주세요)");
			}
		}, timeLimit);
	}
	
	//타이머 취소
	public void cancelTimer() {
		timer.cancel(); //현재 실행 중인 타이머를 취소
	}
	
	//시간이 초과 되었는지 확인
	public boolean isTimeUp() {
		return timeUp;
	}
	
}
